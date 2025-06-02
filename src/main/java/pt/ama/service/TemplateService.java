package pt.ama.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pt.ama.model.Template;
import pt.ama.repository.TemplateRepository;
import pt.ama.model.DocumentType;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TemplateService {
    
    @Inject
    TemplateRepository templateRepository;
    
    @Inject
    PdfGenerator pdfGenerator;

    public List<Template> findAll() {
        return templateRepository.listAll();
    }

    public Template findByName(String name) {
        return templateRepository.findByName(name);
    }

    public List<Template> findByType(DocumentType type) {
        return templateRepository.findByType(type);
    }

    public List<Template> findByNameContaining(String namePattern) {
        return templateRepository.findByNameContaining(namePattern);
    }

    public void save(Template template) {
        templateRepository.persist(template);
    }

    public void update(Template template) {
        templateRepository.update(template);
    }

    public void delete(String name) {
        templateRepository.deleteByName(name);
    }

    public boolean exists(String name) {
        return templateRepository.exists(name);
    }

    public byte[] generatePdf(String templateName, Map<String, Object> data) {
        Template template = findByName(templateName);
        if (template == null) {
            throw new RuntimeException("Template não encontrado: " + templateName);
        }
        return pdfGenerator.generatePdf(template.getContent(), data);
    }
}