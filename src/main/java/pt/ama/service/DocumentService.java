package pt.ama.service;

import pt.ama.model.DocumentRequest;
import pt.ama.model.Template;
import io.quarkus.qute.Engine;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DocumentService {

    @Inject
    TemplateService templateService;

    @Inject
    PdfGenerator pdfGenerator;
    
    // @Inject
    // EmailGenerator emailGenerator; // Future implementation
    
    // @Inject
    // SmsGenerator smsGenerator; // Future implementation

    @Inject
    Engine quteEngine;

    public byte[] generateDocument(DocumentRequest request) throws Exception {
        Template template = templateService.findByName(request.templateName);
        if (template == null) {
            throw new RuntimeException("Template not found: " + request.templateName);
        }

        String processedContent = quteEngine.parse(template.content).data(request.metadata).render();

        switch (template.type) {
            case PDF:
                return pdfGenerator.generate(processedContent);
            // case EMAIL:
                // return emailGenerator.generate(processedContent, request.metadata); // Future
            // case SMS:
                // return smsGenerator.generate(processedContent, request.metadata); // Future
            default:
                throw new UnsupportedOperationException("Document type not supported: " + template.type);
        }
    }
}
