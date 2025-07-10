package pt.ama.test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class CssRenderingTest {

    @BeforeEach
    void setup() {
        // Limpar templates existentes
        given()
            .when()
            .delete("/api/templates/carta-simples")
            .then()
            .statusCode(anyOf(is(204), is(404)));
    }

    @Test
    public void testCssRenderingWithLiteralSections() {
        // Criar template com CSS corrigido usando seções literais
        String templateContent = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Carta Comercial</title><style>{|body{font-family:Arial,sans-serif;margin:40px;line-height:1.6}.header{text-align:center;margin-bottom:30px}.logo{max-width:120px;height:auto}.title{color:#333;font-size:24px;margin:10px 0}.date{text-align:right;margin-bottom:20px}.recipient{margin-bottom:20px}.content{margin:20px 0;text-align:justify}.signature{margin-top:40px;text-align:center}.signature-line{border-top:1px solid #333;width:200px;margin:40px auto 10px}|}</style></head><body><div class=\"header\">{#if company.logo}<img src=\"{company.logo}\" alt=\"Logo\" class=\"logo\">{/if}<h1 class=\"title\">{company.name}</h1></div><div class=\"date\">{date}</div><div class=\"recipient\"><strong>Para:</strong><br>{recipient.name}<br>{recipient.company}<br>{recipient.address}</div><div class=\"content\"><p>{greeting},</p><p>{message}</p>{#if items}<ul>{#for item in items}<li>{item}</li>{/for}</ul>{/if}<p>{closing}</p></div><div class=\"signature\"><div class=\"signature-line\"></div><p><strong>{sender.name}</strong><br>{sender.title}<br>{sender.contact}</p></div></body></html>";
        
        // Criar template
        given()
            .contentType(ContentType.JSON)
            .body("{\n" +
                "  \"name\": \"carta-simples\",\n" +
                "  \"type\": \"PDF\",\n" +
                "  \"description\": \"Template simples para cartas comerciais\",\n" +
                "  \"content\": \"" + templateContent.replace("\"", "\\\"") + "\"\n" +
                "}")
            .when()
            .post("/api/templates")
            .then()
            .statusCode(201);

        // Testar geração de documento
        given()
            .contentType(ContentType.JSON)
            .body("{\n" +
                "  \"templateName\": \"carta-simples\",\n" +
                "  \"data\": {\n" +
                "    \"company\": {\n" +
                "      \"name\": \"Minha Empresa Ltda\",\n" +
                "      \"logo\": \"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAI9jU8j8wAAAABJRU5ErkJggg==\"\n" +
                "    },\n" +
                "    \"date\": \"31 de Janeiro de 2024\",\n" +
                "    \"recipient\": {\n" +
                "      \"name\": \"João Silva\",\n" +
                "      \"company\": \"Cliente Importante S.A.\",\n" +
                "      \"address\": \"Rua das Flores, 123 - São Paulo/SP\"\n" +
                "    },\n" +
                "    \"greeting\": \"Prezado Sr. João\",\n" +
                "    \"message\": \"Temos o prazer de apresentar nossa nova linha de produtos.\",\n" +
                "    \"items\": [\n" +
                "      \"Produto A - Solução completa\",\n" +
                "      \"Produto B - Sistema de monitoramento\"\n" +
                "    ],\n" +
                "    \"closing\": \"Aguardamos seu contato.\",\n" +
                "    \"sender\": {\n" +
                "      \"name\": \"Maria Santos\",\n" +
                "      \"title\": \"Gerente Comercial\",\n" +
                "      \"contact\": \"maria@empresa.com\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"options\": {\n" +
                "    \"filename\": \"carta-teste.pdf\",\n" +
                "    \"orientation\": \"portrait\",\n" +
                "    \"pageSize\": \"A4\"\n" +
                "  }\n" +
                "}")
            .when()
            .post("/documents/generate")
            .then()
            .statusCode(200)
            .contentType("application/pdf")
            .header("Content-Disposition", containsString("carta-teste.pdf"));
    }
}