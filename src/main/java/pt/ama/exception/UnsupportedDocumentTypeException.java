package pt.ama.exception;

import lombok.Getter;
import pt.ama.model.DocumentType;

/**
 * Exceção lançada quando um tipo de documento não é suportado pelo sistema.
 */
@Getter
public class UnsupportedDocumentTypeException extends RuntimeException {
    
    private final DocumentType documentType;
    
    public UnsupportedDocumentTypeException(DocumentType documentType) {
        super(String.format("Tipo de documento não suportado: %s", documentType));
        this.documentType = documentType;
    }
}