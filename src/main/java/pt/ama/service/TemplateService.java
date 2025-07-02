package pt.ama.service;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pt.ama.model.Template;
import pt.ama.model.DocumentRequest;
import pt.ama.repository.TemplateRepository;
import pt.ama.model.DocumentType;

import java.time.LocalDateTime;
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
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        templateRepository.persist(template);
    }

    public void update(Template template) {
        template.setUpdatedAt(LocalDateTime.now());
        templateRepository.update(template);
    }

    public void delete(String name) {
        templateRepository.deleteByName(name);
    }

    public boolean exists(String name) {
        return templateRepository.exists(name);
    }

    public List<Template> findByTemplateVersion(String name) {
        return templateRepository.findByTemplateVersion(name);
    }

    // Método atualizado para suportar JsonNode
    public byte[] generatePdf(String templateName, JsonNode data, DocumentRequest.PdfOptions options) {
        Template template = findByName(templateName);
        if (template == null) {
            throw new RuntimeException("Template not found: " + templateName);
        }
        return pdfGenerator.generatePdf(template.getContent(), data, options);
    }
    
    // Método de compatibilidade com versão anterior
    public byte[] generatePdf(String templateName, Map<String, Object> data) {
        Template template = findByName(templateName);
        if (template == null) {
            throw new RuntimeException("Template not found: " + templateName);
        }
        return pdfGenerator.generatePdf(template.getContent(), data);
    }
    
    public List<Template> findByCategory(String category) {
        return templateRepository.find("category = ?1 and active = true", category).list();
    }
    
    public List<Template> findByOwner(String owner) {
        return templateRepository.find("owner = ?1 and active = true", owner).list();
    }
}