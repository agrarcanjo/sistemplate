package pt.ama.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class CallbackService {
    
    private static final Logger LOG = Logger.getLogger(CallbackService.class);
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_BACKOFF_MS = 2000;
    
    @ConfigProperty(name = "callback.timeout.seconds", defaultValue = "60")
    int callbackTimeoutSeconds;
    
    @ConfigProperty(name = "callback.connect.timeout.seconds", defaultValue = "30")
    int connectTimeoutSeconds;
    
    @ConfigProperty(name = "callback.retry.attempts", defaultValue = "3")
    int maxRetryAttempts;
    
    @ConfigProperty(name = "callback.retry.backoff.ms", defaultValue = "2000")
    long retryBackoffMs;
    
    @Inject
    MeterRegistry meterRegistry;
    
    private final HttpClient httpClient;
    
    public CallbackService() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    }
    
    public void sendDocumentCallback(String callbackUrl, String eventId, byte[] documentBytes, 
                                   String filename, String contentType) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            String base64Document = Base64.getEncoder().encodeToString(documentBytes);
            
            String jsonPayload = String.format("""
                {
                    "eventId": "%s",
                    "status": "SUCCESS",
                    "filename": "%s",
                    "contentType": "%s",
                    "document": "%s",
                    "timestamp": "%s"
                }
                """, eventId, filename, contentType, base64Document, 
                java.time.Instant.now().toString());
            
            boolean success = sendCallbackWithRetry(callbackUrl, jsonPayload, eventId, "document");
            
            sample.stop(Timer.builder("callback.send.time")
                .tag("type", "document")
                .tag("success", String.valueOf(success))
                .register(meterRegistry));
            
            if (success) {
                meterRegistry.counter("callback.document.success").increment();
            } else {
                meterRegistry.counter("callback.document.failed").increment();
            }
            
        } catch (Exception e) {
            sample.stop(Timer.builder("callback.send.time")
                .tag("type", "document")
                .tag("success", "false")
                .register(meterRegistry));
            
            LOG.error("Unexpected error sending document callback for event: " + eventId, e);
            meterRegistry.counter("callback.document.errors",
                "error", e.getClass().getSimpleName())
                .increment();
        }
    }
    
    public void sendErrorCallback(String callbackUrl, String eventId, String errorMessage) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            String jsonPayload = String.format("""
                {
                    "eventId": "%s",
                    "status": "ERROR",
                    "message": "%s",
                    "timestamp": "%s"
                }
                """, eventId, errorMessage.replace("\"", "\\\""), 
                java.time.Instant.now().toString());
            
            boolean success = sendCallbackWithRetry(callbackUrl, jsonPayload, eventId, "error");
            
            sample.stop(Timer.builder("callback.send.time")
                .tag("type", "error")
                .tag("success", String.valueOf(success))
                .register(meterRegistry));
            
            if (success) {
                meterRegistry.counter("callback.error.success").increment();
            } else {
                meterRegistry.counter("callback.error.failed").increment();
            }
            
        } catch (Exception e) {
            sample.stop(Timer.builder("callback.send.time")
                .tag("type", "error")
                .tag("success", "false")
                .register(meterRegistry));
            
            LOG.error("Unexpected error sending error callback for event: " + eventId, e);
            meterRegistry.counter("callback.error.errors",
                "error", e.getClass().getSimpleName())
                .increment();
        }
    }
    
    private boolean sendCallbackWithRetry(String callbackUrl, String jsonPayload, 
                                        String eventId, String callbackType) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxRetryAttempts; attempt++) {
            try {
                LOG.debug("Sending " + callbackType + " callback attempt " + attempt + 
                         " for event: " + eventId + " to: " + callbackUrl);
                
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(callbackUrl))
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "sistemplate-callback/1.0")
                    .header("X-Event-Id", eventId)
                    .header("X-Callback-Type", callbackType)
                    .header("X-Attempt", String.valueOf(attempt))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .timeout(Duration.ofSeconds(callbackTimeoutSeconds))
                    .build();
                
                HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
                
                if (isSuccessResponse(response.statusCode())) {
                    LOG.info("Callback sent successfully for event: " + eventId + 
                            ", type: " + callbackType + 
                            ", attempt: " + attempt + 
                            ", status: " + response.statusCode());
                    
                    meterRegistry.counter("callback.attempts.success",
                        "type", callbackType,
                        "attempt", String.valueOf(attempt))
                        .increment();
                    
                    return true;
                } else {
                    String errorMsg = "Callback failed with HTTP " + response.statusCode() + 
                                    " for event: " + eventId + ", response: " + response.body();
                    LOG.warn(errorMsg);
                    lastException = new RuntimeException(errorMsg);
                    
                    meterRegistry.counter("callback.attempts.http_error",
                        "type", callbackType,
                        "status", String.valueOf(response.statusCode()),
                        "attempt", String.valueOf(attempt))
                        .increment();
                }
                
            } catch (IOException | InterruptedException e) {
                lastException = e;
                LOG.warn("Callback attempt " + attempt + " failed for event: " + eventId + 
                        ", type: " + callbackType, e);
                
                meterRegistry.counter("callback.attempts.network_error",
                    "type", callbackType,
                    "error", e.getClass().getSimpleName(),
                    "attempt", String.valueOf(attempt))
                    .increment();
                
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            // Wait before retry (except for last attempt)
            if (attempt < maxRetryAttempts) {
                try {
                    long backoffTime = retryBackoffMs * attempt; // Linear backoff
                    LOG.debug("Waiting " + backoffTime + "ms before retry for event: " + eventId);
                    Thread.sleep(backoffTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOG.warn("Interrupted during callback retry backoff for event: " + eventId);
                    break;
                }
            }
        }
        
        LOG.error("Failed to send " + callbackType + " callback after " + maxRetryAttempts + 
                 " attempts for event: " + eventId, lastException);
        
        meterRegistry.counter("callback.final.failure",
            "type", callbackType)
            .increment();
        
        return false;
    }
    
    public CompletableFuture<Boolean> sendDocumentCallbackAsync(String callbackUrl, String eventId, 
                                                              byte[] documentBytes, String filename, 
                                                              String contentType) {
        return CompletableFuture.supplyAsync(() -> {
            sendDocumentCallback(callbackUrl, eventId, documentBytes, filename, contentType);
            return true;
        }).exceptionally(throwable -> {
            LOG.error("Async document callback failed for event: " + eventId, throwable);
            return false;
        });
    }
    
    public CompletableFuture<Boolean> sendErrorCallbackAsync(String callbackUrl, String eventId, 
                                                           String errorMessage) {
        return CompletableFuture.supplyAsync(() -> {
            sendErrorCallback(callbackUrl, eventId, errorMessage);
            return true;
        }).exceptionally(throwable -> {
            LOG.error("Async error callback failed for event: " + eventId, throwable);
            return false;
        });
    }
    
    private boolean isSuccessResponse(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }
    
    public void shutdown() {
        // HttpClient doesn't need explicit shutdown in newer Java versions
        // but we can add cleanup logic here if needed
        LOG.info("CallbackService shutdown completed");
    }
}