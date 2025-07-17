package pt.ama.service;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pt.ama.model.Template;
import pt.ama.dto.DocumentRequest;
import pt.ama.repository.TemplateRepository;
import pt.ama.model.DocumentType;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class TemplateService {
    
    private static final Logger LOG = Logger.getLogger(TemplateService.class);
    
    @Inject
    TemplateRepository templateRepository;
    
    @Inject
    PdfGenerator pdfGenerator;

    public List<Template> findAll() {
        return templateRepository.listAll();
    }

    public Template findByName(String name) {
        LOG.infof("TemplateService: Buscando template com nome: '%s'", name);
            Template template = templateRepository.findByName(name);
        if (template == null) {
            LOG.warnf("TemplateService: Template não encontrado: '%s'", name);
        } else {
            LOG.infof("TemplateService: Template encontrado: '%s', active=%s", template.getName(), template.isActive());
        }
        return template;
    }

    public List<Template> findByType(DocumentType type) {
        return templateRepository.findByType(type);
    }

    public List<Template> findByNameContaining(String namePattern) {
        return templateRepository.findByNameContaining(namePattern);
    }

    public void save(Template template) {
        LOG.infof("TemplateService: Salvando template: '%s'", template.getName());
        
        if (!template.isActive() && template.getName() != null) {
            template.setActive(true);
            LOG.infof("TemplateService: Definindo template como ativo: '%s'", template.getName());
        }
        
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        templateRepository.persist(template);
        
        LOG.infof("TemplateService: Template salvo com sucesso: '%s', active=%s", 
                 template.getName(), template.isActive());
    }

    public void update(Template template) {
        LOG.infof("TemplateService: Atualizando template: '%s'", template.getName());
        template.setUpdatedAt(LocalDateTime.now());
        templateRepository.update(template);
    }

    public void delete(String name) {
        LOG.infof("TemplateService: Deletando template: '%s'", name);
        templateRepository.deleteByName(name);
    }

    public boolean exists(String name) {
        boolean exists = templateRepository.exists(name);
        LOG.infof("TemplateService: Template '%s' existe: %s", name, exists);
        return exists;
    }

    public List<Template> findByTemplateVersion(String name) {
        return templateRepository.findByTemplateVersion(name);
    }

    public byte[] generatePdf(String templateName, JsonNode data, DocumentRequest.PdfOptions options) {
        LOG.infof("TemplateService: Gerando PDF para template: '%s'", templateName);
        Template template = findByName(templateName);
        if (template == null) {
            throw new RuntimeException("Template not found: " + templateName);
        }
        return pdfGenerator.generatePdf(template.getContent(), data, options);
    }
    
    public List<Template> findByCategory(String category) {
        return templateRepository.find("category = ?1 and active = true", category).list();
    }
    
    public List<Template> findByOwner(String owner) {
        return templateRepository.find("owner = ?1 and active = true", owner).list();
    }
}