package pt.ama.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import io.quarkus.qute.Engine;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pt.ama.model.DocumentRequest;
import pt.ama.service.ImageAssetService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

@ApplicationScoped
public class PdfGenerator {

    @Inject
    Engine quteEngine;
    
    @Inject
    ImageAssetService imageService;
    
    @Inject
    ObjectMapper objectMapper;

    public byte[] generatePdf(String templateContent, JsonNode data, DocumentRequest.PdfOptions options) {
        try {
            // Converter JsonNode para Map para compatibilidade com Qute
            Map<String, Object> dataMap = convertJsonNodeToMap(data);
            
            // Processar imagens no template
            dataMap = processImages(dataMap);
            
            // Renderizar template
            Template template = quteEngine.parse(templateContent);
            TemplateInstance instance = template.data(dataMap);
            String renderedHtml = instance.render();

            // Configurar propriedades do PDF
            ConverterProperties properties = new ConverterProperties();
            
            // Configurar orientação e tamanho da página se especificado
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            PdfWriter pdfWriter = new PdfWriter(pdfOutputStream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            
            // Adicionar metadados se especificado
            if (options != null && options.getIncludeMetadata()) {
                PdfDocumentInfo info = pdfDocument.getDocumentInfo();
                if (options.getAuthor() != null) info.setAuthor(options.getAuthor());
                if (options.getSubject() != null) info.setSubject(options.getSubject());
                if (options.getKeywords() != null) info.setKeywords(options.getKeywords());
            }
            
            HtmlConverter.convertToPdf(
                new ByteArrayInputStream(renderedHtml.getBytes("UTF-8")),
                pdfDocument,
                properties
            );

            pdfDocument.close();
            return pdfOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF: " + e.getMessage(), e);
        }
    }
    
    // Método de compatibilidade com a versão anterior
    public byte[] generatePdf(String templateContent, Map<String, Object> data) {
        try {
            JsonNode jsonNode = objectMapper.valueToTree(data);
            return generatePdf(templateContent, jsonNode, null);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF: " + e.getMessage(), e);
        }
    }
    
    private Map<String, Object> convertJsonNodeToMap(JsonNode jsonNode) {
        try {
            return objectMapper.convertValue(jsonNode, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter JSON para Map: " + e.getMessage(), e);
        }
    }
    
    private Map<String, Object> processImages(Map<String, Object> data) {
        Map<String, Object> processedData = new HashMap<>(data);
        
        // Processar imagens recursivamente
        processImagesRecursive(processedData);
        
        return processedData;
    }
    
    @SuppressWarnings("unchecked")
    private void processImagesRecursive(Object obj) {
        if (obj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) obj;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                
                // Se a chave termina com "_image" ou "Image", processar como imagem
                if ((key.endsWith("_image") || key.endsWith("Image") || key.equals("logo")) && value instanceof String) {
                    String imageName = (String) value;
                    String base64Image = imageService.getImageAsBase64(imageName);
                    if (base64Image != null) {
                        map.put(key, base64Image);
                    }
                } else if (value instanceof Map || value instanceof Iterable) {
                    processImagesRecursive(value);
                }
            }
        } else if (obj instanceof Iterable) {
            for (Object item : (Iterable<?>) obj) {
                processImagesRecursive(item);
            }
        }
    }
}