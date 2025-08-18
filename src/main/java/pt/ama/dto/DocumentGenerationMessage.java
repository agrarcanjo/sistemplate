package pt.ama.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentGenerationMessage {
    private String eventId;
    private String templateName;
    private JsonNode data;
    private String receiver;
    private String callbackUrl;
    private DocumentRequest.PdfOptions options;
}