package pt.ama.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import pt.ama.dto.DocumentRequest;
import pt.ama.dto.TemplateRequest;
import pt.ama.dto.TemplateResponse;
import pt.ama.model.DocumentType;
import pt.ama.model.Template;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class TemplateTestUtils {

    public static DocumentRequest createDocumentRequest(String templateName, String filename) throws Exception {
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


    public static Template createTemplate(String name, DocumentType type) {
        Template template = new Template();
        template.setName(name);
        template.setType(type);
        template.setContent(getContentByType(type));
        template.setDescription("Test template");
        template.setAuthor("Test Author");
        template.setVersion(BigDecimal.valueOf(1.0));
        template.setActive(true);
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        return template;
    }

    public static TemplateRequest createTemplateRequest(String name, DocumentType type) {
        TemplateRequest request = new TemplateRequest();
        request.setName(name);
        request.setType(type);
        request.setContent(getContentByType(type));
        request.setDescription("Test template");
        request.setAuthor("Test Author");
        request.setOwner("Test Owner");
        request.setManager("Test Manager");
        request.setCategory("Test Category");
        return request;
    }

    public static TemplateResponse createTemplateResponse(String name, DocumentType type) {
        TemplateResponse response = new TemplateResponse();
        response.setName(name);
        response.setType(type);
        response.setContent(getContentByType(type));
        response.setDescription("Test template");
        response.setAuthor("Test Author");
        response.setVersion(BigDecimal.valueOf(1.0));
        response.setActive(true);
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());
        return response;
    }

    public static String getContentByType(DocumentType type) {
        return switch (type) {
            case PDF -> "<html><body>PDF Test content</body></html>";
            case EMAIL -> "Subject: Test Email\n\nEmail content: {content}";
            case SMS -> "SMS content: {message}";
        };
    }

    public static String loadTemplateFromFile(String filename) {
        try {
            return Files.readString(Paths.get("src/test/resources/templates/" + filename));
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar template: " + filename, e);
        }
    }
    
    public static String getDefaultHtmlTemplate() {
        return "<!DOCTYPE html> <html> <head> <meta charset=\"UTF-8\"> <style> body { font-family: Arial, sans-serif; margin: 40px; line-height: 1.6; } .header { text-align: center; margin-bottom: 30px; } .content { margin: 20px 0; } .footer { margin-top: 50px; text-align: center; font-size: 0.9em; } </style> </head> <body> <div class=\"header\"> <h1>{header}</h1> </div> <div class=\"content\"> {#if recipient} <p>Prezado(a) {recipient},</p> {/if} {content} {#if items} <ul> {#for item in items} <li>{item}</li> {/for} </ul> {/if} </div> <div class=\"footer\"> {#if date} <p>Data: {date}</p> {/if} {#if signature} <p>{signature}</p> {/if} </div> </body> </html>";
    }

    /**
     * Cria dados de exemplo para testar templates complexos
     */
    public static Map<String, Object> createComplexTemplateData() {
        Map<String, Object> data = new HashMap<>();

        data.put("title", "Relatório de Vendas - Janeiro 2024");
        data.put("subtitle", "Análise Mensal de Performance");
        data.put("header", "EMPRESA XYZ LTDA");
        data.put("date", "2024-01-31");
        data.put("documentNumber", "REL-2024-001");
        data.put("version", "1.0");

        Map<String, Object> company = new HashMap<>();
        company.put("name", "Empresa XYZ Ltda");
        company.put("address", "Rua das Flores, 123 - São Paulo/SP");
        company.put("website", "www.empresaxyz.com.br");
        
        Map<String, String> contact = new HashMap<>();
        contact.put("phone", "(11) 1234-5678");
        contact.put("email", "contato@empresaxyz.com.br");
        company.put("contact", contact);
        
        data.put("company", company);

        Map<String, String> recipient = new HashMap<>();
        recipient.put("name", "João Silva");
        recipient.put("company", "Cliente ABC");
        recipient.put("email", "joao@clienteabc.com");
        data.put("recipient", recipient);

        data.put("greeting", "Prezado(a)");
        data.put("content", "<p>Este relatório apresenta os resultados obtidos durante o mês de janeiro de 2024.</p>");

        List<Map<String, Object>> sections = new ArrayList<>();
        
        Map<String, Object> section1 = new HashMap<>();
        section1.put("title", "Resumo Executivo");
        section1.put("content", "<p>Durante o período analisado, observamos crescimento significativo.</p>");
        
        List<Map<String, String>> items1 = new ArrayList<>();
        Map<String, String> item1 = new HashMap<>();
        item1.put("title", "Vendas");
        item1.put("description", "Aumento de 25% em relação ao mês anterior");
        item1.put("value", "R$ 150.000");
        items1.add(item1);
        
        Map<String, String> item2 = new HashMap<>();
        item2.put("title", "Novos Clientes");
        item2.put("description", "Captação de novos clientes");
        item2.put("value", "45 clientes");
        items1.add(item2);
        
        section1.put("items", items1);
        sections.add(section1);
        
        data.put("sections", sections);

        Map<String, Object> tableData = new HashMap<>();
        tableData.put("title", "Vendas por Produto");
        tableData.put("headers", Arrays.asList("Produto", "Quantidade", "Valor Unitário", "Total"));
        
        List<List<String>> rows = new ArrayList<>();
        rows.add(Arrays.asList("Produto A", "100", "R$ 50,00", "R$ 5.000,00"));
        rows.add(Arrays.asList("Produto B", "75", "R$ 80,00", "R$ 6.000,00"));
        rows.add(Arrays.asList("Produto C", "50", "R$ 120,00", "R$ 6.000,00"));
        tableData.put("rows", rows);
        
        tableData.put("totals", Arrays.asList("Total", "225", "-", "R$ 17.000,00"));
        data.put("tableData", tableData);

        List<Map<String, String>> statistics = new ArrayList<>();
        Map<String, String> stat1 = new HashMap<>();
        stat1.put("label", "Total de Vendas");
        stat1.put("value", "R$ 150.000");
        statistics.add(stat1);
        
        Map<String, String> stat2 = new HashMap<>();
        stat2.put("label", "Clientes Ativos");
        stat2.put("value", "1.250");
        statistics.add(stat2);
        
        data.put("statistics", statistics);

        List<Map<String, Object>> progressBars = new ArrayList<>();
        Map<String, Object> progress1 = new HashMap<>();
        progress1.put("label", "Meta de Vendas");
        progress1.put("percentage", 85);
        progressBars.add(progress1);
        
        Map<String, Object> progress2 = new HashMap<>();
        progress2.put("label", "Satisfação do Cliente");
        progress2.put("percentage", 94);
        progressBars.add(progress2);
        
        data.put("progressBars", progressBars);

        List<Map<String, String>> infoBoxes = new ArrayList<>();
        Map<String, String> box1 = new HashMap<>();
        box1.put("type", "success");
        box1.put("title", "Destaque do Mês");
        box1.put("content", "Superamos a meta de vendas em 15%.");
        infoBoxes.add(box1);
        
        Map<String, String> box2 = new HashMap<>();
        box2.put("type", "warning");
        box2.put("title", "Atenção");
        box2.put("content", "Estoque do Produto C está baixo.");
        infoBoxes.add(box2);
        
        data.put("infoBoxes", infoBoxes);

        List<Map<String, String>> conditions = new ArrayList<>();
        Map<String, String> condition1 = new HashMap<>();
        condition1.put("type", "approved");
        condition1.put("message", "Relatório aprovado pela diretoria em 31/01/2024");
        conditions.add(condition1);
        
        data.put("conditions", conditions);

        List<Map<String, Object>> calculations = new ArrayList<>();
        Map<String, Object> calc1 = new HashMap<>();
        calc1.put("label", "Receita Bruta");
        calc1.put("value", "150.000,00");
        calc1.put("currency", "R$");
        calculations.add(calc1);
        
        Map<String, Object> calc2 = new HashMap<>();
        calc2.put("label", "Impostos");
        calc2.put("value", "22.500,00");
        calc2.put("currency", "R$");
        calc2.put("percentage", 15);
        calculations.add(calc2);
        
        data.put("calculations", calculations);

        data.put("observations", Arrays.asList(
            "Todos os dados foram coletados através do sistema ERP da empresa",
            "As projeções são baseadas em dados históricos e tendências de mercado"
        ));

        data.put("terms", "<p>Este documento é confidencial e destinado exclusivamente ao uso interno da empresa.</p>");

        List<Map<String, String>> signatures = new ArrayList<>();
        Map<String, String> sig1 = new HashMap<>();
        sig1.put("name", "Maria Santos");
        sig1.put("title", "Gerente de Vendas");
        sig1.put("date", "31/01/2024");
        signatures.add(sig1);
        
        Map<String, String> sig2 = new HashMap<>();
        sig2.put("name", "Carlos Oliveira");
        sig2.put("title", "Diretor Comercial");
        sig2.put("date", "31/01/2024");
        signatures.add(sig2);
        
        data.put("signatures", signatures);

        data.put("footerText", "Documento gerado automaticamente pelo Sistema de Templates");
        data.put("confidential", true);
        
        return data;
    }

    /**
     * Cria dados simples para testes básicos
     */
    public static Map<String, Object> createSimpleTemplateData() {
        Map<String, Object> data = new HashMap<>();
        data.put("header", "Documento de Teste");
        data.put("content", "Este é um documento de teste simples.");
        data.put("recipient", "João Silva");
        data.put("date", "2024-01-31");
        data.put("items", Arrays.asList("Item 1", "Item 2", "Item 3"));
        data.put("signature", "Assinatura Digital");
        return data;
    }

    /**
     * Cria dados para teste de condicionais
     */
    public static Map<String, Object> createConditionalTemplateData() {
        Map<String, Object> data = new HashMap<>();
        data.put("header", "Teste de Condicionais");
        
        Map<String, String> user = new HashMap<>();
        user.put("role", "admin");
        user.put("name", "Administrador");
        data.put("user", user);
        
        data.put("status", "approved");
        data.put("showDetails", true);
        
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item1 = new HashMap<>();
        item1.put("name", "Item Ativo");
        item1.put("active", true);
        items.add(item1);
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("name", "Item Inativo");
        item2.put("active", false);
        items.add(item2);
        
        data.put("items", items);
        
        return data;
    }
}