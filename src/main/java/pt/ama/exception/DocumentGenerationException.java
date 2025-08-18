package pt.ama.exception;

import lombok.Getter;

/**
 * Exceção lançada quando ocorre um erro durante a geração do documento.
 */
@Getter
public class DocumentGenerationException extends RuntimeException {
    
    private final String templateName;
    
    public DocumentGenerationException(String templateName, String message, Throwable cause) {
        super(String.format("Erro ao gerar documento para template %s: %s", templateName, message), cause);
        this.templateName = templateName;
    }
    
    public DocumentGenerationException(String templateName, String message) {
        super(String.format("Erro ao gerar documento para template '%s': %s", templateName, message));
        this.templateName = templateName;
    }
}