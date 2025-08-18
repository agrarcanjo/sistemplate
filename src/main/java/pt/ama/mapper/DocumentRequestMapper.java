package pt.ama.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import pt.ama.dto.DocumentGenerationMessage;
import pt.ama.dto.DocumentRequest;

@ApplicationScoped
public class DocumentRequestMapper {

    public DocumentGenerationMessage toDocumentGenerationMessage(DocumentRequest request) {
        if (request == null) {
            return null;
        }

        DocumentGenerationMessage message = new DocumentGenerationMessage();
        message.setEventId(null);
        message.setTemplateName(request.getTemplateName());
        message.setData(request.getData());
        message.setReceiver(request.getReceiver());
        message.setCallbackUrl(request.getCallbackUrl());
        message.setOptions(request.getOptions());

        return message;
    }

    public DocumentRequest toDocumentRequest(DocumentGenerationMessage message) {
        if (message == null) {
            return null;
        }

        DocumentRequest request = new DocumentRequest();
        request.setTemplateName(message.getTemplateName());
        request.setData(message.getData());
        request.setReceiver(message.getReceiver());
        request.setCallbackUrl(message.getCallbackUrl());
        request.setOptions(message.getOptions());
        request.setAsync(true); // Assuming conversion from message implies async processing

        return request;
    }
}
