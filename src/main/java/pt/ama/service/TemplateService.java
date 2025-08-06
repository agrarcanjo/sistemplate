package pt.ama.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pt.ama.dto.TemplateRequest;
import pt.ama.exception.TemplateNotFoundException;
import pt.ama.mapper.TemplateMapper;
import pt.ama.model.DocumentStatus;
import pt.ama.model.DocumentType;
import pt.ama.model.Template;
import pt.ama.repository.TemplateRepository;
import pt.ama.service.validation.TemplateValidator;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class TemplateService {

    private static final Logger LOG = Logger.getLogger(TemplateService.class);

    @Inject
    TemplateRepository templateRepository;

    @Inject
    TemplateMapper templateMapper;

    @Inject
    TemplateValidator templateValidator;

    public List<Template> findAll() {
        LOG.debug("Buscando todos os templates");
        return templateRepository.listAll();
    }

    public Template findByName(String name) {
        LOG.debugf("Buscando template com nome: %s", name);
        return templateRepository.findByName(name);
    }

    public Template findByNameOrThrow(String name) {
        Template template = findByName(name);
        if (template == null) {
            LOG.warnf("Template não encontrado: %s", name);
            throw new TemplateNotFoundException(name);
        }
        LOG.debugf("Template encontrado: %s", template.getName());
        return template;
    }

    public List<Template> findByType(DocumentType type) {
        LOG.debugf("Buscando templates por tipo: %s", type);
        return templateRepository.findByType(type);
    }

    public List<Template> findByNameContaining(String namePattern) {
        LOG.debugf("Buscando templates com padrão: %s", namePattern);
        return templateRepository.findByNameContaining(namePattern);
    }

    public List<Template> findByTemplateVersion(String templateName) {
        LOG.debugf("Buscando versões do template: %s", templateName);
        return templateRepository.findByTemplateVersion(templateName);
    }

    public Template createTemplate(TemplateRequest request) {
        LOG.infof("Criando novo template: %s", request.getName());

        templateValidator.validateForCreation(request);

        Template template = templateMapper.toEntity(request);
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        template.setActive(true);
        template.setStatus(DocumentStatus.RASCUNHO);

        templateRepository.persist(template);

        LOG.infof("Template criado com sucesso: %s", template.getName());
        return template;
    }

    public Template updateTemplate(String name, TemplateRequest request) {
        LOG.infof("Atualizando template: %s", name);

        Template existingTemplate = findByNameOrThrow(name);
        templateValidator.validateForUpdate(name, request);

        templateMapper.updateEntity(existingTemplate, request);
        existingTemplate.setUpdatedAt(LocalDateTime.now());

        templateRepository.persist(existingTemplate);

        LOG.infof("Template atualizado com sucesso: %s", existingTemplate.getName());
        return existingTemplate;
    }

    public void deleteTemplate(String name) {
        LOG.infof("Removendo template: %s", name);

        Template template = findByNameOrThrow(name);

        validateTemplateCanBeDeleted(template);

        templateRepository.delete(template);

        LOG.infof("Template removido com sucesso: %s", name);
    }

    public boolean exists(String name) {
        return templateRepository.existsByName(name);
    }

    private void validateTemplateCanBeDeleted(Template template) {
        if (!template.isActive()) {
            LOG.warnf("Tentativa de remover template já inativo: %s", template.getName());
        }
    }
}