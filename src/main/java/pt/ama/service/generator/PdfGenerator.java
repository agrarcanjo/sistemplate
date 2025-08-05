package pt.ama.service.generator;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import pt.ama.dto.DocumentRequest;
import pt.ama.exception.PdfGenerationException;

import java.io.ByteArrayOutputStream;

/**
 * Gerador de documentos PDF melhorado com validações e configurações avançadas
 */
@ApplicationScoped
public class PdfGenerator implements DocumentGenerator {

    private static final Logger LOG = Logger.getLogger(PdfGenerator.class);
    private static final String SUPPORTED_TYPE = "PDF";
    private static final PageSize DEFAULT_PAGE_SIZE = PageSize.A4;
    private static final String DEFAULT_AUTHOR = "Sistema de Templates";

    @Override
    public byte[] generate(String processedContent, DocumentRequest request) {
        LOG.infof("Iniciando geração de PDF - tamanho do conteúdo: %d caracteres", processedContent.length());
        
        try {
            validateContent(processedContent);
            validateHtmlContent(processedContent);
            
            DocumentRequest.PdfOptions options = extractPdfOptions(request);
            
            return generatePdfDocument(processedContent, options);
            
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao gerar PDF");
            throw new PdfGenerationException("Erro na geração do PDF: " + e.getMessage(), e);
        }
    }

    @Override
    public String getSupportedType() {
        return SUPPORTED_TYPE;
    }

    @Override
    public void validateContent(String content) {
        DocumentGenerator.super.validateContent(content);
        
        if (content.length() > 10_000_000) {
            throw new IllegalArgumentException("Conteúdo muito grande para geração de PDF (limite: 10MB)");
        }
    }

    /**
     * Valida se o conteúdo é HTML válido básico
     */
    private void validateHtmlContent(String content) {

        if (!content.trim().toLowerCase().contains("<html") && 
            !content.trim().toLowerCase().contains("<body") &&
            !content.trim().toLowerCase().contains("<div")) {
            
            LOG.warnf("Conteúdo pode não ser HTML válido, envolvendo em estrutura básica");

        }
    }

    /**
     * Extrai opções de PDF do request
     */
    private DocumentRequest.PdfOptions extractPdfOptions(DocumentRequest request) {
        if (request.getOptions() != null) {
            return request.getOptions();
        }

        return createDefaultPdfOptions();
    }

    /**
     * Cria opções padrão para PDF
     */
    private DocumentRequest.PdfOptions createDefaultPdfOptions() {
        DocumentRequest.PdfOptions options = new DocumentRequest.PdfOptions();
        options.setAuthor(DEFAULT_AUTHOR);
        options.setSubject("Documento gerado automaticamente");
        options.setKeywords("template,pdf,automatico");
        options.setPageSize("A4");
        options.setOrientation("portrait");
        return options;
    }

    /**
     * Gera o documento PDF propriamente dito
     */
    private byte[] generatePdfDocument(String htmlContent, DocumentRequest.PdfOptions options) {
        LOG.debugf("Gerando PDF com opções: %s", options);
        
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            ConverterProperties converterProperties = createConverterProperties(options);

            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);

            configureDocumentMetadata(pdfDocument, options);

            configurePageSettings(pdfDocument, options);

            HtmlConverter.convertToPdf(htmlContent, pdfDocument, converterProperties);
            
            byte[] result = outputStream.toByteArray();
            
            LOG.infof("PDF gerado com sucesso - tamanho: %d bytes", result.length);
            
            return result;
            
        } catch (Exception e) {
            LOG.errorf(e, "Erro durante a geração do PDF");
            throw new PdfGenerationException("Falha na conversão HTML para PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Cria propriedades do conversor HTML para PDF
     */
    private ConverterProperties createConverterProperties(DocumentRequest.PdfOptions options) {
        ConverterProperties properties = new ConverterProperties();
        
        // Configurações adicionais podem ser adicionadas aqui
        // Por exemplo: fontes customizadas, CSS externo, etc.
        
        LOG.debugf("Propriedades do conversor configuradas");
        return properties;
    }

    /**
     * Configura metadados do documento PDF
     */
    private void configureDocumentMetadata(PdfDocument pdfDocument, DocumentRequest.PdfOptions options) {
        var documentInfo = pdfDocument.getDocumentInfo();
        
        if (options.getAuthor() != null) {
            documentInfo.setAuthor(options.getAuthor());
        } else {
            documentInfo.setAuthor(DEFAULT_AUTHOR);
        }
        
        if (options.getSubject() != null) {
            documentInfo.setSubject(options.getSubject());
        }
        
        if (options.getKeywords() != null) {
            documentInfo.setKeywords(options.getKeywords());
        }

        documentInfo.setCreator("Sistema de Templates v1.0");
        documentInfo.setTitle(options.getFilename() != null ? options.getFilename() : "Documento");
        
        LOG.debugf("Metadados do PDF configurados - autor: %s", documentInfo.getAuthor());
    }

    /**
     * Configura configurações de página (tamanho, orientação)
     */
    private void configurePageSettings(PdfDocument pdfDocument, DocumentRequest.PdfOptions options) {
        PageSize pageSize = determinePageSize(options.getPageSize());
        
        if ("landscape".equalsIgnoreCase(options.getOrientation())) {
            pageSize = pageSize.rotate();
            LOG.debugf("Orientação configurada para paisagem");
        }

        pdfDocument.setDefaultPageSize(pageSize);
        
        LOG.debugf("Configurações de página aplicadas - tamanho: %s, orientação: %s", 
                  options.getPageSize(), options.getOrientation());
    }

    /**
     * Determina o tamanho da página baseado na string fornecida
     */
    private PageSize determinePageSize(String pageSizeStr) {
        if (pageSizeStr == null) {
            return DEFAULT_PAGE_SIZE;
        }
        
        return switch (pageSizeStr.toUpperCase()) {
            case "A3" -> PageSize.A3;
            case "A4" -> PageSize.A4;
            case "A5" -> PageSize.A5;
            case "LETTER" -> PageSize.LETTER;
            case "LEGAL" -> PageSize.LEGAL;
            default -> {
                LOG.warnf("Tamanho de página desconhecido: %s, usando A4", pageSizeStr);
                yield DEFAULT_PAGE_SIZE;
            }
        };
    }
}