package pt.ama.service.validation;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import pt.ama.exception.RequiredFieldsValidationException;
import pt.ama.model.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validador responsável por verificar se os campos obrigatórios estão presentes nos dados da requisição.
 */
@ApplicationScoped
public class RequiredFieldsValidator {
    
    private static final Logger LOG = Logger.getLogger(RequiredFieldsValidator.class);
    
    /**
     * Valida se todos os campos obrigatórios definidos no template estão presentes nos dados.
     *
     * @param template Template contendo os metadados com campos obrigatórios
     * @param dataMap Dados da requisição convertidos para Map
     * @throws RequiredFieldsValidationException se algum campo obrigatório estiver ausente
     */
    public void validateRequiredFields(Template template, Map<String, Object> dataMap) {
        LOG.debugf("RequiredFieldsValidator: Iniciando validação de campos obrigatórios para template '%s'", 
                  template.getName());
        
        if (template.getMetadata() == null || 
            template.getMetadata().getRequiredFields() == null || 
            template.getMetadata().getRequiredFields().isEmpty()) {
            LOG.debugf("RequiredFieldsValidator: Template '%s' não possui campos obrigatórios definidos", 
                      template.getName());
            return;
        }
        
        List<String> requiredFields = template.getMetadata().getRequiredFields();
        List<String> missingFields = findMissingFields(requiredFields, dataMap);
        
        if (!missingFields.isEmpty()) {
            LOG.warnf("RequiredFieldsValidator: Campos obrigatórios ausentes no template '%s': %s", 
                     template.getName(), String.join(", ", missingFields));
            throw new RequiredFieldsValidationException(template.getName(), missingFields);
        }
        
        LOG.infof("RequiredFieldsValidator: Validação concluída com sucesso para template '%s'. " +
                 "Todos os %d campos obrigatórios estão presentes", 
                 template.getName(), requiredFields.size());
    }
    
    /**
     * Encontra os campos obrigatórios que estão ausentes nos dados.
     *
     * @param requiredFields Lista de campos obrigatórios
     * @param dataMap Dados da requisição
     * @return Lista de campos ausentes
     */
    private List<String> findMissingFields(List<String> requiredFields, Map<String, Object> dataMap) {
        List<String> missingFields = new ArrayList<>();
        
        for (String field : requiredFields) {
            if (!isFieldPresent(field, dataMap)) {
                missingFields.add(field);
            }
        }
        
        return missingFields;
    }
    
    /**
     * Verifica se um campo está presente nos dados.
     * Suporta campos aninhados usando notação de ponto (ex: "user.name").
     *
     * @param fieldPath Caminho do campo (pode ser aninhado)
     * @param dataMap Dados da requisição
     * @return true se o campo estiver presente e não for null
     */
    private boolean isFieldPresent(String fieldPath, Map<String, Object> dataMap) {
        if (fieldPath == null || fieldPath.trim().isEmpty()) {
            return false;
        }
        
        String[] fieldParts = fieldPath.split("\\.");
        Object currentValue = dataMap;
        
        for (String fieldPart : fieldParts) {
            if (!(currentValue instanceof Map)) {
                return false;
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> currentMap = (Map<String, Object>) currentValue;
            
            if (!currentMap.containsKey(fieldPart)) {
                return false;
            }
            
            currentValue = currentMap.get(fieldPart);
            
            if (currentValue == null) {
                return false;
            }
        }
        
        return true;
    }
}