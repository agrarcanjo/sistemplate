package pt.ama.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentResponse {
    
    @JsonProperty("filename")
    private String filename;
    
    @JsonProperty("content_type")
    private String contentType;
    
    @JsonProperty("content")
    private String content;
    
    @JsonProperty("size")
    private Long size;
    
    @JsonProperty("template_name")
    private String templateName;
    
    @JsonProperty("generated_at")
    private String generatedAt;
    
    @JsonProperty("encoding")
    @Builder.Default
    private String encoding = "base64";
    
    @JsonProperty("version")
    @Builder.Default
    private String version = "1.0";
    
    /**
     * Cria uma resposta com timestamp atual
     */
    public static DocumentResponseBuilder withCurrentTimestamp() {
        return DocumentResponse.builder()
                .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
    
    /**
     * Cria uma resposta para PDF
     */
    public static DocumentResponseBuilder forPdf(String filename, String base64Content, long size, String templateName) {
        return withCurrentTimestamp()
                .filename(filename.endsWith(".pdf") ? filename : filename + ".pdf")
                .contentType("application/pdf")
                .content(base64Content)
                .size(size)
                .templateName(templateName);
    }
    
    /**
     * Cria uma resposta para Email
     */
    public static DocumentResponseBuilder forEmail(String filename, String htmlContent, long size, String templateName) {
        return withCurrentTimestamp()
                .filename(filename.endsWith(".html") ? filename : filename + ".html")
                .contentType("text/html")
                .content(htmlContent)
                .size(size)
                .templateName(templateName)
                .encoding("utf-8");
    }
    
    /**
     * Cria uma resposta para SMS
     */
    public static DocumentResponseBuilder forSms(String filename, String textContent, long size, String templateName) {
        return withCurrentTimestamp()
                .filename(filename.endsWith(".txt") ? filename : filename + ".txt")
                .contentType("text/plain")
                .content(textContent)
                .size(size)
                .templateName(templateName)
                .encoding("utf-8");
    }
}