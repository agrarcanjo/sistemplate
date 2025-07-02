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
    Engine quteEngine;

    public byte[] generateDocument(DocumentRequest request) throws Exception {
        Template template = templateService.findByName(request.getTemplateName());
        if (template == null) {
            throw new RuntimeException("Template não encontrado: " + request.getTemplateName());
        }

        switch (template.getType()) {
            case PDF:
                return templateService.generatePdf(
                    request.getTemplateName(), 
                    request.getData(), 
                    request.getOptions()
                );
            case EMAIL:
                // TODO: Implementar geração de email
                throw new UnsupportedOperationException("Email generation not implemented yet");
            case SMS:
                // TODO: Implementar geração de SMS
                throw new UnsupportedOperationException("SMS generation not implemented yet");
            default:
                throw new UnsupportedOperationException("Documento não suportado " + template.getType());
        }
    }
}