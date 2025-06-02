package pt.ama.service;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import io.quarkus.qute.Engine;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

@ApplicationScoped
public class PdfGenerator {

    @Inject
    Engine quteEngine;

    public byte[] generatePdf(String templateContent, Map<String, Object> data) {
        try {
            Template template = quteEngine.parse(templateContent);
            TemplateInstance instance = template.data(data);
            String renderedHtml = instance.render();

            ConverterProperties properties = new ConverterProperties();

            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            PdfWriter pdfWriter = new PdfWriter(pdfOutputStream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            
            HtmlConverter.convertToPdf(
                new ByteArrayInputStream(renderedHtml.getBytes()),
                pdfDocument,
                properties
            );

            pdfDocument.close();
            return pdfOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF: " + e.getMessage(), e);
        }
    }
}