package pt.ama.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ImageResourceTest {

    private static final String TEST_IMAGES_DIR = "target/test-images";
    
    @BeforeEach
    public void setup() throws IOException {
        // Criar diretório para imagens de teste
        Files.createDirectories(Path.of(TEST_IMAGES_DIR));
        
        // Limpar imagens de teste anteriores
        cleanupTestImages();
    }

    private void cleanupTestImages() {
        String[] imageNames = {"test-image", "test-logo", "sample-chart", "profile-pic"};
        for (String name : imageNames) {
            given()
                    .when()
                    .delete("/api/images/" + name)
                    .then()
                    .statusCode(anyOf(204, 404)); // 204 se deletou, 404 se não existia
        }
    }

    @Test
    public void testUploadValidImage() throws IOException {
        File testImage = createTestImage("test-image.png", "PNG");
        
        given()
                .multiPart("file", testImage, "image/png")
                .multiPart("name", "test-image")
                .multiPart("description", "Imagem de teste")
                .multiPart("category", "test")
                .multiPart("owner", "test-user")
                .when()
                .post("/api/images/upload")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("name", is("test-image"))
                .body("originalFilename", is("test-image.png"))
                .body("contentType", is("image/png"))
                .body("description", is("Imagem de teste"))
                .body("category", is("test"))
                .body("owner", is("test-user"))
                .body("id", notNullValue())
                .body("uploadDate", notNullValue());
    }

    @Test
    public void testUploadImageWithoutOptionalFields() throws IOException {
        File testImage = createTestImage("simple-image.jpg", "JPEG");
        
        given()
                .multiPart("file", testImage, "image/jpeg")
                .multiPart("name", "simple-test")
                .when()
                .post("/api/images/upload")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("name", is("simple-test"))
                .body("contentType", is("image/jpeg"));
    }

    @Test
    public void testUploadImageWithInvalidType() throws IOException {
        File testFile = createTestFile("test.txt", "text/plain");
        
        given()
                .multiPart("file", testFile, "text/plain")
                .multiPart("name", "invalid-file")
                .when()
                .post("/api/images/upload")
                .then()
                .statusCode(400)
                .body(containsString("Invalid image type"));
    }

    @Test
    public void testUploadImageWithoutFile() {
        given()
                .multiPart("name", "no-file-test")
                .when()
                .post("/api/images/upload")
                .then()
                .statusCode(400)
                .body(containsString("File is required"));
    }

    @Test
    public void testUploadDuplicateImageName() throws IOException {
        File testImage1 = createTestImage("duplicate.png", "PNG");
        File testImage2 = createTestImage("duplicate2.png", "PNG");
        
        // Upload primeira imagem
        given()
                .multiPart("file", testImage1, "image/png")
                .multiPart("name", "duplicate-name")
                .when()
                .post("/api/images/upload")
                .then()
                .statusCode(201);
        
        // Tentar upload com mesmo nome
        given()
                .multiPart("file", testImage2, "image/png")
                .multiPart("name", "duplicate-name")
                .when()
                .post("/api/images/upload")
                .then()
                .statusCode(400)
                .body(containsString("already exists"));
    }

    @Test
    public void testGetImageByName() throws IOException {
        // Primeiro fazer upload de uma imagem
        File testImage = createTestImage("get-test.png", "PNG");
        
        given()
                .multiPart("file", testImage, "image/png")
                .multiPart("name", "get-test-image")
                .multiPart("description", "Imagem para teste de busca")
                .when()
                .post("/api/images/upload")
                .then()
                .statusCode(201);
        
        // Buscar a imagem
        given()
                .when()
                .get("/api/images/get-test-image")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("name", is("get-test-image"))
                .body("description", is("Imagem para teste de busca"));
    }

    @Test
    public void testGetNonExistentImage() {
        given()
                .when()
                .get("/api/images/non-existent-image")
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetImageAsBase64() throws IOException {
        // Upload de uma imagem
        File testImage = createTestImage("base64-test.png", "PNG");
        
        given()
                .multiPart("file", testImage, "image/png")
                .multiPart("name", "base64-test")
                .when()
                .post("/api/images/upload")
                .then()
                .statusCode(201);
        
        // Buscar como base64
        given()
                .when()
                .get("/api/images/base64-test/base64")
                .then()
                .statusCode(200)
                .contentType("text/plain")
                .body(not(empty()));
    }

    @Test
    public void testGetImagesByCategory() throws IOException {
        // Upload de várias imagens na mesma categoria
        File image1 = createTestImage("cat1.png", "PNG");
        File image2 = createTestImage("cat2.png", "PNG");
        File image3 = createTestImage("other.png", "PNG");
        
        given()
                .multiPart("file", image1, "image/png")
                .multiPart("name", "category-test-1")
                .multiPart("category", "logos")
                .when()
                .post("/api/images/upload")
                .then()
                .statusCode(201);
        
        given()
                .multiPart("file", image2, "image/png")
                .multiPart("name", "category-test-2")
                .multiPart("category", "logos")
                .when()
                .post("/api/images/upload")
                .then()
                .statusCode(201);
        
        given()
                .multiPart("file", image3, "image/png")
                .multiPart("name", "category-test-3")
                .multiPart("category", "charts")
                .when()
                .post("/api/images/upload")
                .then()
                .statusCode(201);
        
        // Buscar por categoria
        given()
                .when()
                .get("/api/images/category/logos")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(2));
        
        given()
                .when()
                .get("/api/images/category/charts")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(1));
    }

    @Test
    public void testGetImagesByOwner() throws IOException {
        // Upload de várias imagens para diferentes owners
        File image1 = createTestImage("owner1.png", "PNG");
        File image2 = createTestImage("owner2.png", "PNG");
        
        given()
                .multiPart("file", image1, "image/png")
                .multiPart("name", "owner-test-1")
                .multiPart("owner", "user1")
                .when()
                .post("/api/images/upload")
                .then()
                .statusCode(201);
        
        given()
                .multiPart("file", image2, "image/png")
                .multiPart("name", "owner-test-2")
                .multiPart("owner", "user1")
                .when()
                .post("/api/images/upload")
                .then()
                .statusCode(201);
        
        // Buscar por owner
        given()
                .when()
                .get("/api/images/owner/user1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(2));
    }

    @Test
    public void testDeleteImage() throws IOException {
        // Upload de uma imagem
        File testImage = createTestImage("delete-test.png", "PNG");
        
        given()
                .multiPart("file", testImage, "image/png")
                .multiPart("name", "delete-test")
                .when()
                .post("/api/images/upload")
                .then()
                .statusCode(201);
        
        // Verificar que existe
        given()
                .when()
                .get("/api/images/delete-test")
                .then()
                .statusCode(200);
        
        // Deletar
        given()
                .when()
                .delete("/api/images/delete-test")
                .then()
                .statusCode(204);
        
        // Verificar que não existe mais
        given()
                .when()
                .get("/api/images/delete-test")
                .then()
                .statusCode(404);
    }

    @Test
    public void testDeleteNonExistentImage() {
        given()
                .when()
                .delete("/api/images/non-existent")
                .then()
                .statusCode(anyOf(204, 500)); // Pode retornar 204 ou 500 dependendo da implementação
    }

    @Test
    public void testUploadLargeImage() throws IOException {
        // Criar uma imagem maior para testar limites
        File largeImage = createLargeTestImage("large-image.png");
        
        given()
                .multiPart("file", largeImage, "image/png")
                .multiPart("name", "large-test")
                .when()
                .post("/api/images/upload")
                .then()
                .statusCode(anyOf(201, 413)); // 201 se aceito, 413 se muito grande
    }

    @Test
    public void testUploadImageWithSpecialCharactersInName() throws IOException {
        File testImage = createTestImage("special.png", "PNG");
        
        given()
                .multiPart("file", testImage, "image/png")
                .multiPart("name", "test-with-special-chars-áéíóú")
                .multiPart("description", "Descrição com acentos: ção, ã, é")
                .when()
                .post("/api/images/upload")
                .then()
                .statusCode(201)
                .body("name", is("test-with-special-chars-áéíóú"))
                .body("description", is("Descrição com acentos: ção, ã, é"));
    }

    // Métodos auxiliares para criar arquivos de teste
    private File createTestImage(String filename, String format) throws IOException {
        File file = new File(TEST_IMAGES_DIR, filename);
        
        // Criar uma imagem simples de 1x1 pixel
        byte[] imageData;
        if ("PNG".equals(format)) {
            // PNG mínimo de 1x1 pixel transparente
            imageData = Base64.getDecoder().decode(
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAI9jU8j8wAAAABJRU5ErkJggg=="
            );
        } else {
            // JPEG mínimo de 1x1 pixel
            imageData = Base64.getDecoder().decode(
                "/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/2wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwA/8A8A"
            );
        }
        
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(imageData);
        }
        
        return file;
    }

    private File createTestFile(String filename, String contentType) throws IOException {
        File file = new File(TEST_IMAGES_DIR, filename);
        
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("This is a test file content".getBytes());
        }
        
        return file;
    }

    private File createLargeTestImage(String filename) throws IOException {
        File file = new File(TEST_IMAGES_DIR, filename);
        
        // Criar um arquivo de ~1MB para testar limites
        byte[] largeData = new byte[1024 * 1024]; // 1MB
        java.util.Arrays.fill(largeData, (byte) 0xFF);
        
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(largeData);
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