package pt.ama.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import pt.ama.dto.DocumentRequest;
import pt.ama.service.DocumentService;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class DocumentResourceIntegrationTest {

    @InjectMock
    DocumentService documentService;

    @Test
    @DisplayName("Should generate document via REST endpoint")
    void shouldGenerateDocumentViaRestEndpoint() throws Exception {

        byte[] mockDocument = "Mock PDF content".getBytes();
        when(documentService.generateDocument(any(DocumentRequest.class)))
                .thenReturn(mockDocument);

        DocumentRequest request = createDocumentRequest("test-template", null);


        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/documents/generate")
        .then()
            .statusCode(200)
            .header("Content-Type", "application/pdf")
            .header("Content-Disposition", containsString("attachment"))
            .header("Content-Disposition", containsString("test-template.pdf"));
    }

    @Test
    @DisplayName("Should generate code document via REST endpoint")
    void shouldGenerateCodeDocumentViaRestEndpoint() throws Exception {

        byte[] mockDocument = "Mock PDF content".getBytes();
        when(documentService.generateDocument(any(DocumentRequest.class)))
                .thenReturn(mockDocument);

        DocumentRequest request = createDocumentRequest("test-template", null);


        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/documents/generate-code")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("filename", equalTo("test-template.pdf"))
            .body("contentType", equalTo("application/pdf"))
            .body("base64Content", notNullValue())
            .body("size", equalTo(mockDocument.length))
            .body("templateName", equalTo("test-template"))
            .body("generatedAt", notNullValue());
    }

    @Test
    @DisplayName("Should return 400 for invalid request")
    void shouldReturn400ForInvalidRequest() {

        DocumentRequest invalidRequest = new DocumentRequest();
        invalidRequest.setTemplateName("");


        given()
            .contentType(ContentType.JSON)
            .body(invalidRequest)
        .when()
            .post("/documents/generate")
        .then()
            .statusCode(400);
    }

    private DocumentRequest createDocumentRequest(String templateName, String filename) throws Exception {
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName(templateName);
        
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode data = objectMapper.readTree("{\"title\": \"Test Document\", \"content\": \"Sample content\"}");
        request.setData(data);
        
        if (filename != null) {
            DocumentRequest.PdfOptions options = new DocumentRequest.PdfOptions();
            options.setFilename(filename);
            request.setOptions(options);
        }
        
        return request;
    }
}