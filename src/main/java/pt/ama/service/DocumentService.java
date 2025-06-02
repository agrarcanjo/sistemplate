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
    
     @Inject
     EmailGenerator emailGenerator;
    
     @Inject
     SmsGenerator smsGenerator;

    @Inject
    Engine quteEngine;

    public byte[] generateDocument(DocumentRequest request) throws Exception {
        Template template = templateService.findByName(request.getTemplateName());
        if (template == null) {
            throw new RuntimeException("Template não encontrado: " + request.getTemplateName());
        }

        String processedContent = quteEngine
                .parse(template.getContent())
                .data(request.getMetadata())
                .render();

        switch (template.getType()) {
            case PDF:
                return pdfGenerator.generatePdf(template.getContent(), request.getMetadata());
             case EMAIL:
                 return emailGenerator.generate(processedContent, request.metadata); // Future
             case SMS:
                 return smsGenerator.generate(processedContent, request.metadata); // Future
            default:
                throw new UnsupportedOperationException("Documento não suportado " + template.getType());
        }
    }
}
