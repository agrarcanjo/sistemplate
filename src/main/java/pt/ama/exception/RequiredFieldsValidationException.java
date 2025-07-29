package pt.ama.exception;

import lombok.Getter;

import java.util.List;

/**
 * Exceção lançada quando campos obrigatórios estão ausentes na requisição de geração de documento.
 */
@Getter
public class RequiredFieldsValidationException extends RuntimeException {
    
    private final List<String> missingFields;
    private final String templateName;
    
    public RequiredFieldsValidationException(String templateName, List<String> missingFields) {
        super(buildMessage(templateName, missingFields));
        this.templateName = templateName;
        this.missingFields = missingFields;
    }
    
    private static String buildMessage(String templateName, List<String> missingFields) {
        return String.format("Campos obrigatórios ausentes no template '%s': %s", 
                           templateName, String.join(", ", missingFields));
    }

}