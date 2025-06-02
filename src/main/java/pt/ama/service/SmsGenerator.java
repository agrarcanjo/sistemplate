package pt.ama.service;

import jakarta.enterprise.context.ApplicationScoped;
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
}
