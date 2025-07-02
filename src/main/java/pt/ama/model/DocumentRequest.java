package pt.ama.model;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DocumentRequest {
    @NotBlank(message = "Template name is required")
    private String templateName;
    
    @NotNull(message = "Data is required")
    private JsonNode data;
    
    // Configurações opcionais para geração do PDF
    private PdfOptions options;
    
    @Data
    public static class PdfOptions {
        private String filename;
        private String orientation = "portrait"; // portrait ou landscape
        private String pageSize = "A4"; // A4, A3, LETTER, etc.
        private Boolean includeMetadata = true;
        private String author;
        private String subject;
        private String keywords;
    }
}