package pt.ama.exception;

/**
 * Exceção específica para erros na geração de PDF
 */
public class PdfGenerationException extends BusinessException {
    
    public PdfGenerationException(String message) {
        super("PDF_GENERATION_ERROR", message);
    }
    
    public PdfGenerationException(String message, Throwable cause) {
        super("PDF_GENERATION_ERROR", message, cause);
    }
}