package pt.ama.api;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ama.dto.DocumentRequest;
import pt.ama.dto.DocumentResponse;
import pt.ama.service.DocumentService;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static pt.ama.util.TemplateTestUtils.createDocumentRequest;

@QuarkusTest
class DocumentResourceTest {

    @InjectMock
    DocumentService documentService;

    private DocumentResource documentResource;

    @BeforeEach
    void setUp() {
        documentResource = new DocumentResource();
        documentResource.documentService = documentService;
    }

    @Test
    @DisplayName("Should generate document successfully and return binary response")
    void shouldGenerateDocumentSuccessfully() throws Exception {

        String templateName = "test-template";
        byte[] expectedDocument = "PDF content".getBytes();

        DocumentRequest request = createDocumentRequest(templateName, null);

        when(documentService.generateDocument(any(DocumentRequest.class)))
                .thenReturn(expectedDocument);


        Response response = documentResource.generateDocument(request);


        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(expectedDocument, response.getEntity());
        assertEquals("application/pdf", response.getHeaderString("Content-Type"));
        assertEquals("attachment; filename=\"test-template.pdf\"",
                response.getHeaderString("Content-Disposition"));

        verify(documentService, times(1)).generateDocument(request);
    }

    @Test
    @DisplayName("Should generate document with custom filename from options")
    void shouldGenerateDocumentWithCustomFilename() throws Exception {

        String templateName = "test-template";
        String customFilename = "custom-report";
        byte[] expectedDocument = "PDF content".getBytes();

        DocumentRequest request = createDocumentRequest(templateName, customFilename);

        when(documentService.generateDocument(any(DocumentRequest.class)))
                .thenReturn(expectedDocument);


        Response response = documentResource.generateDocument(request);


        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("attachment; filename=\"custom-report.pdf\"",
                response.getHeaderString("Content-Disposition"));

        verify(documentService, times(1)).generateDocument(request);
    }

    @Test
    @DisplayName("Should generate document with filename already containing .pdf extension")
    void shouldGenerateDocumentWithPdfExtension() throws Exception {

        String templateName = "test-template";
        String filenameWithExtension = "report.pdf";
        byte[] expectedDocument = "PDF content".getBytes();

        DocumentRequest request = createDocumentRequest(templateName, filenameWithExtension);

        when(documentService.generateDocument(any(DocumentRequest.class)))
                .thenReturn(expectedDocument);


        Response response = documentResource.generateDocument(request);


        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("attachment; filename=\"report.pdf\"",
                response.getHeaderString("Content-Disposition"));

        verify(documentService, times(1)).generateDocument(request);
    }

    @Test
    @DisplayName("Should generate code document successfully and return base64 response")
    void shouldGenerateCodeDocumentSuccessfully() throws Exception {

        String templateName = "test-template";
        byte[] expectedDocument = "PDF content".getBytes();
        String expectedBase64 = Base64.getEncoder().encodeToString(expectedDocument);

        DocumentRequest request = createDocumentRequest(templateName, null);

        when(documentService.generateDocument(any(DocumentRequest.class)))
                .thenReturn(expectedDocument);


        Response response = documentResource.generateBase64Document(request);


        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        DocumentResponse documentResponse = (DocumentResponse) response.getEntity();
        assertNotNull(documentResponse);
        assertEquals("test-template.pdf", documentResponse.getFilename());
        assertEquals("application/pdf", documentResponse.getContentType());
        //assertEquals(expectedBase64, documentResponse.ge());
        assertEquals(expectedDocument.length, documentResponse.getSize());
        assertEquals(templateName, documentResponse.getTemplateName());
        assertNotNull(documentResponse.getGeneratedAt());

        verify(documentService, times(1)).generateDocument(request);
    }

    @Test
    @DisplayName("Should generate code document with custom filename")
    void shouldGenerateCodeDocumentWithCustomFilename() throws Exception {

        String templateName = "test-template";
        String customFilename = "custom-report";
        byte[] expectedDocument = "PDF content".getBytes();

        DocumentRequest request = createDocumentRequest(templateName, customFilename);

        when(documentService.generateDocument(any(DocumentRequest.class)))
                .thenReturn(expectedDocument);


        Response response = documentResource.generateDocument(request);


        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        DocumentResponse documentResponse = (DocumentResponse) response.getEntity();
        assertNotNull(documentResponse);
        assertEquals("custom-report.pdf", documentResponse.getFilename());

        verify(documentService, times(1)).generateDocument(request);
    }

