package pt.ama.service;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class EmailGenerator {

    public byte[] generate(String processedContent, Map<String, Object> metadata) {

        // TODO
        System.out.println("Generating Email:");
        System.out.println("Content: " + processedContent);
        System.out.println("Metadata: " + metadata);
        return ("Email Body: " + processedContent).getBytes();
    }
}
