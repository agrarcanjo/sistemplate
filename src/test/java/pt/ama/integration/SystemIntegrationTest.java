package pt.ama.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import pt.ama.dto.TemplateRequest;
import pt.ama.dto.DocumentRequest;
import pt.ama.model.DocumentType;
import pt.ama.util.TemplateTestUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SystemIntegrationTest {

    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String TEST_IMAGES_DIR = "target/integration-test-images";

    @BeforeEach
    public void setup() throws IOException {
        // Criar diretório para imagens de teste
        java.nio.file.Files.createDirectories(java.nio.file.Path.of(TEST_IMAGES_DIR));
    }

    @Test
    @Order(1)
    public void testCompleteWorkflow_CreateTemplateUploadImageGenerateDocument() throws Exception {
        // 1. Criar um template que usa imagens
        String templateWithImage = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; }
                    .header { text-align: center; margin-bottom: 30px; }
                    .logo { max-width: 200px; height: auto; }
                    .content { margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>{title}</h1>
                    {#if company.logo}
                    <img src="{company.logo}" alt="Logo" class="logo" />
                    {/if}
                </div>
                <div class="content">
                    <p>Empresa: {company.name}</p>
                    <p>Data: {date}</p>
                    <p>{content}</p>
                </div>
            </body>
            </html>
            """;

        TemplateRequest templateRequest = new TemplateRequest();
        templateRequest.setName("integration-template");
        templateRequest.setType(DocumentType.PDF);
        templateRequest.setContent(templateWithImage);
        templateRequest.setDescription("Template de integração com imagem");

        // Criar template
        given()
                .contentType(ContentType.JSON)
                .body(templateRequest)
                .when()
                .post("/api/templates")
                .then()
                .statusCode(201)
                .body("name", is("integration-template"));

        // 2. Upload de uma imagem
        File testImage = createTestImage("company-logo.png");
        
        given()
                .multiPart("file", testImage, "image/png")
                .multiPart("name", "company-logo")
                .multiPart("description", "Logo da empresa")
                .multiPart("category", "logos")
                .multiPart("owner", "integration-test")
                .when()
                .post("/api/images/upload")
                .then()
                .statusCode(201)
                .body("name", is("company-logo"));

        // 3. Obter a imagem como base64 para usar no template
        String base64Image = given()
                .when()
                .get("/api/images/company-logo/base64")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        // 4. Gerar documento usando o template e a imagem
        Map<String, Object> documentData = Map.of(
            "title", "Relatório de Integração",
            "company", Map.of(
                "name", "Empresa de Teste Ltda",
                "logo", "data:image/png;base64," + base64Image
            ),
            "date", "2024-01-31",
            "content", "Este documento foi gerado através do teste de integração completo."
        );

        DocumentRequest documentRequest = new DocumentRequest();
        documentRequest.setTemplateName("integration-template");
        documentRequest.setData(objectMapper.valueToTree(documentData));

        given()
                .contentType(ContentType.JSON)
                .body(documentRequest)
                .when()
                .post("/documents/generate")
                .then()
                .statusCode(200)
                .contentType("application/octet-stream")
                .header("Content-Disposition", is("attachment; filename=\"integration-template.pdf\""))
                .body(is(not(empty())));

        // 5. Verificar que o template ainda existe
        given()
                .when()
                .get("/api/templates/integration-template")
                .then()
                .statusCode(200)
                .body("name", is("integration-template"));

        // 6. Verificar que a imagem ainda existe
        given()
                .when()
                .get("/api/images/company-logo")
                .then()
                .statusCode(200)
                .body("name", is("company-logo"));
    }

    @Test
    @Order(2)
    public void testMultipleTemplatesAndDocuments() throws Exception {
        // Criar múltiplos templates
        String[] templateNames = {"template-1", "template-2", "template-3"};
        DocumentType[] types = {DocumentType.PDF, DocumentType.EMAIL, DocumentType.SMS};

        for (int i = 0; i < templateNames.length; i++) {
            TemplateRequest request = new TemplateRequest();
            request.setName(templateNames[i]);
            request.setType(types[i]);
            request.setContent(TemplateTestUtils.getDefaultHtmlTemplate());
            request.setDescription("Template " + (i + 1) + " para teste múltiplo");

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/api/templates")
                    .then()
                    .statusCode(201);
        }

        // Verificar que todos foram criados
        given()
                .when()
                .get("/api/templates")
                .then()
                .statusCode(200)
                .body("", hasSize(greaterThan(3))); // Pelo menos os 3 que criamos + outros de testes anteriores

        // Gerar documentos apenas dos templates PDF
        for (String templateName : templateNames) {
            if (templateName.equals("template-1")) { // Apenas o PDF
                DocumentRequest docRequest = new DocumentRequest();
                docRequest.setTemplateName(templateName);
                docRequest.setData(objectMapper.valueToTree(TemplateTestUtils.createSimpleTemplateData()));

                given()
                        .contentType(ContentType.JSON)
                        .body(docRequest)
                        .when()
                        .post("/documents/generate")
                        .then()
                        .statusCode(200)
                        .contentType("application/octet-stream");
            }
        }
    }

    @Test
    @Order(3)
    public void testErrorHandlingWorkflow() throws Exception {
        // 1. Tentar gerar documento com template inexistente
        DocumentRequest request = new DocumentRequest();
        request.setTemplateName("non-existent-template");
        request.setData(objectMapper.valueToTree(TemplateTestUtils.createSimpleTemplateData()));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/documents/generate")
                .then()
                .statusCode(404);

        // 2. Tentar fazer upload de arquivo inválido
        File invalidFile = createInvalidFile("invalid.txt");

        given()
                .multiPart("file", invalidFile, "text/plain")
                .multiPart("name", "invalid-file")
                .when()
                .post("/api/images/upload")
                .then()
                .statusCode(400);

        // 3. Tentar criar template com dados inválidos
        TemplateRequest invalidTemplate = new TemplateRequest();
        invalidTemplate.setName(""); // Nome vazio
        invalidTemplate.setType(DocumentType.PDF);
        invalidTemplate.setContent("Some content");

        given()
                .contentType(ContentType.JSON)
                .body(invalidTemplate)
                .when()
                .post("/api/templates")
                .then()
                .statusCode(400);

        // 4. Tentar buscar imagem inexistente
        given()
                .when()
                .get("/api/images/non-existent-image")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(4)
    public void testSearchAndFilteringWorkflow() throws Exception {
        // Criar templates com diferentes categorias
        String[] categories = {"reports", "invoices", "letters"};
        
        for (int i = 0; i < categories.length; i++) {
            TemplateRequest request = new TemplateRequest();
            request.setName("search-template-" + categories[i]);
            request.setType(DocumentType.PDF);
            request.setContent(TemplateTestUtils.getDefaultHtmlTemplate());
            request.setDescription("Template para " + categories[i]);

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/api/templates")
                    .then()
                    .statusCode(201);
        }

        // Upload de imagens com diferentes categorias
        for (String category : categories) {
            File image = createTestImage(category + "-image.png");
            
            given()
                    .multiPart("file", image, "image/png")
                    .multiPart("name", category + "-image")
                    .multiPart("category", category)
                    .multiPart("owner", "search-test")
                    .when()
                    .post("/api/images/upload")
                    .then()
                    .statusCode(201);
        }

        // Buscar templates por padrão
        given()
                .queryParam("name", "search-template")
                .when()
                .get("/api/templates/search")
                .then()
                .statusCode(200)
                .body("", hasSize(3));

        // Buscar templates por tipo
        given()
                .when()
                .get("/api/templates/type/PDF")
                .then()
                .statusCode(200)
                .body("", hasSize(greaterThan(0)));

        // Buscar imagens por categoria
        given()
                .when()
                .get("/api/images/category/reports")
                .then()
                .statusCode(200)
                .body("", hasSize(1));

        // Buscar imagens por owner
        given()
                .when()
                .get("/api/images/owner/search-test")
                .then()
                .statusCode(200)
                .body("", hasSize(3));
    }

    @Test
    @Order(5)
    public void testComplexDocumentGeneration() throws Exception {
        // Criar template complexo
        TemplateRequest complexTemplate = new TemplateRequest();
        complexTemplate.setName("complex-integration-template");
        complexTemplate.setType(DocumentType.PDF);
        complexTemplate.setContent(TemplateTestUtils.loadTemplateFromFile("advanced-report.html"));
        complexTemplate.setDescription("Template complexo para integração");

        given()
                .contentType(ContentType.JSON)
                .body(complexTemplate)
                .when()
                .post("/api/templates")
                .then()
                .statusCode(201);

        // Usar dados do arquivo de exemplo
        String jsonContent = TemplateTestUtils.loadTemplateFromFile("../data/executive-report-sample.json");
        JsonNode complexData = objectMapper.readTree(jsonContent);

        DocumentRequest docRequest = new DocumentRequest();
        docRequest.setTemplateName("complex-integration-template");
        docRequest.setData(complexData);

        // Configurar opções avançadas do PDF
        DocumentRequest.PdfOptions options = new DocumentRequest.PdfOptions();
        options.setFilename("complex-report.pdf");
        options.setAuthor("Sistema de Templates");
        options.setSubject("Relatório Executivo");
        options.setKeywords("relatório, executivo, integração");
        docRequest.setOptions(options);

        given()
                .contentType(ContentType.JSON)
                .body(docRequest)
                .when()
                .post("/documents/generate")
                .then()
                .statusCode(200)
                .contentType("application/octet-stream")
                .header("Content-Disposition", is("attachment; filename=\"complex-integration-template.pdf\""))
                .body(is(not(empty())));
    }

    @Test
    @Order(6)
    public void testCleanupWorkflow() {
        // Deletar templates criados nos testes
        String[] templatesCreated = {
            "integration-template", "template-1", "template-2", "template-3",
            "search-template-reports", "search-template-invoices", "search-template-letters",
            "complex-integration-template"
        };

        for (String templateName : templatesCreated) {
            given()
                    .when()
                    .delete("/api/templates/" + templateName)
                    .then()
                    .statusCode(anyOf(204, 404));
        }

        // Deletar imagens criadas nos testes
        String[] imagesCreated = {
            "company-logo", "reports-image", "invoices-image", "letters-image"
        };

        for (String imageName : imagesCreated) {
            given()
                    .when()
                    .delete("/api/images/" + imageName)
                    .then()
                    .statusCode(anyOf(204, 404, 500));
        }
    }

    // Métodos auxiliares
    private File createTestImage(String filename) throws IOException {
        File file = new File(TEST_IMAGES_DIR, filename);
        
        // PNG mínimo de 1x1 pixel transparente
        byte[] imageData = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAI9jU8j8wAAAABJRU5ErkJggg=="
        );
        
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(imageData);
        }
        
        return file;
    }

    private File createInvalidFile(String filename) throws IOException {
        File file = new File(TEST_IMAGES_DIR, filename);
        
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("This is not an image file".getBytes());
        }
        
        return file;
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