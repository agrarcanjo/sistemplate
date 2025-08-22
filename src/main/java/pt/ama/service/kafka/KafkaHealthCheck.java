package pt.ama.service.kafka;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.TopicDescription;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.jboss.logging.Logger;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Readiness
@ApplicationScoped
public class KafkaHealthCheck implements HealthCheck {
    
    private static final Logger LOG = Logger.getLogger(KafkaHealthCheck.class);
    
    @ConfigProperty(name = "kafka.bootstrap.servers", defaultValue = "localhost:9092")
    String bootstrapServers;
    
    @ConfigProperty(name = "kafka.topic.document-generation", defaultValue = "document-generation")
    String documentGenerationTopic;
    
    @ConfigProperty(name = "kafka.health.check.timeout.seconds", defaultValue = "5")
    int healthCheckTimeoutSeconds;
    
    @Override
    public HealthCheckResponse call() {
        try {
            Properties props = new Properties();
            props.put("bootstrap.servers", bootstrapServers);
            props.put("request.timeout.ms", healthCheckTimeoutSeconds * 1000);
            
            try (AdminClient adminClient = AdminClient.create(props)) {
                // Check if we can describe the topic (this verifies connectivity)
                DescribeTopicsResult result = adminClient.describeTopics(
                    Collections.singletonList(documentGenerationTopic)
                );
                
                TopicDescription topicDescription = result.values()
                    .get(documentGenerationTopic)
                    .get(healthCheckTimeoutSeconds, TimeUnit.SECONDS);
                
                int partitionCount = topicDescription.partitions().size();
                
                return HealthCheckResponse.up("kafka")
                    .withData("bootstrap.servers", bootstrapServers)
                    .withData("topic", documentGenerationTopic)
                    .withData("partitions", partitionCount)
                    .withData("status", "connected")
                    .build();
                    
            }
        } catch (Exception e) {
            LOG.error("Kafka health check failed", e);
            return HealthCheckResponse.down("kafka")
                .withData("bootstrap.servers", bootstrapServers)
                .withData("topic", documentGenerationTopic)
                .withData("error", e.getMessage())
                .withData("status", "disconnected")
                .build();
        }
    }
}