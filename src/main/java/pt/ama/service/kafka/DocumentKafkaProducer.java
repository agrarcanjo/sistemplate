package pt.ama.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import pt.ama.dto.DocumentGenerationMessage;

import java.util.Properties;
import java.util.UUID;

@ApplicationScoped
public class DocumentKafkaProducer {
    
    private static final Logger LOG = Logger.getLogger(DocumentKafkaProducer.class);
    
    @ConfigProperty(name = "kafka.bootstrap.servers", defaultValue = "localhost:9092")
    String bootstrapServers;
    
    @ConfigProperty(name = "kafka.topic.document-generation", defaultValue = "document-generation")
    String documentGenerationTopic;
    
    @Inject
    ObjectMapper objectMapper;
    
    private KafkaProducer<String, String> producer;
    
    public void init() {
        if (producer == null) {
            Properties props = new Properties();
            props.put("bootstrap.servers", bootstrapServers);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("acks", "all");
            props.put("retries", 3);
            props.put("batch.size", 16384);
            props.put("linger.ms", 1);
            props.put("buffer.memory", 33554432);
            
            producer = new KafkaProducer<>(props);
        }
    }
    
    public String publishDocumentGenerationRequest(DocumentGenerationMessage message) {
        try {
            init();
            
            String eventId = UUID.randomUUID().toString();
            message.setEventId(eventId);
            
            String messageJson = objectMapper.writeValueAsString(message);
            
            ProducerRecord<String, String> record = new ProducerRecord<>(
                documentGenerationTopic, 
                eventId, 
                messageJson
            );
            
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    LOG.error("Error sending message to Kafka", exception);
                } else {
                    LOG.info("Message sent successfully to topic: " + metadata.topic() + 
                            ", partition: " + metadata.partition() + 
                            ", offset: " + metadata.offset());
                }
            });
            
            return eventId;
            
        } catch (JsonProcessingException e) {
            LOG.error("Error serializing message to JSON", e);
            throw new RuntimeException("Failed to publish document generation request", e);
        }
    }
    
    public void close() {
        if (producer != null) {
            producer.close();
        }
    }
}