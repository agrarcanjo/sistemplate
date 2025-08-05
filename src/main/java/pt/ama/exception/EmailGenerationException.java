package pt.ama.exception;

/**
 * Exceção específica para erros na geração de email
 */
public class EmailGenerationException extends BusinessException {
    
    public EmailGenerationException(String message) {
        super("EMAIL_GENERATION_ERROR", message);
    }
    
    public EmailGenerationException(String message, Throwable cause) {
        super("EMAIL_GENERATION_ERROR", message, cause);
    }
}