package pt.ama.exception;

/**
 * Exceção específica para erros na geração de SMS
 */
public class SmsGenerationException extends BusinessException {
    
    public SmsGenerationException(String message) {
        super("SMS_GENERATION_ERROR", message);
    }
    
    public SmsGenerationException(String message, Throwable cause) {
        super("SMS_GENERATION_ERROR", message, cause);
    }
}