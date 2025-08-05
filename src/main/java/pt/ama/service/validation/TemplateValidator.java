package pt.ama.service.validation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pt.ama.dto.TemplateRequest;
import pt.ama.exception.BusinessException;
import pt.ama.model.DocumentType;
import pt.ama.repository.TemplateRepository;

@ApplicationScoped
public class TemplateValidator {

    @Inject
    TemplateRepository templateRepository;

    public void validateForCreation(TemplateRequest request) {
        validateTemplateName(request.getName());
        validateTemplateContent(request);
        validateTemplateType(request.getType());
        
        if (templateRepository.existsByName(request.getName())) {
            throw new TemplateAlreadyExistsException(request.getName());
        }
    }

    public void validateForUpdate(String currentName, TemplateRequest request) {
        validateTemplateName(request.getName());
        validateTemplateContent(request);
        validateTemplateType(request.getType());

        if (!currentName.equals(request.getName()) && 
            templateRepository.existsByName(request.getName())) {
            throw new TemplateAlreadyExistsException(request.getName());
        }
    }

    private void validateTemplateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nome do template é obrigatório");
        }
        
        if (name.length() > 100) {
            throw new ValidationException("Nome do template não pode exceder 100 caracteres");
        }
        
        if (!name.matches("^[a-zA-Z0-9_-]+$")) {
            throw new ValidationException("Nome do template deve conter apenas letras, números, hífens e underscores");
        }
    }

    private void validateTemplateContent(TemplateRequest request) {
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new ValidationException("Conteúdo do template é obrigatório");
        }
        
        if (request.getContent().length() > 1000000) { // 1MB
            throw new ValidationException("Conteúdo do template não pode exceder 1MB");
        }
    }

    private void validateTemplateType(DocumentType type) {
        if (type == null) {
            throw new ValidationException("Tipo do documento é obrigatório");
        }
    }

    public static class ValidationException extends BusinessException {
        public ValidationException(String message) {
            super("VALIDATION_ERROR", message);
        }
    }

    public static class TemplateAlreadyExistsException extends BusinessException {
        public TemplateAlreadyExistsException(String templateName) {
            super("TEMPLATE_ALREADY_EXISTS", 
                  String.format("Template com nome '%s' já existe", templateName));
        }
    }
}