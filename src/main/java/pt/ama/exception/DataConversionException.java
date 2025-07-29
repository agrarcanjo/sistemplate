package pt.ama.exception;

import lombok.Getter;

/**
 * Exceção lançada quando ocorre um erro durante a conversão de dados.
 */
@Getter
public class DataConversionException extends RuntimeException {
    
    public DataConversionException(String message, Throwable cause) {
        super(String.format("Erro ao converter dados: %s", message), cause);
    }
    
    public DataConversionException(String message) {
        super(String.format("Erro ao converter dados: %s", message));
    }
}