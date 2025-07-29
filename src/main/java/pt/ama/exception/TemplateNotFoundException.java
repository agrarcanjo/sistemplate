package pt.ama.exception;

import lombok.Getter;

/**
 * Exceção lançada quando um template não é encontrado no sistema.
 */
@Getter
public class TemplateNotFoundException extends RuntimeException {
    
    private final String templateName;
    
    public TemplateNotFoundException(String templateName) {
        super(String.format("Template não encontrado: %s", templateName));
        this.templateName = templateName;
    }
}