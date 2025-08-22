package pt.ama.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import pt.ama.dto.DocumentGenerationMessage;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Future;

@ApplicationScoped
public class DocumentKafkaProducer {
    
    private static final Logger LOG = Logger.getLogger(DocumentKafkaProducer.class);
    
    @ConfigProperty(name = "kafka.bootstrap.servers", defaultValue = "localhost:9092")
    String bootstrapServers;
    
    @ConfigProperty(name = "kafka.topic.document-generation", defaultValue = "document-generation")
    String documentGenerationTopic;
    
    @ConfigProperty(name = "kafka.topic.document-generation-dlq", defaultValue = "document-generation-dlq")
    String dlqTopic;
    
    @ConfigProperty(name = "kafka.producer.retries", defaultValue = "2147483647")
    int retries;
    
    @ConfigProperty(name = "kafka.producer.delivery.timeout.ms", defaultValue = "120000")
    int deliveryTimeout;
    
    @ConfigProperty(name = "kafka.producer.request.timeout.ms", defaultValue = "30000")
    int requestTimeout;
    
    @ConfigProperty(name = "kafka.producer.batch.size", defaultValue = "32768")
    int batchSize;
    
    @ConfigProperty(name = "kafka.producer.linger.ms", defaultValue = "5")
    int lingerMs;
    
    @Inject
    ObjectMapper objectMapper;
    
    @Inject
    MeterRegistry meterRegistry;
    
    private volatile KafkaProducer<String, String> producer;
    private final Object producerLock = new Object();
    
    private KafkaProducer<String, String> getProducer() {
        if (producer == null) {
            synchronized (producerLock) {
                if (producer == null) {
                    producer = createProducer();
                }
            }
        }
        return producer;
    }
    
    private KafkaProducer<String, String> createProducer() {
        Properties props = new Properties();
        
        // Connection settings
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        
        // Durability settings
        props.put("acks", "all");
        props.put("retries", retries);
        props.put("enable.idempotence", true);
        
        // Performance settings
        props.put("batch.size", batchSize);
        props.put("linger.ms", lingerMs);
        props.put("compression.type", "snappy");
        
        // Timeout settings
        props.put("delivery.timeout.ms", deliveryTimeout);
        props.put("request.timeout.ms", requestTimeout);
        props.put("retry.backoff.ms", 1000);
        
        // Buffer settings
        props.put("buffer.memory", 33554432);
        props.put("max.block.ms", 60000);
        
        LOG.info("Creating Kafka producer with configuration: " + props);
        return new KafkaProducer<>(props);
    }
    
    public String publishDocumentGenerationRequest(DocumentGenerationMessage message) {
        Timer.Sample sample = Timer.start(meterRegistry);
        String eventId = UUID.randomUUID().toString();
        
        try {
            message.setEventId(eventId);
            String messageJson = objectMapper.writeValueAsString(message);
            
            // Use eventId hash for consistent partitioning
            int partition = Math.abs(eventId.hashCode()) % getTopicPartitionCount();
            
            ProducerRecord<String, String> record = new ProducerRecord<>(
                documentGenerationTopic, 
                partition,
                eventId, 
                messageJson
            );
            
            Future<RecordMetadata> future = getProducer().send(record, (metadata, exception) -> {
                sample.stop(Timer.builder("kafka.producer.send.time")
                    .tag("topic", documentGenerationTopic)
                    .tag("success", String.valueOf(exception == null))
                    .register(meterRegistry));
                
                if (exception != null) {
                    LOG.error("Error sending message to Kafka for event: " + eventId, exception);
                    meterRegistry.counter("kafka.producer.send.errors",
                        "topic", documentGenerationTopic,
                        "error", exception.getClass().getSimpleName())
                        .increment();
                } else {
                    LOG.info("Message sent successfully for event: " + eventId + 
                            ", topic: " + metadata.topic() + 
                            ", partition: " + metadata.partition() + 
                            ", offset: " + metadata.offset());
                    meterRegistry.counter("kafka.producer.send.success",
                        "topic", documentGenerationTopic)
                        .increment();
                }
            });
            
            // Optional: Wait for send completion for critical messages
            // RecordMetadata metadata = future.get(5, TimeUnit.SECONDS);
            
            return eventId;
            
        } catch (JsonProcessingException e) {
            sample.stop(Timer.builder("kafka.producer.send.time")
                .tag("topic", documentGenerationTopic)
                .tag("success", "false")
                .register(meterRegistry));
            
            LOG.error("Error serializing message to JSON for event: " + eventId, e);
            meterRegistry.counter("kafka.producer.serialization.errors").increment();
            throw new RuntimeException("Failed to publish document generation request", e);
        } catch (Exception e) {
            sample.stop(Timer.builder("kafka.producer.send.time")
                .tag("topic", documentGenerationTopic)
                .tag("success", "false")
                .register(meterRegistry));
            
            LOG.error("Unexpected error publishing message for event: " + eventId, e);
            meterRegistry.counter("kafka.producer.unexpected.errors").increment();
            throw new RuntimeException("Failed to publish document generation request", e);
        }
    }
    
    public void sendToDLQ(String originalEventId, String originalMessage, String errorReason) {
        try {
            String dlqEventId = "DLQ-" + originalEventId;
            String dlqMessage = String.format("""
                {
                    "originalEventId": "%s",
                    "originalMessage": %s,
                    "errorReason": "%s",
                    "timestamp": "%s"
                }
                """, originalEventId, originalMessage, 
                errorReason.replace("\"", "\\\""), 
                java.time.Instant.now().toString());
            
            ProducerRecord<String, String> dlqRecord = new ProducerRecord<>(
                dlqTopic, 
                dlqEventId, 
                dlqMessage
            );
            
            getProducer().send(dlqRecord, (metadata, exception) -> {
                if (exception != null) {
                    LOG.error("Error sending message to DLQ for event: " + originalEventId, exception);
                } else {
                    LOG.warn("Message sent to DLQ for event: " + originalEventId + ", reason: " + errorReason);
                }
            });
            
            meterRegistry.counter("kafka.producer.dlq.messages",
                "original_topic", documentGenerationTopic)
                .increment();
                
        } catch (Exception e) {
            LOG.error("Failed to send message to DLQ for event: " + originalEventId, e);
            meterRegistry.counter("kafka.producer.dlq.errors").increment();
        }
    }
    
    private int getTopicPartitionCount() {
        // In a real implementation, you would query Kafka metadata
        // For now, assume 3 partitions
        return 3;
    }
    
    @PreDestroy
    public void close() {
        if (producer != null) {
            try {
                LOG.info("Closing Kafka producer...");
                producer.flush(); // Ensure all messages are sent
                producer.close();
                LOG.info("Kafka producer closed successfully");
            } catch (Exception e) {
                LOG.error("Error closing Kafka producer", e);
            }
        }
    }
}