    @Test
    @DisplayName("Should handle empty document generation")
    void shouldHandleEmptyDocument() throws Exception {

        String templateName = "test-template";
        byte[] emptyDocument = new byte[0];

        DocumentRequest request = createDocumentRequest(templateName, null);

        when(documentService.generateDocument(any(DocumentRequest.class)))
                .thenReturn(emptyDocument);


        Response response = documentResource.generateDocument(request);


        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(emptyDocument, response.getEntity());

        verify(documentService, times(1)).generateDocument(request);
    }

    @Test
    @DisplayName("Should handle empty document in code generation")
    void shouldHandleEmptyDocumentInCodeGeneration() throws Exception {

        String templateName = "test-template";
        byte[] emptyDocument = new byte[0];

        DocumentRequest request = createDocumentRequest(templateName, null);

        when(documentService.generateDocument(any(DocumentRequest.class)))
                .thenReturn(emptyDocument);


        Response response = documentResource.generateBase64Document(request);


        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        DocumentResponse documentResponse = (DocumentResponse) response.getEntity();
        assertNotNull(documentResponse);
        assertEquals("", documentResponse.getContent());
        assertEquals(0L, documentResponse.getSize());

        verify(documentService, times(1)).generateDocument(request);
    }

    @Test
    @DisplayName("Should propagate service exceptions in generate document")
    void shouldPropagateServiceExceptionsInGenerateDocument() throws Exception {

        String templateName = "test-template";
        DocumentRequest request = createDocumentRequest(templateName, null);

        RuntimeException expectedException = new RuntimeException("Service error");
        when(documentService.generateDocument(any(DocumentRequest.class)))
                .thenThrow(expectedException);


        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            documentResource.generateDocument(request);
        });

        assertEquals("Service error", thrownException.getMessage());
        verify(documentService, times(1)).generateDocument(request);
    }

    @Test
    @DisplayName("Should propagate service exceptions in generate code document")
    void shouldPropagateServiceExceptionsInGenerateCodeDocument() throws Exception {

        String templateName = "test-template";
        DocumentRequest request = createDocumentRequest(templateName, null);

        RuntimeException expectedException = new RuntimeException("Service error");
        when(documentService.generateDocument(any(DocumentRequest.class)))
                .thenThrow(expectedException);


        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            documentResource.generateBase64Document(request);
        });

        assertEquals("Service error", thrownException.getMessage());
        verify(documentService, times(1)).generateDocument(request);
    }

    @Test
    @DisplayName("Should build filename correctly when template name is used")
    void shouldBuildFilenameFromTemplateName() {

        String templateName = "invoice-template";
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName(templateName);

        byte[] document = "content".getBytes();

        when(documentService.generateDocument(any(DocumentRequest.class)))
                .thenReturn(document);

        Response response = documentResource.generateDocument(request);

        assertEquals("attachment; filename=\"invoice-template.pdf\"",
                response.getHeaderString("Content-Disposition"));
    }

    @Test
    @DisplayName("Should build filename correctly with case insensitive PDF extension check")
    void shouldBuildFilenameWithCaseInsensitivePdfCheck() throws Exception {

        String templateName = "test-template";
        String filenameWithUppercaseExtension = "report.PDF";
        DocumentRequest request = createDocumentRequest(templateName, filenameWithUppercaseExtension);
        byte[] document = "content".getBytes();

        when(documentService.generateDocument(any(DocumentRequest.class)))
                .thenReturn(document);


        Response response = documentResource.generateDocument(request);


        assertEquals("attachment; filename=\"report.PDF\"",
                response.getHeaderString("Content-Disposition"));
    }

    @Test
    @DisplayName("Should handle large documents correctly")
    void shouldHandleLargeDocuments() throws Exception {

        String templateName = "large-template";
        byte[] largeDocument = new byte[1024 * 1024];
        for (int i = 0; i < largeDocument.length; i++) {
            largeDocument[i] = (byte) (i % 256);
        }

        DocumentRequest request = createDocumentRequest(templateName, null);

        when(documentService.generateDocument(any(DocumentRequest.class)))
                .thenReturn(largeDocument);


        Response response = documentResource.generateBase64Document(request);


        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        DocumentResponse documentResponse = (DocumentResponse) response.getEntity();
        assertNotNull(documentResponse);
        assertEquals((long) largeDocument.length, documentResponse.getSize());
        assertNotNull(documentResponse.getContent());
        assertFalse(documentResponse.getContent().isEmpty());

        verify(documentService, times(1)).generateDocument(request);
    }
}