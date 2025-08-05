package pt.ama.service.generator;

import pt.ama.dto.DocumentRequest;

/**
 * Interface base para todos os geradores de documento
 */
public interface DocumentGenerator {
    
    /**
     * Gera um documento baseado no conteúdo processado e request
     * 
     * @param processedContent Conteúdo já processado pelo template engine
     * @param request Request original com dados e opções
     * @return Array de bytes do documento gerado
     */
    byte[] generate(String processedContent, DocumentRequest request);
    
    /**
     * Retorna o tipo de documento que este gerador suporta
     */
    String getSupportedType();
    
    /**
     * Valida se o conteúdo é válido para este tipo de gerador
     */
    default void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Conteúdo não pode ser nulo ou vazio");
        }
    }
}