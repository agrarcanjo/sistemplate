package pt.ama.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import pt.ama.dto.DocumentRequest;

import java.io.ByteArrayOutputStream;

@ApplicationScoped
public class PdfGenerator {

    private static final Logger LOG = Logger.getLogger(PdfGenerator.class);


    public byte[] generatePdf(String processedHtmlContent, JsonNode data, DocumentRequest.PdfOptions options) {
        LOG.infof("PdfGenerator: Gerando PDF (JsonNode) - tamanho do HTML: %d caracteres", processedHtmlContent.length());
        
        try {
            LOG.infof("PdfGenerator: Usando HTML já processado - tamanho: %d caracteres", processedHtmlContent.length());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            ConverterProperties properties = new ConverterProperties();

            if (options != null) {
                LOG.infof("PdfGenerator: Aplicando opções: filename=%s, orientation=%s, pageSize=%s", 
                         options.getFilename(), options.getOrientation(), options.getPageSize());
            }

            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);

            if (options != null) {
                if (options.getAuthor() != null) {
                    pdfDocument.getDocumentInfo().setAuthor(options.getAuthor());
                }
                if (options.getSubject() != null) {
                    pdfDocument.getDocumentInfo().setSubject(options.getSubject());
                }
                if (options.getKeywords() != null) {
                    pdfDocument.getDocumentInfo().setKeywords(options.getKeywords());
                }
                LOG.infof("PdfGenerator: Metadados aplicados ao PDF");
            }

            HtmlConverter.convertToPdf(processedHtmlContent, pdfDocument, properties);
            
            byte[] result = outputStream.toByteArray();
            LOG.infof("PdfGenerator: PDF gerado com sucesso - tamanho: %d bytes", result.length);
            
            return result;
            
        } catch (Exception e) {
            LOG.errorf("PdfGenerator: Erro ao gerar PDF: %s", e.getMessage());
            throw new RuntimeException("Erro ao gerar PDF: " + e.getMessage(), e);
        }
    }
}