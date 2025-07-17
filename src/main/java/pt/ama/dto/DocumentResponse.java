package pt.ama.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentResponse {
    private String filename;
    private String contentType;
    private String base64Content;
    private Long size;
    private String templateName;
    private String generatedAt;
}