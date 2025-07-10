package pt.ama.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import pt.ama.dto.TemplateRequest;
import pt.ama.model.DocumentType;
import pt.ama.util.TemplateTestUtils;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TemplateResourceTest {

    @BeforeEach
    public void cleanup() {
        // Limpar templates de teste anteriores
        String[] templateNames = {
            "test-template", "update-test", "search-test-1", "search-test-2", 
            "pdf-template", "email-template", "sms-template", "complex-template"
        };
        
        for (String name : templateNames) {
            given()
                    .when()
                    .delete("/api/templates/" + name)
                    .then()
                    .statusCode(anyOf(204, 404));
        }
    }

    @Test
    public void testCreateTemplate() {
        TemplateRequest request = new TemplateRequest();
        request.setName("test-template");
        request.setType(DocumentType.PDF);
        request.setContent(TemplateTestUtils.getDefaultHtmlTemplate());
        request.setDescription("Template de teste");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/templates")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("name", is("test-template"))
                .body("type", is("PDF"))
                .body("description", is("Template de teste"))
                .body("content", notNullValue());
    }

    @Test
    public void testCreateTemplateWithInvalidData() {
        TemplateRequest request = new TemplateRequest();
        // Nome vazio deve falhar
        request.setName("");
        request.setType(DocumentType.PDF);
        request.setContent("Some content");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/templates")
                .then()
                .statusCode(400);
    }

    @Test
    public void testCreateTemplateWithNullType() {
        TemplateRequest request = new TemplateRequest();
        request.setName("null-type-test");
        request.setType(null);
        request.setContent("Some content");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/templates")
                .then()
                .statusCode(400);
    }

    @Test
    public void testCreateTemplateWithEmptyContent() {
        TemplateRequest request = new TemplateRequest();
        request.setName("empty-content-test");
        request.setType(DocumentType.PDF);
        request.setContent("");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/templates")
                .then()
                .statusCode(400);
    }

    @Test
    public void testCreateDuplicateTemplate() {
        TemplateRequest request = new TemplateRequest();
        request.setName("duplicate-test");
        request.setType(DocumentType.PDF);
        request.setContent(TemplateTestUtils.getDefaultHtmlTemplate());

        // Criar primeiro template
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/templates")
                .then()
                .statusCode(201);

        // Tentar criar novamente com mesmo nome
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/templates")
                .then()
                .statusCode(409); // Conflict
    }

    @Test
    public void testGetAllTemplates() {
        // Criar alguns templates primeiro
        createTestTemplate("list-test-1", DocumentType.PDF);
        createTestTemplate("list-test-2", DocumentType.EMAIL);
        createTestTemplate("list-test-3", DocumentType.SMS);

        given()
                .when()
                .get("/api/templates")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(greaterThanOrEqualTo(3)));
    }

    @Test
    public void testGetTemplateByName() {
        // Criar template primeiro
        createTestTemplate("get-test", DocumentType.PDF);

        given()
                .when()
                .get("/api/templates/get-test")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("name", is("get-test"))
                .body("type", is("PDF"));
    }

    @Test
    public void testGetNonExistentTemplate() {
        given()
                .when()
                .get("/api/templates/non-existent")
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetTemplatesByType() {
        // Criar templates de diferentes tipos
        createTestTemplate("pdf-template", DocumentType.PDF);
        createTestTemplate("email-template", DocumentType.EMAIL);
        createTestTemplate("sms-template", DocumentType.SMS);

        // Buscar templates PDF
        given()
                .when()
                .get("/api/templates/type/PDF")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(greaterThanOrEqualTo(1)));

        // Buscar templates EMAIL
        given()
                .when()
                .get("/api/templates/type/EMAIL")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(greaterThanOrEqualTo(1)));
    }

    @Test
    public void testSearchTemplatesByName() {
        // Criar templates com nomes similares
        createTestTemplate("search-test-1", DocumentType.PDF);
        createTestTemplate("search-test-2", DocumentType.PDF);
        createTestTemplate("other-template", DocumentType.PDF);

        // Buscar por padrão
        given()
                .queryParam("name", "search-test")
                .when()
                .get("/api/templates/search")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(2));

        // Buscar por padrão que não existe
        given()
                .queryParam("name", "non-existent-pattern")
                .when()
                .get("/api/templates/search")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(0));
    }

    @Test
    public void testUpdateTemplate() {
        // Criar template primeiro
        createTestTemplate("update-test", DocumentType.PDF);

        // Atualizar template
        TemplateRequest updateRequest = new TemplateRequest();
        updateRequest.setName("update-test");
        updateRequest.setType(DocumentType.EMAIL);
        updateRequest.setContent("<html><body>Updated content</body></html>");
        updateRequest.setDescription("Updated description");

        given()
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put("/api/templates/update-test")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("name", is("update-test"))
                .body("type", is("EMAIL"))
                .body("description", is("Updated description"));
    }

    @Test
    public void testUpdateNonExistentTemplate() {
        TemplateRequest updateRequest = new TemplateRequest();
        updateRequest.setName("non-existent");
        updateRequest.setType(DocumentType.PDF);
        updateRequest.setContent("Some content");

        given()
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put("/api/templates/non-existent")
                .then()
                .statusCode(404);
    }

    @Test
    public void testDeleteTemplate() {
        // Criar template primeiro
        createTestTemplate("delete-test", DocumentType.PDF);

        // Verificar que existe
        given()
                .when()
                .get("/api/templates/delete-test")
                .then()
                .statusCode(200);

        // Deletar
        given()
                .when()
                .delete("/api/templates/delete-test")
                .then()
                .statusCode(204);

        // Verificar que não existe mais
        given()
                .when()
                .get("/api/templates/delete-test")
                .then()
                .statusCode(404);
    }

    @Test
    public void testDeleteNonExistentTemplate() {
        given()
                .when()
                .delete("/api/templates/non-existent")
                .then()
                .statusCode(404);
    }

    @Test
    public void testGeneratePdfFromTemplate() {
        // Criar template primeiro
        createTestTemplate("pdf-gen-test", DocumentType.PDF);

        // Dados para geração do PDF
        Map<String, Object> data = TemplateTestUtils.createSimpleTemplateData();

        given()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("/api/templates/pdf-gen-test/generate-pdf")
                .then()
                .statusCode(200)
                .contentType("application/octet-stream")
                .body(is(not(empty())));
    }

    @Test
    public void testGeneratePdfFromNonExistentTemplate() {
        Map<String, Object> data = TemplateTestUtils.createSimpleTemplateData();

        given()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("/api/templates/non-existent/generate-pdf")
                .then()
                .statusCode(404);
    }

    @Test
    public void testGeneratePdfWithComplexData() {
        // Criar template complexo
        TemplateRequest complexTemplate = new TemplateRequest();
        complexTemplate.setName("complex-template");
        complexTemplate.setType(DocumentType.PDF);
        complexTemplate.setContent(TemplateTestUtils.loadTemplateFromFile("default-template.html"));
        complexTemplate.setDescription("Template complexo para teste");

        given()
                .contentType(ContentType.JSON)
                .body(complexTemplate)
                .when()
                .post("/api/templates")
                .then()
                .statusCode(201);

        // Gerar PDF com dados complexos
        Map<String, Object> complexData = TemplateTestUtils.createComplexTemplateData();

        given()
                .contentType(ContentType.JSON)
                .body(complexData)
                .when()
                .post("/api/templates/complex-template/generate-pdf")
                .then()
                .statusCode(200)
                .contentType("application/octet-stream")
                .body(is(not(empty())));
    }

    @Test
    public void testGeneratePdfWithEmptyData() {
        createTestTemplate("empty-data-test", DocumentType.PDF);

        given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/api/templates/empty-data-test/generate-pdf")
                .then()
                .statusCode(200)
                .contentType("application/octet-stream");
    }

    @Test
    public void testCreateTemplateWithSpecialCharacters() {
        TemplateRequest request = new TemplateRequest();
        request.setName("template-with-áéíóú");
        request.setType(DocumentType.PDF);
        request.setContent("<html><body>Conteúdo com acentos: ção, ã, é</body></html>");
        request.setDescription("Descrição com caracteres especiais: @#$%&*()");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/templates")
                .then()
                .statusCode(201)
                .body("name", is("template-with-áéíóú"))
                .body("description", is("Descrição com caracteres especiais: @#$%&*()"));
    }

    @Test
    public void testCreateTemplateWithLargeContent() {
        // Criar conteúdo grande
        StringBuilder largeContent = new StringBuilder("<html><body>");
        for (int i = 0; i < 1000; i++) {
            largeContent.append("<p>Este é o parágrafo número ").append(i).append("</p>");
        }
        largeContent.append("</body></html>");

        TemplateRequest request = new TemplateRequest();
        request.setName("large-content-test");
        request.setType(DocumentType.PDF);
        request.setContent(largeContent.toString());
        request.setDescription("Template com conteúdo grande");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/templates")
                .then()
                .statusCode(201);
    }

    @Test
    public void testGetTemplateVersions() {
        // Criar template
        createTestTemplate("version-test", DocumentType.PDF);

        given()
                .when()
                .get("/api/templates/version-test/versions")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(greaterThanOrEqualTo(1)));
    }

    @Test
    public void testGetVersionsOfNonExistentTemplate() {
        given()
                .when()
                .get("/api/templates/non-existent/versions")
                .then()
                .statusCode(anyOf(200, 404)) // Pode retornar lista vazia ou 404
                .contentType(ContentType.JSON);
    }

    // Método auxiliar para criar templates de teste
    private void createTestTemplate(String name, DocumentType type) {
        TemplateRequest request = new TemplateRequest();
        request.setName(name);
        request.setType(type);
        request.setContent(TemplateTestUtils.getDefaultHtmlTemplate());
        request.setDescription("Template de teste: " + name);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/templates")
                .then()
                .statusCode(201);
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