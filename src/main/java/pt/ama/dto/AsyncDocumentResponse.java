package pt.ama.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsyncDocumentResponse {
    private String eventId;
    private String status;
    private String message;
}