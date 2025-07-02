package pt.ama.dto;

import lombok.Data;
import pt.ama.model.DocumentType;
import pt.ama.model.Template;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TemplateResponse {
    private String name;
    private DocumentType type;
    private String content;
    private String description;
    private String author;
    private BigDecimal version;
    private String owner;
    private String manager;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String category;
    private List<String> tags;
    private TemplateMetadataResponse metadata;
    
    @Data
    public static class TemplateMetadataResponse {
        private List<String> requiredFields;
        private List<String> optionalFields;
        private List<ImageReferenceResponse> imageReferences;
        private String sampleData;
        private String documentation;
    }
    
    @Data
    public static class ImageReferenceResponse {
        private String placeholder;
        private String description;
        private String recommendedSize;
        private boolean required;
    }
}