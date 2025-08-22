package pt.ama.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import pt.ama.dto.DocumentGenerationMessage;
import pt.ama.dto.DocumentRequest;
import pt.ama.service.DocumentService;
import pt.ama.service.CallbackService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class DocumentKafkaConsumer {
    
    private static final Logger LOG = Logger.getLogger(DocumentKafkaConsumer.class);
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_BACKOFF_MS = 5000;
    
    @ConfigProperty(name = "kafka.bootstrap.servers", defaultValue = "localhost:9092")
    String bootstrapServers;
    
    @ConfigProperty(name = "kafka.topic.document-generation", defaultValue = "document-generation")
    String documentGenerationTopic;
    
    @ConfigProperty(name = "kafka.consumer.group.id", defaultValue = "sistemplate-document-consumer")
    String groupId;
    
    @ConfigProperty(name = "kafka.consumer.max.poll.records", defaultValue = "100")
    int maxPollRecords;
    
    @ConfigProperty(name = "kafka.consumer.max.poll.interval.ms", defaultValue = "300000")
    int maxPollIntervalMs;
    
    @ConfigProperty(name = "kafka.consumer.session.timeout.ms", defaultValue = "30000")
    int sessionTimeoutMs;
    
    @ConfigProperty(name = "kafka.consumer.poll.timeout.ms", defaultValue = "1000")
    long pollTimeoutMs;
    
    @ConfigProperty(name = "kafka.consumer.threads", defaultValue = "1")
    int consumerThreads;
    
    @Inject
    ObjectMapper objectMapper;
    
    @Inject
    DocumentService documentService;
    
    @Inject
    CallbackService callbackService;
    
    @Inject
    DocumentKafkaProducer kafkaProducer;
    
    @Inject
    MeterRegistry meterRegistry;
    
    private KafkaConsumer<String, String> consumer;
    private ExecutorService executorService;
    private volatile boolean running = false;
    private final AtomicInteger activeThreads = new AtomicInteger(0);
    
    @PostConstruct
    public void init() {
        Properties props = createConsumerProperties();
        
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(documentGenerationTopic));
        
        executorService = Executors.newFixedThreadPool(consumerThreads, r -> {
            Thread t = new Thread(r, "kafka-consumer-" + activeThreads.incrementAndGet());
            t.setDaemon(true);
            return t;
        });
        
        running = true;
        
        // Start consumer threads
        for (int i = 0; i < consumerThreads; i++) {
            executorService.submit(this::consumeMessages);
        }
        
        LOG.info("Kafka consumer initialized with " + consumerThreads + " threads");
        
        // Register metrics
        meterRegistry.gauge("kafka.consumer.active.threads", activeThreads);
    }
    
    private Properties createConsumerProperties() {
        Properties props = new Properties();
        
        // Connection settings
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", groupId);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        
        // Processing settings
        props.put("max.poll.records", maxPollRecords);
        props.put("max.poll.interval.ms", maxPollIntervalMs);
        props.put("session.timeout.ms", sessionTimeoutMs);
        
        // Offset management
        props.put("enable.auto.commit", false);
        props.put("auto.offset.reset", "earliest");
        
        // Performance settings
        props.put("fetch.min.bytes", 1024);
        props.put("fetch.max.wait.ms", 500);
        
        LOG.info("Consumer properties configured: " + props);
        return props;
    }
    
    private void consumeMessages() {
        String threadName = Thread.currentThread().getName();
        LOG.info("Starting consumer thread: " + threadName);
        
        try {
            while (running) {
                try {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(pollTimeoutMs));
                    
                    if (!records.isEmpty()) {
                        LOG.debug("Polled " + records.count() + " records in thread: " + threadName);
                        
                        for (ConsumerRecord<String, String> record : records) {
                            processDocumentGeneration(record);
                        }
                        
                        // Manual commit after processing all records
                        consumer.commitSync();
                        
                        meterRegistry.counter("kafka.consumer.records.processed",
                            "topic", documentGenerationTopic)
                            .increment(records.count());
                    }
                    
                } catch (WakeupException e) {
                    LOG.info("Consumer wakeup received in thread: " + threadName);
                    break;
                } catch (Exception e) {
                    LOG.error("Error consuming messages from Kafka in thread: " + threadName, e);
                    meterRegistry.counter("kafka.consumer.errors",
                        "topic", documentGenerationTopic,
                        "error", e.getClass().getSimpleName())
                        .increment();
                    
                    handleConsumerError(e);
                }
            }
        } finally {
            activeThreads.decrementAndGet();
            LOG.info("Consumer thread stopped: " + threadName);
        }
    }
    
    private void handleConsumerError(Exception e) {
        try {
            LOG.warn("Consumer error occurred, waiting " + RETRY_BACKOFF_MS + "ms before retry");
            Thread.sleep(RETRY_BACKOFF_MS);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            running = false;
        }
    }
    
    public void processDocumentGeneration(ConsumerRecord<String, String> record) {
        Timer.Sample sample = Timer.start(meterRegistry);
        String eventId = record.key();
        
        try {
            LOG.info("Processing document generation message with key: " + eventId);
            
            DocumentGenerationMessage message = objectMapper.readValue(
                record.value(), 
                DocumentGenerationMessage.class
            );
            
            // Validate message
            if (message.getTemplateName() == null || message.getData() == null) {
                throw new IllegalArgumentException("Invalid message: missing templateName or data");
            }
            
            // Convert message to DocumentRequest
            DocumentRequest request = createDocumentRequest(message);
            
            // Generate document with retry logic
            byte[] documentBytes = generateDocumentWithRetry(request, eventId);
            
            // Send callback with the generated document
            if (message.getCallbackUrl() != null && !message.getCallbackUrl().isEmpty()) {
                sendCallbackWithRetry(message, documentBytes, request, eventId);
            }
            
            sample.stop(Timer.builder("kafka.consumer.message.processing.time")
                .tag("topic", documentGenerationTopic)
                .tag("success", "true")
                .register(meterRegistry));
            
            meterRegistry.counter("kafka.consumer.messages.success",
                "topic", documentGenerationTopic)
                .increment();
            
            LOG.info("Document generation completed successfully for event: " + eventId);
            
        } catch (Exception e) {
            sample.stop(Timer.builder("kafka.consumer.message.processing.time")
                .tag("topic", documentGenerationTopic)
                .tag("success", "false")
                .register(meterRegistry));
            
            meterRegistry.counter("kafka.consumer.messages.failed",
                "topic", documentGenerationTopic,
                "error", e.getClass().getSimpleName())
                .increment();
            
            LOG.error("Error processing document generation message for event: " + eventId, e);
            
            // Send to DLQ after max retries
            kafkaProducer.sendToDLQ(eventId, record.value(), e.getMessage());
            
            // Try to send error callback
            sendErrorCallback(record, e);
        }
    }
    
    private DocumentRequest createDocumentRequest(DocumentGenerationMessage message) {
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName(message.getTemplateName());
        request.setData(message.getData());
        request.setReceiver(message.getReceiver());
        request.setCallbackUrl(message.getCallbackUrl());
        request.setOptions(message.getOptions());
        return request;
    }
    
    private byte[] generateDocumentWithRetry(DocumentRequest request, String eventId) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                LOG.debug("Document generation attempt " + attempt + " for event: " + eventId);
                return documentService.generateDocument(request);
            } catch (Exception e) {
                lastException = e;
                LOG.warn("Document generation attempt " + attempt + " failed for event: " + eventId, e);
                
                if (attempt < MAX_RETRY_ATTEMPTS) {
                    try {
                        Thread.sleep(RETRY_BACKOFF_MS * attempt); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted during retry", ie);
                    }
                }
            }
        }
        
        throw new RuntimeException("Failed to generate document after " + MAX_RETRY_ATTEMPTS + " attempts", lastException);
    }
    
    private void sendCallbackWithRetry(DocumentGenerationMessage message, byte[] documentBytes, 
                                     DocumentRequest request, String eventId) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                callbackService.sendDocumentCallback(
                    message.getCallbackUrl(),
                    message.getEventId(),
                    documentBytes,
                    documentService.buildFilename(request),
                    documentService.getContentTypeByTemplate(request.getTemplateName())
                );
                
                meterRegistry.counter("kafka.consumer.callbacks.success").increment();
                return; // Success, exit retry loop
                
            } catch (Exception e) {
                lastException = e;
                LOG.warn("Callback attempt " + attempt + " failed for event: " + eventId, e);
                
                if (attempt < MAX_RETRY_ATTEMPTS) {
                    try {
                        Thread.sleep(RETRY_BACKOFF_MS * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted during callback retry", ie);
                    }
                }
            }
        }
        
        meterRegistry.counter("kafka.consumer.callbacks.failed").increment();
        LOG.error("Failed to send callback after " + MAX_RETRY_ATTEMPTS + " attempts for event: " + eventId, lastException);
    }
    
    private void sendErrorCallback(ConsumerRecord<String, String> record, Exception error) {
        try {
            DocumentGenerationMessage message = objectMapper.readValue(
                record.value(), 
                DocumentGenerationMessage.class
            );
            
            if (message.getCallbackUrl() != null && !message.getCallbackUrl().isEmpty()) {
                callbackService.sendErrorCallback(
                    message.getCallbackUrl(),
                    message.getEventId(),
                    "Error generating document: " + error.getMessage()
                );
            }
        } catch (Exception callbackError) {
            LOG.error("Error sending error callback for event: " + record.key(), callbackError);
        }
    }
    
    @PreDestroy
    public void shutdown() {
        LOG.info("Shutting down Kafka consumer...");
        running = false;
        
        if (consumer != null) {
            consumer.wakeup(); // Interrupt poll() calls
        }
        
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    LOG.warn("Consumer threads did not terminate within 30 seconds, forcing shutdown");
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                LOG.warn("Interrupted while waiting for consumer threads to terminate");
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        if (consumer != null) {
            try {
                consumer.close(Duration.ofSeconds(10));
                LOG.info("Kafka consumer closed successfully");
            } catch (Exception e) {
                LOG.error("Error closing Kafka consumer", e);
            }
        }
    }
}