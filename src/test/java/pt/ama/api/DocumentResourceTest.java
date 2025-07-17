package pt.ama.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import pt.ama.dto.TemplateRequest;
import pt.ama.dto.DocumentRequest;
import pt.ama.model.DocumentType;
import pt.ama.util.TemplateTestUtils;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DocumentResourceTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setupTemplates() {
        // Limpar templates existentes e criar novos para cada teste
        cleanupTemplates();
        
        // Criar template simples para testes
        TemplateRequest simpleTemplate = new TemplateRequest();
        simpleTemplate.setName("simple-document");
        simpleTemplate.setType(DocumentType.PDF);
        simpleTemplate.setContent(TemplateTestUtils.getDefaultHtmlTemplate());
        simpleTemplate.setDescription("Template simples para testes de geração");

        given()
                .contentType(ContentType.JSON)
                .body(simpleTemplate)
                .when()
                .post("/api/templates")
                .then()
                .statusCode(201);

        // Criar template complexo para testes avançados
        TemplateRequest complexTemplate = new TemplateRequest();
        complexTemplate.setName("complex-document");
        complexTemplate.setType(DocumentType.PDF);
        complexTemplate.setContent(TemplateTestUtils.loadTemplateFromFile("default-template.html"));
        complexTemplate.setDescription("Template complexo para testes avançados");

        given()
                .contentType(ContentType.JSON)
                .body(complexTemplate)
                .when()
                .post("/api/templates")
                .then()
                .statusCode(201);

        // Criar template para relatório executivo
        TemplateRequest executiveTemplate = new TemplateRequest();
        executiveTemplate.setName("executive-report");
        executiveTemplate.setType(DocumentType.PDF);
        executiveTemplate.setContent(TemplateTestUtils.loadTemplateFromFile("advanced-report.html"));
        executiveTemplate.setDescription("Template para relatórios executivos");

        given()
                .contentType(ContentType.JSON)
                .body(executiveTemplate)
                .when()
                .post("/api/templates")
                .then()
                .statusCode(201);
    }

    private void cleanupTemplates() {
        // Tentar deletar templates que podem existir de testes anteriores
        String[] templateNames = {"simple-document", "complex-document", "executive-report"};
        for (String name : templateNames) {
            given()
                    .when()
                    .delete("/api/templates/" + name)
                    .then()
                    .statusCode(anyOf(204, 404)); // 204 se deletou, 404 se não existia
        }
    }

    @Test
    public void testGenerateSimpleDocument() throws Exception {
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("simple-document");
        
        Map<String, Object> data = TemplateTestUtils.createSimpleTemplateData();
        JsonNode jsonData = objectMapper.valueToTree(data);
        request.setData(jsonData);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/documents/generate")
                .then()
                .statusCode(200)
                .contentType("application/octet-stream")
                .header("Content-Disposition", is("attachment; filename=\"simple-document.pdf\""))
                .body(is(not(empty())));
    }

    @Test
    public void testGenerateComplexDocument() throws Exception {
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("complex-document");
        
        Map<String, Object> data = TemplateTestUtils.createComplexTemplateData();
        JsonNode jsonData = objectMapper.valueToTree(data);
        request.setData(jsonData);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/documents/generate")
                .then()
                .statusCode(200)
                .contentType("application/octet-stream")
                .header("Content-Disposition", is("attachment; filename=\"complex-document.pdf\""))
                .body(is(not(empty())));
    }

    @Test
    public void testGenerateDocumentWithConditionals() throws Exception {
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("complex-document");
        
        Map<String, Object> data = TemplateTestUtils.createConditionalTemplateData();
        JsonNode jsonData = objectMapper.valueToTree(data);
        request.setData(jsonData);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/documents/generate")
                .then()
                .statusCode(200)
                .contentType("application/octet-stream")
                .header("Content-Disposition", is("attachment; filename=\"complex-document.pdf\""))
                .body(is(not(empty())));
    }

    @Test
    public void testGenerateExecutiveReport() throws Exception {
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("executive-report");
        
        // Usar dados do arquivo de exemplo
        String jsonContent = TemplateTestUtils.loadTemplateFromFile("../data/executive-report-sample.json");
        JsonNode jsonData = objectMapper.readTree(jsonContent);
        request.setData(jsonData);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/documents/generate")
                .then()
                .statusCode(200)
                .contentType("application/octet-stream")
                .header("Content-Disposition", is("attachment; filename=\"executive-report.pdf\""))
                .body(is(not(empty())));
    }

    @Test
    public void testGenerateDocumentWithPdfOptions() throws Exception {
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("simple-document");
        
        Map<String, Object> data = TemplateTestUtils.createSimpleTemplateData();
        JsonNode jsonData = objectMapper.valueToTree(data);
        request.setData(jsonData);

        // Configurar opções do PDF
        DocumentRequest.PdfOptions options = new DocumentRequest.PdfOptions();
        options.setFilename("custom-filename.pdf");
        options.setOrientation("landscape");
        options.setPageSize("A3");
        options.setAuthor("Test Author");
        options.setSubject("Test Subject");
        options.setKeywords("test, pdf, generation");
        request.setOptions(options);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/documents/generate")
                .then()
                .statusCode(200)
                .contentType("application/octet-stream")
                .header("Content-Disposition", is("attachment; filename=\"simple-document.pdf\""))
                .body(is(not(empty())));
    }

    @Test
    public void testGenerateDocumentWithNonExistentTemplate() throws Exception {
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("non-existent-template");
        
        Map<String, Object> data = TemplateTestUtils.createSimpleTemplateData();
        JsonNode jsonData = objectMapper.valueToTree(data);
        request.setData(jsonData);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/documents/generate")
                .then()
                .statusCode(404)
                .body(containsString("Template not found"));
    }

    @Test
    public void testGenerateDocumentWithEmptyData() throws Exception {
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("simple-document");
        
        JsonNode emptyData = objectMapper.createObjectNode();
        request.setData(emptyData);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/documents/generate")
                .then()
                .statusCode(200)
                .contentType("application/octet-stream");
    }

    @Test
    public void testGenerateDocumentWithPartialData() throws Exception {
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("complex-document");
        
        // Criar dados parciais para testar condicionais
        Map<String, Object> partialData = Map.of(
            "header", "Documento Parcial",
            "content", "Este documento tem apenas dados básicos.",
            "date", "2024-01-31"
        );
        
        JsonNode jsonData = objectMapper.valueToTree(partialData);
        request.setData(jsonData);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/documents/generate")
                .then()
                .statusCode(200)
                .contentType("application/octet-stream")
                .header("Content-Disposition", is("attachment; filename=\"complex-document.pdf\""));
    }

    @Test
    public void testGenerateDocumentWithSpecialCharacters() throws Exception {
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("simple-document");
        
        Map<String, Object> specialData = Map.of(
            "header", "Documento com Acentos: ção, ã, é, ü",
            "content", "Conteúdo com caracteres especiais: @#$%&*()",
            "recipient", "José da Silva & Cia Ltda.",
            "date", "31/01/2024"
        );
        
        JsonNode jsonData = objectMapper.valueToTree(specialData);
        request.setData(jsonData);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/documents/generate")
                .then()
                .statusCode(200)
                .contentType("application/octet-stream");
    }

    @Test
    public void testGenerateDocumentWithInvalidRequest() {
        // Teste com template name vazio
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("");
        
        JsonNode emptyData = objectMapper.createObjectNode();
        request.setData(emptyData);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/documents/generate")
                .then()
                .statusCode(400);
    }

    @Test
    public void testGenerateDocumentWithNullData() {
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("simple-document");
        request.setData(null);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/documents/generate")
                .then()
                .statusCode(400);
    }

    @Test
    public void testGenerateDocumentWithMalformedJson() {
        String malformedJson = "{\"templateName\":\"simple-document\",\"data\":{\"header\":\"test\",}}";

        given()
                .contentType(ContentType.JSON)
                .body(malformedJson)
                .when()
                .post("/documents/generate")
                .then()
                .statusCode(400);
    }

    @Test
    public void testGenerateDocumentWithLargeData() throws Exception {
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("complex-document");
        
        // Criar dados grandes para testar performance
        Map<String, Object> largeData = TemplateTestUtils.createComplexTemplateData();
        
        // Adicionar muitos itens para testar com dados grandes
        java.util.List<String> manyItems = new java.util.ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            manyItems.add("Item " + i);
        }
        largeData.put("manyItems", manyItems);
        
        JsonNode jsonData = objectMapper.valueToTree(largeData);
        request.setData(jsonData);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/documents/generate")
                .then()
                .statusCode(200)
                .contentType("application/octet-stream");
    }

    @Test
    public void testGenerateDocumentWithNestedObjects() throws Exception {
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("complex-document");
        
        // Criar estrutura de dados aninhada complexa
        Map<String, Object> nestedData = Map.of(
            "company", Map.of(
                "name", "Empresa Teste",
                "address", Map.of(
                    "street", "Rua das Flores, 123",
                    "city", "São Paulo",
                    "state", "SP",
                    "zipCode", "01234-567"
                ),
                "contact", Map.of(
                    "phone", "(11) 1234-5678",
                    "email", "contato@empresa.com"
                )
            ),
            "items", java.util.List.of(
                Map.of("name", "Item 1", "price", 100.0, "quantity", 2),
                Map.of("name", "Item 2", "price", 200.0, "quantity", 1)
            )
        );
        
        JsonNode jsonData = objectMapper.valueToTree(nestedData);
        request.setData(jsonData);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/documents/generate")
                .then()
                .statusCode(200)
                .contentType("application/octet-stream");
    }

    // Método auxiliar para usar anyOf em statusCode
    private static org.hamcrest.Matcher<Integer> anyOf(int... statusCodes) {
        org.hamcrest.Matcher<Integer>[] matchers = new org.hamcrest.Matcher[statusCodes.length];
        for (int i = 0; i < statusCodes.length; i++) {
            matchers[i] = org.hamcrest.CoreMatchers.is(statusCodes[i]);
        }
        return org.hamcrest.CoreMatchers.anyOf(matchers);
    }
}