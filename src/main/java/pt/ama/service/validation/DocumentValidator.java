package pt.ama.service.validation;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import pt.ama.dto.DocumentRequest;
import pt.ama.exception.BusinessException;

/**
 * Validador específico para requests de documento
 */
@ApplicationScoped
public class DocumentValidator {
    
    private static final Logger LOG = Logger.getLogger(DocumentValidator.class);
    
    /**
     * Valida um request de documento
     */
    public void validateDocumentRequest(DocumentRequest request) {
        LOG.debugf("Validando request de documento para template: %s", request.getTemplateName());
        
        validateTemplateName(request.getTemplateName());
        validateData(request.getData());
        validateOptions(request.getOptions());
        
        LOG.debugf("Request de documento validado com sucesso");
    }
    
    /**
     * Valida o nome do template
     */
    private void validateTemplateName(String templateName) {
        if (templateName == null || templateName.trim().isEmpty()) {
            throw new DocumentValidationException("Nome do template é obrigatório");
        }
        
        if (templateName.length() > 100) {
            throw new DocumentValidationException("Nome do template muito longo (máximo 100 caracteres)");
        }
        
        // Validar caracteres permitidos
        if (!templateName.matches("^[a-zA-Z0-9_-]+$")) {
            throw new DocumentValidationException("Nome do template contém caracteres inválidos");
        }
    }
    
    /**
     * Valida os dados fornecidos
     */
    private void validateData(JsonNode data) {
        if (data == null) {
            throw new DocumentValidationException("Dados são obrigatórios para geração do documento");
        }
        
        if (data.isEmpty()) {
            LOG.warnf("Dados vazios fornecidos para geração de documento");
        }
        
        // Validar tamanho dos dados (limite de 10MB em JSON)
        String dataString = data.toString();
        if (dataString.length() > 10_000_000) {
            throw new DocumentValidationException("Dados muito grandes (limite: 10MB)");
        }
    }
    
    /**
     * Valida as opções se fornecidas
     */
    private void validateOptions(DocumentRequest.PdfOptions options) {
        if (options == null) {
            return; // Opções são opcionais
        }
        
        // Validar filename se fornecido
        if (options.getFilename() != null) {
            validateFilename(options.getFilename());
        }
        
        // Validar outros campos das opções
        validatePdfOptions(options);
    }
    
    /**
     * Valida o nome do arquivo
     */
    private void validateFilename(String filename) {
        if (filename.trim().isEmpty()) {
            throw new DocumentValidationException("Nome do arquivo não pode ser vazio");
        }
        
        if (filename.length() > 255) {
            throw new DocumentValidationException("Nome do arquivo muito longo (máximo 255 caracteres)");
        }
        
        // Validar caracteres proibidos em nomes de arquivo
        if (filename.matches(".*[<>:\"/\\\\|?*].*")) {
            throw new DocumentValidationException("Nome do arquivo contém caracteres inválidos");
        }
    }
    
    /**
     * Valida opções específicas de PDF
     */
    private void validatePdfOptions(DocumentRequest.PdfOptions options) {
        // Validar orientação
        if (options.getOrientation() != null) {
            String orientation = options.getOrientation().toLowerCase();
            if (!orientation.equals("portrait") && !orientation.equals("landscape")) {
                throw new DocumentValidationException("Orientação deve ser 'portrait' ou 'landscape'");
            }
        }
        
        // Validar tamanho da página
        if (options.getPageSize() != null) {
            String pageSize = options.getPageSize().toUpperCase();
            if (!pageSize.matches("A[3-5]|LETTER|LEGAL")) {
                throw new DocumentValidationException("Tamanho de página inválido. Valores aceitos: A3, A4, A5, LETTER, LEGAL");
            }
        }
        
        // Validar metadados
        validateMetadata(options);
    }
    
    /**
     * Valida metadados do PDF
     */
    private void validateMetadata(DocumentRequest.PdfOptions options) {
        if (options.getAuthor() != null && options.getAuthor().length() > 100) {
            throw new DocumentValidationException("Autor muito longo (máximo 100 caracteres)");
        }
        
        if (options.getSubject() != null && options.getSubject().length() > 200) {
            throw new DocumentValidationException("Assunto muito longo (máximo 200 caracteres)");
        }
        
        if (options.getKeywords() != null && options.getKeywords().length() > 500) {
            throw new DocumentValidationException("Palavras-chave muito longas (máximo 500 caracteres)");
        }
    }
    
    /**
     * Exceção específica para validação de documentos
     */
    public static class DocumentValidationException extends BusinessException {
        public DocumentValidationException(String message) {
            super("DOCUMENT_VALIDATION_ERROR", message);
        }
    }
}