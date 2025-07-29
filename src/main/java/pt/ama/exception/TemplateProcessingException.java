package pt.ama.exception;

import lombok.Getter;

/**
 * Exceção lançada quando ocorre um erro durante o processamento do template.
 */
@Getter
public class TemplateProcessingException extends RuntimeException {
    
    private final String templateName;
    
    public TemplateProcessingException(String templateName, String message, Throwable cause) {
        super(String.format("Erro ao processar template '%s': %s", templateName, message), cause);
        this.templateName = templateName;
    }
    
    public TemplateProcessingException(String templateName, String message) {
        super(String.format("Erro ao processar template '%s': %s", templateName, message));
        this.templateName = templateName;
    }
}