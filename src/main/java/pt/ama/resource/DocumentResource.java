package pt.ama.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import pt.ama.dto.DocumentRequest;
import pt.ama.dto.AsyncDocumentResponse;
import pt.ama.service.DocumentService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Path("/documents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DocumentResource {
    
    private static final Logger LOG = Logger.getLogger(DocumentResource.class);
    
    @Inject
    DocumentService documentService;
    
    @Inject
    MeterRegistry meterRegistry;
    
    @POST
    @Path("/generate")
    public Response generateDocument(@Valid DocumentRequest request) {
        Timer.Sample sample = Timer.start(meterRegistry);
        String processingMode = request.isAsync() ? "async" : "sync";
        
        try {
            LOG.info("Received document generation request - Template: " + request.getTemplateName() + 
                    ", Mode: " + processingMode);
            
            Object result = documentService.processDocumentRequest(request);
            
            sample.stop(Timer.builder("document.generation.request.time")
                .tag("mode", processingMode)
                .tag("template", request.getTemplateName())
                .tag("success", "true")
                .register(meterRegistry));
            
            if (result instanceof AsyncDocumentResponse) {
                // Processamento assíncrono - retorna 202 Accepted
                meterRegistry.counter("document.generation.requests",
                    "mode", "async",
                    "template", request.getTemplateName(),
                    "status", "accepted")
                    .increment();
                
                return Response.status(Response.Status.ACCEPTED)
                    .entity(result)
                    .header("X-Processing-Mode", "async")
                    .header("X-Event-Id", ((AsyncDocumentResponse) result).getEventId())
                    .build();
            } else {
                // Processamento síncrono - retorna o documento
                meterRegistry.counter("document.generation.requests",
                    "mode", "sync",
                    "template", request.getTemplateName(),
                    "status", "completed")
                    .increment();
                
                return Response.ok(result)
                    .header("X-Processing-Mode", "sync")
                    .build();
            }
            
        } catch (IllegalArgumentException e) {
            sample.stop(Timer.builder("document.generation.request.time")
                .tag("mode", processingMode)
                .tag("template", request.getTemplateName())
                .tag("success", "false")
                .register(meterRegistry));
            
            LOG.warn("Invalid request for template: " + request.getTemplateName(), e);
            meterRegistry.counter("document.generation.requests",
                "mode", processingMode,
                "template", request.getTemplateName(),
                "status", "bad_request")
                .increment();
            
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(createErrorResponse("INVALID_REQUEST", e.getMessage()))
                .build();
                
        } catch (RuntimeException e) {
            sample.stop(Timer.builder("document.generation.request.time")
                .tag("mode", processingMode)
                .tag("template", request.getTemplateName())
                .tag("success", "false")
                .register(meterRegistry));
            
            LOG.error("Error generating document for template: " + request.getTemplateName(), e);
            meterRegistry.counter("document.generation.requests",
                "mode", processingMode,
                "template", request.getTemplateName(),
                "status", "error")
                .increment();
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(createErrorResponse("GENERATION_ERROR", "Error generating document: " + e.getMessage()))
                .build();
                
        } catch (Exception e) {
            sample.stop(Timer.builder("document.generation.request.time")
                .tag("mode", processingMode)
                .tag("template", request.getTemplateName())
                .tag("success", "false")
                .register(meterRegistry));
            
            LOG.error("Unexpected error generating document for template: " + request.getTemplateName(), e);
            meterRegistry.counter("document.generation.requests",
                "mode", processingMode,
                "template", request.getTemplateName(),
                "status", "unexpected_error")
                .increment();
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(createErrorResponse("UNEXPECTED_ERROR", "An unexpected error occurred"))
                .build();
        }
    }
    
    @POST
    @Path("/generate/async")
    public Response generateDocumentAsync(@Valid DocumentRequest request) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            LOG.info("Received async document generation request - Template: " + request.getTemplateName());
            
            // Força processamento assíncrono
            request.setAsync(true);
            
            AsyncDocumentResponse response = documentService.generateDocumentAsync(request);
            
            sample.stop(Timer.builder("document.generation.async.request.time")
                .tag("template", request.getTemplateName())
                .tag("success", "true")
                .register(meterRegistry));
            
            meterRegistry.counter("document.generation.async.requests",
                "template", request.getTemplateName(),
                "status", "accepted")
                .increment();
            
            return Response.status(Response.Status.ACCEPTED)
                .entity(response)
                .header("X-Processing-Mode", "async")
                .header("X-Event-Id", response.getEventId())
                .build();
                
        } catch (IllegalArgumentException e) {
            sample.stop(Timer.builder("document.generation.async.request.time")
                .tag("template", request.getTemplateName())
                .tag("success", "false")
                .register(meterRegistry));
            
            LOG.warn("Invalid async request for template: " + request.getTemplateName(), e);
            meterRegistry.counter("document.generation.async.requests",
                "template", request.getTemplateName(),
                "status", "bad_request")
                .increment();
            
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(createErrorResponse("INVALID_REQUEST", e.getMessage()))
                .build();
                
        } catch (Exception e) {
            sample.stop(Timer.builder("document.generation.async.request.time")
                .tag("template", request.getTemplateName())
                .tag("success", "false")
                .register(meterRegistry));
            
            LOG.error("Error processing async document request for template: " + request.getTemplateName(), e);
            meterRegistry.counter("document.generation.async.requests",
                "template", request.getTemplateName(),
                "status", "error")
                .increment();
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(createErrorResponse("ASYNC_ERROR", "Error processing async document request: " + e.getMessage()))
                .build();
        }
    }
    
    @GET
    @Path("/health")
    public Response healthCheck() {
        try {
            // Simple health check - could be expanded to check dependencies
            return Response.ok()
                .entity(createSuccessResponse("HEALTHY", "Document service is operational"))
                .build();
        } catch (Exception e) {
            LOG.error("Health check failed", e);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(createErrorResponse("UNHEALTHY", "Document service is not operational"))
                .build();
        }
    }
    
    @GET
    @Path("/metrics/summary")
    public Response getMetricsSummary() {
        try {
            // This could return a summary of key metrics
            // In a real implementation, you'd query the MeterRegistry
            return Response.ok()
                .entity(createSuccessResponse("METRICS_AVAILABLE", "Metrics are being collected"))
                .build();
        } catch (Exception e) {
            LOG.error("Error retrieving metrics summary", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(createErrorResponse("METRICS_ERROR", "Error retrieving metrics"))
                .build();
        }
    }
    
    private Object createErrorResponse(String errorCode, String message) {
        return new ErrorResponse(errorCode, message, java.time.Instant.now().toString());
    }
    
    private Object createSuccessResponse(String status, String message) {
        return new SuccessResponse(status, message, java.time.Instant.now().toString());
    }
    
    // Inner classes for response DTOs
    public static class ErrorResponse {
        public String errorCode;
        public String message;
        public String timestamp;
        
        public ErrorResponse(String errorCode, String message, String timestamp) {
            this.errorCode = errorCode;
            this.message = message;
            this.timestamp = timestamp;
        }
    }
    
    public static class SuccessResponse {
        public String status;
        public String message;
        public String timestamp;
        
        public SuccessResponse(String status, String message, String timestamp) {
            this.status = status;
            this.message = message;
            this.timestamp = timestamp;
        }
    }
}