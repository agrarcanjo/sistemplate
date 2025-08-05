package pt.ama.exception;

/**
 * Exceção para template inativo
 */
public class InactiveTemplateException extends BusinessException {
    
    public InactiveTemplateException(String templateName) {
        super("INACTIVE_TEMPLATE", 
              String.format("Template '%s' está inativo e não pode ser usado", templateName));
    }
}