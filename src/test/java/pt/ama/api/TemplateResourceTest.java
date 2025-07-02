package pt.ama.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import pt.ama.dto.TemplateRequest;
import pt.ama.model.DocumentType;
import pt.ama.util.TemplateTestUtils;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class TemplateResourceTest {

    @Test
    public void testCreateTemplate() {
        String templateRequest = """
                {
                    "name": "test-template",
                    "type": "PDF",
                    "content": "<!DOCTYPE html> <html> <head> <meta charset=\\"UTF-8\\"> <style> body { font-family: Arial, sans-serif; margin: 40px; line-height: 1.6; } .header { text-align: center; margin-bottom: 30px; } .content { margin: 20px 0; } .footer { margin-top: 50px; text-align: center; font-size: 0.9em; } </style> </head> <body> <div class=\\"header\\"> <h1>{header}</h1> </div> <div class=\\"content\\"> {#if recipient} <p>Prezado(a) {recipient},</p> {/if} {content} {#if items} <ul> {#for item in items} <li>{item}</li> {/for} </ul> {/if} </div> <div class=\\"footer\\"> {#if date} <p>Data: {date}</p> {/if} {#if signature} <p>{signature}</p> {/if} </div> </body> </html>",
                    "description": "Template de teste"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(templateRequest)
                .when()
                .post("/api/templates")
                .then()
                .statusCode(201)
                .body("name", is("test-template"))
                .body("type", is("PDF"))
                .body("description", is("Template de teste"));
    }

    @Test
    public void testCreateTemplateWithValidationError() {
        String invalidRequest = """
                {
                    "description": "Template sem campos obrigatórios"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(invalidRequest)
                .when()
                .post("/api/templates")
                .then()
                .statusCode(400);
    }

    @Test
    public void testGetAllTemplates() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/templates")
                .then()
                .statusCode(200)
                .body(notNullValue());
    }

    @Test
    public void testCreateTemplateWithComplexHtml() {

        TemplateRequest templateRequest = new TemplateRequest();
        templateRequest.setName("complex-template");
        templateRequest.setName("complex-template");
        String htmlContent = TemplateTestUtils.getDefaultHtmlTemplate();
        templateRequest.setType(DocumentType.PDF);
        templateRequest.setContent(htmlContent);
        templateRequest.setDescription("Template complexo com HTML");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(templateRequest)
                .when()
                .post("/api/templates")
                .then()
                .statusCode(201)
                .body("name", is("complex-template"))
                .body("type", is("PDF"));
    }

    @Test
    public void testCreateAdvancedTemplate() {
        TemplateRequest templateRequest = new TemplateRequest();
        templateRequest.setName("advanced-report-template");
        templateRequest.setType(DocumentType.PDF);
        templateRequest.setContent(TemplateTestUtils.loadTemplateFromFile("default-template.html"));
        templateRequest.setDescription("Template avançado para relatórios com funcionalidades dinâmicas");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(templateRequest)
                .when()
                .post("/api/templates")
                .then()
                .statusCode(201)
                .body("name", is("advanced-report-template"))
                .body("type", is("PDF"))
                .body("description", is("Template avançado para relatórios com funcionalidades dinâmicas"));
    }

    @Test
    public void testGetTemplateByName() {
        // Primeiro criar um template
        TemplateRequest templateRequest = new TemplateRequest();
        templateRequest.setName("get-test-template");
        templateRequest.setType(DocumentType.PDF);
        templateRequest.setContent("<html><body><h1>Get Test</h1></body></html>");
        templateRequest.setDescription("Template para teste de busca");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(templateRequest)
                .when()
                .post("/api/templates")
                .then()
                .statusCode(201);

        // Agora buscar o template criado
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/templates/get-test-template")
                .then()
                .statusCode(200)
                .body("name", is("get-test-template"))
                .body("type", is("PDF"))
                .body("description", is("Template para teste de busca"));
    }

    @Test
    public void testUpdateTemplate() {
        // Primeiro criar um template
        TemplateRequest createRequest = new TemplateRequest();
        createRequest.setName("update-test-template");
        createRequest.setType(DocumentType.PDF);
        createRequest.setContent("<html><body><h1>Original</h1></body></html>");
        createRequest.setDescription("Template original");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(createRequest)
                .when()
                .post("/api/templates")
                .then()
                .statusCode(201);

        // Agora atualizar o template
        TemplateRequest updateRequest = new TemplateRequest();
        updateRequest.setName("update-test-template");
        updateRequest.setType(DocumentType.PDF);
        updateRequest.setContent("<html><body><h1>Atualizado</h1></body></html>");
        updateRequest.setDescription("Template atualizado");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put("/api/templates/update-test-template")
                .then()
                .statusCode(200)
                .body("name", is("update-test-template"))
                .body("description", is("Template atualizado"));
    }
}