package pt.ama.service.generator;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ama.dto.DocumentRequest;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class PdfGeneratorTest {

    private PdfGenerator pdfGenerator;

    @BeforeEach
    void setUp() {
        pdfGenerator = new PdfGenerator();
    }

    @Test
    @DisplayName("Should generate PDF successfully with valid HTML content")
    void shouldGeneratePdfSuccessfully() {
        String htmlContent = "<html><body><h1>Test Document</h1><p>This is a test.</p></body></html>";
        DocumentRequest request = createDocumentRequest();

        byte[] result = pdfGenerator.generate(htmlContent, request);

        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals("PDF", pdfGenerator.getSupportedType());
    }

    @Test
    @DisplayName("Should throw exception for null content")
    void shouldThrowExceptionForNullContent() {
        DocumentRequest request = createDocumentRequest();

        assertThrows(IllegalArgumentException.class, () -> {
            pdfGenerator.generate(null, request);
        });
    }

    @Test
    @DisplayName("Should throw exception for empty content")
    void shouldThrowExceptionForEmptyContent() {
        DocumentRequest request = createDocumentRequest();

        assertThrows(IllegalArgumentException.class, () -> {
            pdfGenerator.generate("", request);
        });
    }

    @Test
    @DisplayName("Should throw exception for content too large")
    void shouldThrowExceptionForContentTooLarge() {
        String largeContent = "x".repeat(10_000_001);
        DocumentRequest request = createDocumentRequest();

        assertThrows(IllegalArgumentException.class, () -> {
            pdfGenerator.generate(largeContent, request);
        });
    }

    @Test
    @DisplayName("Should generate PDF with default options when none provided")
    void shouldGeneratePdfWithDefaultOptions() {
        String htmlContent = "<html><body><h1>Test</h1></body></html>";
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("test-template");

        byte[] result = pdfGenerator.generate(htmlContent, request);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("Should generate PDF with custom options")
    void shouldGeneratePdfWithCustomOptions() {
        String htmlContent = "<html><body><h1>Test</h1></body></html>";
        DocumentRequest request = createDocumentRequestWithOptions();

        byte[] result = pdfGenerator.generate(htmlContent, request);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    private DocumentRequest createDocumentRequest() {
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("test-template");
        return request;
    }

    private DocumentRequest createDocumentRequestWithOptions() {
        DocumentRequest request = createDocumentRequest();
        
        DocumentRequest.PdfOptions options = new DocumentRequest.PdfOptions();
        options.setAuthor("Test Author");
        options.setSubject("Test Subject");
        options.setKeywords("test,pdf,generation");
        options.setPageSize("A4");
        options.setOrientation("portrait");
        options.setFilename("test-document.pdf");
        
        request.setOptions(options);
        return request;
    }
}