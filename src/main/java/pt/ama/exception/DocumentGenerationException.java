package pt.ama.exception;

import lombok.Getter;

/**
 * Exceção lançada quando ocorre um erro durante a geração do documento.
 */
@Getter
public class DocumentGenerationException extends RuntimeException {
    
    private final String templateName;
    private final String documentType;
    
    public DocumentGenerationException(String templateName, String documentType, String message, Throwable cause) {
        super(String.format("Erro ao gerar documento %s para template '%s': %s", documentType, templateName, message), cause);
        this.templateName = templateName;
        this.documentType = documentType;
    }
    
    public DocumentGenerationException(String templateName, String documentType, String message) {
        super(String.format("Erro ao gerar documento %s para template '%s': %s", documentType, templateName, message));
        this.templateName = templateName;
        this.documentType = documentType;
    }
}