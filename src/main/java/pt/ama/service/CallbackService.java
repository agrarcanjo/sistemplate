package pt.ama.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

@ApplicationScoped
public class CallbackService {
    
    private static final Logger LOG = Logger.getLogger(CallbackService.class);
    
    private final HttpClient httpClient;
    
    public CallbackService() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    }
    
    public void sendDocumentCallback(String callbackUrl, String eventId, byte[] documentBytes, 
                                   String filename, String contentType) {
        try {
            String base64Document = Base64.getEncoder().encodeToString(documentBytes);
            
            String jsonPayload = String.format("""
                {
                    "eventId": "%s",
                    "status": "SUCCESS",
                    "filename": "%s",
                    "contentType": "%s",
                    "document": "%s"
                }
                """, eventId, filename, contentType, base64Document);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(callbackUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .timeout(Duration.ofSeconds(60))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                LOG.info("Callback sent successfully for event: " + eventId + 
                        ", status: " + response.statusCode());
            } else {
                LOG.warn("Callback failed for event: " + eventId + 
                        ", status: " + response.statusCode() + 
                        ", response: " + response.body());
            }
            
        } catch (IOException | InterruptedException e) {
            LOG.error("Error sending callback for event: " + eventId, e);
        }
    }
    
    public void sendErrorCallback(String callbackUrl, String eventId, String errorMessage) {
        try {
            String jsonPayload = String.format("""
                {
                    "eventId": "%s",
                    "status": "ERROR",
                    "message": "%s"
                }
                """, eventId, errorMessage.replace("\"", "\\\""));
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(callbackUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .timeout(Duration.ofSeconds(60))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                LOG.info("Error callback sent successfully for event: " + eventId);
            } else {
                LOG.warn("Error callback failed for event: " + eventId + 
                        ", status: " + response.statusCode());
            }
            
        } catch (IOException | InterruptedException e) {
            LOG.error("Error sending error callback for event: " + eventId, e);
        }
    }
}