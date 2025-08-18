package pt.ama.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import pt.ama.dto.DocumentGenerationMessage;
import pt.ama.dto.DocumentRequest;
import pt.ama.mapper.DocumentRequestMapper;
import pt.ama.service.DocumentService;
import pt.ama.service.CallbackService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class DocumentKafkaConsumer {
    
    private static final Logger LOG = Logger.getLogger(DocumentKafkaConsumer.class);
    
    @ConfigProperty(name = "kafka.bootstrap.servers", defaultValue = "localhost:9092")
    String bootstrapServers;
    
    @ConfigProperty(name = "kafka.topic.document-generation", defaultValue = "document-generation")
    String documentGenerationTopic;
    
    @Inject
    ObjectMapper objectMapper;
    
    @Inject
    DocumentService documentService;

    @Inject
    DocumentRequestMapper documentRequestMapper;
    
    @Inject
    CallbackService callbackService;
    
    private KafkaConsumer<String, String> consumer;
    private ExecutorService executorService;
    private volatile boolean running = false;
    
    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", "sistemplate-document-consumer");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "earliest");
        props.put("enable.auto.commit", "true");
        
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(documentGenerationTopic));
        
        executorService = Executors.newSingleThreadExecutor();
        running = true;
        
        executorService.submit(this::consumeMessages);
        
        LOG.info("Kafka consumer initialized and started");
    }
    
    private void consumeMessages() {
        while (running) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, String> record : records) {
                    processDocumentGeneration(record);
                }
                
            } catch (Exception e) {
                LOG.error("Error consuming messages from Kafka", e);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    
    public void processDocumentGeneration(ConsumerRecord<String, String> record) {
        try {
            LOG.info("Processing document generation message with key: " + record.key());
            
            DocumentGenerationMessage message = objectMapper.readValue(
                record.value(), 
                DocumentGenerationMessage.class
            );

            DocumentRequest request = documentRequestMapper.toDocumentRequest(message);

            byte[] documentBytes = documentService.generateDocument(request);

            // Enviar callback com o documento gerado
            if (message.getCallbackUrl() != null && !message.getCallbackUrl().isEmpty()) {
                callbackService.sendDocumentCallback(
                    message.getCallbackUrl(),
                    message.getEventId(),
                    documentBytes,
                    documentService.buildFilename(request),
                    documentService.getContentTypeByTemplate(request.getTemplateName())
                );
            }
            
            LOG.info("Document generation completed for event: " + message.getEventId());
            
        } catch (Exception e) {
            LOG.error("Error processing document generation message", e);

            // Em caso de erro, tentar enviar callback de erro
            try {
                DocumentGenerationMessage message = objectMapper.readValue(
                    record.value(), 
                    DocumentGenerationMessage.class
                );
                
                if (message.getCallbackUrl() != null && !message.getCallbackUrl().isEmpty()) {
                    callbackService.sendErrorCallback(
                        message.getCallbackUrl(),
                        message.getEventId(),
                        "Error generating document: " + e.getMessage()
                    );
                }
            } catch (Exception callbackError) {
                LOG.error("Error sending error callback", callbackError);
            }
        }
    }
    
    @PreDestroy
    public void shutdown() {
        running = false;
        if (consumer != null) {
            consumer.close();
        }
        if (executorService != null) {
            executorService.shutdown();
        }
        LOG.info("Kafka consumer shutdown completed");
    }
}