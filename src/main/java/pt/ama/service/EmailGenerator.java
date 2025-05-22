package pt.ama.service;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class EmailGenerator {

    public byte[] generate(String processedContent, Map<String, Object> metadata) {
        // Future implementation for email generation
        System.out.println("Generating Email (Future):");
        System.out.println("Content: " + processedContent);
        System.out.println("Metadata: " + metadata);
        return ("Email Body: " + processedContent).getBytes(); // Placeholder
    }
}
