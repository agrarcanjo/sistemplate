package pt.ama.service;

import com.itextpdf.html2pdf.HtmlConverter;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@ApplicationScoped
public class PdfGenerator {

    public byte[] generate(String htmlContent) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(htmlContent, baos);
        return baos.toByteArray();
    }
}
