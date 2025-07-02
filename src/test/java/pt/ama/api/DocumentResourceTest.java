//package pt.ama.api;
//
//import io.quarkus.test.junit.QuarkusTest;
//import io.restassured.http.ContentType;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import pt.ama.dto.TemplateRequest;
//import pt.ama.model.DocumentRequest;
//import pt.ama.model.DocumentType;
//import pt.ama.util.TemplateTestUtils;
//
//import static io.restassured.RestAssured.given;
//import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.CoreMatchers.not;
//import static org.hamcrest.Matchers.empty;
//
//@QuarkusTest
//public class DocumentResourceTest {
//
//    @BeforeEach
//    public void setupTemplates() {
//        // Criar template simples para testes
//        TemplateRequest simpleTemplate = new TemplateRequest();
//        simpleTemplate.setName("simple-document");
//        simpleTemplate.setType(DocumentType.PDF);
//        simpleTemplate.setContent(TemplateTestUtils.getDefaultHtmlTemplate());
//        simpleTemplate.setDescription("Template simples para testes de geração");
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(simpleTemplate)
//                .when()
//                .post("/api/templates")
//                .then()
//                .statusCode(201);
//
//        // Criar template complexo para testes avançados
//        TemplateRequest complexTemplate = new TemplateRequest();
//        complexTemplate.setName("complex-document");
//        complexTemplate.setType(DocumentType.PDF);
//        complexTemplate.setContent(TemplateTestUtils.loadTemplateFromFile("default-template.html"));
//        complexTemplate.setDescription("Template complexo para testes avançados");
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(complexTemplate)
//                .when()
//                .post("/api/templates")
//                .then()
//                .statusCode(201);
//    }
//
//    @Test
//    public void testGenerateSimpleDocument() {
//        DocumentRequest request = new DocumentRequest();
//        request.setTemplateName("simple-document");
//        request.setMetadata(TemplateTestUtils.createSimpleTemplateData());
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(request)
//                .when()
//                .post("/documents/generate")
//                .then()
//                .statusCode(200)
//                .contentType("application/octet-stream")
//                .header("Content-Disposition", is("attachment; filename=\"simple-document.pdf\""))
//                .body(is(not(empty())));
//    }
//
//    @Test
//    public void testGenerateComplexDocument() {
//        DocumentRequest request = new DocumentRequest();
//        request.setTemplateName("complex-document");
//        request.setMetadata(TemplateTestUtils.createComplexTemplateData());
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(request)
//                .when()
//                .post("/documents/generate")
//                .then()
//                .statusCode(200)
//                .contentType("application/octet-stream")
//                .header("Content-Disposition", is("attachment; filename=\"complex-document.pdf\""))
//                .body(is(not(empty())));
//    }
//
//    @Test
//    public void testGenerateDocumentWithConditionals() {
//        DocumentRequest request = new DocumentRequest();
//        request.setTemplateName("complex-document");
//        request.setMetadata(TemplateTestUtils.createConditionalTemplateData());
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(request)
//                .when()
//                .post("/documents/generate")
//                .then()
//                .statusCode(200)
//                .contentType("application/octet-stream")
//                .header("Content-Disposition", is("attachment; filename=\"complex-document.pdf\""))
//                .body(is(not(empty())));
//    }
//
//    @Test
//    public void testGenerateDocumentWithNonExistentTemplate() {
//        DocumentRequest request = new DocumentRequest();
//        request.setTemplateName("non-existent-template");
//        request.setMetadata(TemplateTestUtils.createSimpleTemplateData());
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(request)
//                .when()
//                .post("/documents/generate")
//                .then()
//                .statusCode(404);
//    }
//
//    @Test
//    public void testGenerateDocumentWithEmptyMetadata() {
//        DocumentRequest request = new DocumentRequest();
//        request.setTemplateName("simple-document");
//        request.setMetadata(new java.util.HashMap<>());
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(request)
//                .when()
//                .post("/documents/generate")
//                .then()
//                .statusCode(200)
//                .contentType("application/octet-stream");
//    }
//
//    @Test
//    public void testGenerateDocumentWithPartialData() {
//        DocumentRequest request = new DocumentRequest();
//        request.setTemplateName("complex-document");
//
//        // Criar dados parciais para testar condicionais
//        java.util.Map<String, Object> partialData = new java.util.HashMap<>();
//        partialData.put("header", "Documento Parcial");
//        partialData.put("content", "Este documento tem apenas dados básicos.");
//        partialData.put("date", "2024-01-31");
//
//        request.setMetadata(partialData);
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(request)
//                .when()
//                .post("/documents/generate")
//                .then()
//                .statusCode(200)
//                .contentType("application/octet-stream")
//                .header("Content-Disposition", is("attachment; filename=\"complex-document.pdf\""));
//    }
//
//    @Test
//    public void testGenerateDocumentWithSpecialCharacters() {
//        DocumentRequest request = new DocumentRequest();
//        request.setTemplateName("simple-document");
//
//        java.util.Map<String, Object> specialData = new java.util.HashMap<>();
//        specialData.put("header", "Documento com Acentos: ção, ã, é, ü");
//        specialData.put("content", "Conteúdo com caracteres especiais: @#$%&*()");
//        specialData.put("recipient", "José da Silva & Cia Ltda.");
//        specialData.put("date", "31/01/2024");
//
//        request.setMetadata(specialData);
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(request)
//                .when()
//                .post("/documents/generate")
//                .then()
//                .statusCode(200)
//                .contentType("application/octet-stream");
//    }
//}

package pt.ama.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ama.dto.TemplateRequest;
import pt.ama.model.DocumentRequest;
import pt.ama.model.DocumentType;
import pt.ama.util.TemplateTestUtils;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.empty;

@QuarkusTest
public class DocumentResourceTest {

    @BeforeEach
    public void setupTemplates() {
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
    }

    @Test
    public void testGenerateSimpleDocument() {
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("simple-document");
        request.setMetadata(TemplateTestUtils.createSimpleTemplateData());

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
    public void testGenerateComplexDocument() {
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("complex-document");
        request.setMetadata(TemplateTestUtils.createComplexTemplateData());

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
    public void testGenerateDocumentWithConditionals() {
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("complex-document");
        request.setMetadata(TemplateTestUtils.createConditionalTemplateData());

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
    public void testGenerateDocumentWithNonExistentTemplate() {
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("non-existent-template");
        request.setMetadata(TemplateTestUtils.createSimpleTemplateData());

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/documents/generate")
                .then()
                .statusCode(404);
    }

    @Test
    public void testGenerateDocumentWithEmptyMetadata() {
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("simple-document");
        request.setMetadata(new java.util.HashMap<>());

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
    public void testGenerateDocumentWithPartialData() {
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("complex-document");
        
        // Criar dados parciais para testar condicionais
        java.util.Map<String, Object> partialData = new java.util.HashMap<>();
        partialData.put("header", "Documento Parcial");
        partialData.put("content", "Este documento tem apenas dados básicos.");
        partialData.put("date", "2024-01-31");
        
        request.setMetadata(partialData);

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
    public void testGenerateDocumentWithSpecialCharacters() {
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("simple-document");
        
        java.util.Map<String, Object> specialData = new java.util.HashMap<>();
        specialData.put("header", "Documento com Acentos: ção, ã, é, ü");
        specialData.put("content", "Conteúdo com caracteres especiais: @#$%&*()");
        specialData.put("recipient", "José da Silva & Cia Ltda.");
        specialData.put("date", "31/01/2024");
        
        request.setMetadata(specialData);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/documents/generate")
                .then()
                .statusCode(200)
                .contentType("application/octet-stream");
    }
}