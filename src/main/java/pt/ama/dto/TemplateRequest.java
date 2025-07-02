package pt.ama.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.ama.model.DocumentType;
import pt.ama.model.Template;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRequest {
    @NotBlank(message = "Name cannot be blank")
    private String name;
    
    @NotNull(message = "Type cannot be null")
    private DocumentType type;
    
    @NotBlank(message = "Content cannot be blank")
    private String content;
    
    private String description;
    private String author;
    private String owner;
    private String manager;
    private String category;
    private List<String> tags;
    
    // Metadados do template
    private TemplateMetadataRequest metadata;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemplateMetadataRequest {
        private List<String> requiredFields;
        private List<String> optionalFields;
        private List<ImageReferenceRequest> imageReferences;
        private String sampleData;
        private String documentation;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageReferenceRequest {
        private String placeholder;
        private String description;
        private String recommendedSize;
        private boolean required;
    }
}