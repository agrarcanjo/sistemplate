package pt.ama.exception;

public class TemplateAlreadyExistsException extends BusinessException {
    
    public TemplateAlreadyExistsException(String templateName) {
        super("TEMPLATE_ALREADY_EXISTS", 
              String.format("Template com nome '%s' já existe", templateName));
    }
}