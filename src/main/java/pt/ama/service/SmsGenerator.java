package pt.ama.service;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

@ApplicationScoped
public class SmsGenerator {
    public byte[] generate(String processedContent, Map<String, Object> metadata) {
        // TODO
        System.out.println("Generating SMS");
        System.out.println("Content: " + processedContent);
        System.out.println("Metadata: " + metadata);
        return ("SMS Body: " + processedContent).getBytes();
    }

    public byte[] generateSms(String processedContent, @NotNull(message = "Data is required") JsonNode data) {
        return null;
    }
}
