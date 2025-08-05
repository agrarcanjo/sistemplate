package pt.ama.service.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.jboss.logging.Logger;
import pt.ama.dto.DocumentRequest;
import pt.ama.exception.EmailGenerationException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import static pt.ama.util.Util.extractStringField;

/**
 * Gerador de emails melhorado com validações e formatação adequada
 */
@ApplicationScoped
public class EmailGenerator implements DocumentGenerator {

    private static final Logger LOG = Logger.getLogger(EmailGenerator.class);
    private static final String SUPPORTED_TYPE = "EMAIL";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final int MAX_SUBJECT_LENGTH = 200;
    private static final int MAX_CONTENT_LENGTH = 1_000_000;

    @Override
    public byte[] generate(String processedContent, DocumentRequest request) {
        LOG.infof("Iniciando geração de email - tamanho do conteúdo: %d caracteres", processedContent.length());
        
        try {
            validateContent(processedContent);
            
            EmailData emailData = extractEmailData(request);
            validateEmailData(emailData);
            
            String emailHtml = buildEmailHtml(processedContent, emailData);
            
            LOG.infof("Email gerado com sucesso - destinatário: %s", emailData.getTo());
            
            return emailHtml.getBytes(StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao gerar email");
            throw new EmailGenerationException("Erro na geração do email: " + e.getMessage(), e);
        }
    }

    @Override
    public String getSupportedType() {
        return SUPPORTED_TYPE;
    }

    @Override
    public void validateContent(String content) {
        DocumentGenerator.super.validateContent(content);
        
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Conteúdo muito grande para email (limite: %d caracteres)", MAX_CONTENT_LENGTH)
            );
        }
    }

    /**
     * Extrai dados específicos do email do request
     */
    private EmailData extractEmailData(DocumentRequest request) {
        EmailData emailData = new EmailData();
        
        try {
            JsonNode data = request.getData();

            emailData.setTo(extractStringField(data, "to", "email_to", "recipient"));
            emailData.setSubject(extractStringField(data, "subject", "email_subject", "titulo"));

            emailData.setFrom(extractStringField(data, "from", "email_from", "sender"));
            emailData.setCc(extractStringField(data, "cc", "email_cc"));
            emailData.setBcc(extractStringField(data, "bcc", "email_bcc"));
            emailData.setReplyTo(extractStringField(data, "reply_to", "email_reply_to"));

            if (emailData.getFrom() == null) {
                emailData.setFrom("noreply@ama.pt");
            }
            
            if (emailData.getSubject() == null) {
                emailData.setSubject("Documento gerado automaticamente");
            }
            
            LOG.debugf("Dados do email extraídos - para: %s, assunto: %s", 
                      emailData.getTo(), emailData.getSubject());
            
            return emailData;
            
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao extrair dados do email");
            throw new EmailGenerationException("Erro ao processar dados do email: " + e.getMessage(), e);
        }
    }


    /**
     * Valida os dados do email
     */
    private void validateEmailData(EmailData emailData) {
        if (emailData.getTo() == null || emailData.getTo().trim().isEmpty()) {
            throw new EmailGenerationException("Campo 'to' (destinatário) é obrigatório para geração de email");
        }

        if (!EMAIL_PATTERN.matcher(emailData.getTo()).matches()) {
            throw new EmailGenerationException("Formato de email inválido: " + emailData.getTo());
        }

        validateOptionalEmail(emailData.getFrom(), "from");
        validateOptionalEmail(emailData.getCc(), "cc");
        validateOptionalEmail(emailData.getBcc(), "bcc");
        validateOptionalEmail(emailData.getReplyTo(), "reply_to");

        if (emailData.getSubject() != null && emailData.getSubject().length() > MAX_SUBJECT_LENGTH) {
            throw new EmailGenerationException(
                String.format("Assunto muito longo (limite: %d caracteres)", MAX_SUBJECT_LENGTH)
            );
        }
        
        LOG.debugf("Dados do email validados com sucesso");
    }

    /**
     * Valida email opcional se fornecido
     */
    private void validateOptionalEmail(String email, String fieldName) {
        if (email != null && !email.trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                throw new EmailGenerationException(
                    String.format("Formato de email inválido no campo '%s': %s", fieldName, email)
                );
            }
        }
    }

    /**
     * Constrói o HTML completo do email
     */
    private String buildEmailHtml(String processedContent, EmailData emailData) {
        StringBuilder emailHtml = new StringBuilder();

        emailHtml.append("<!-- EMAIL METADATA -->\n");
        emailHtml.append("<!-- TO: ").append(emailData.getTo()).append(" -->\n");
        emailHtml.append("<!-- FROM: ").append(emailData.getFrom()).append(" -->\n");
        emailHtml.append("<!-- SUBJECT: ").append(emailData.getSubject()).append(" -->\n");
        
        if (emailData.getCc() != null) {
            emailHtml.append("<!-- CC: ").append(emailData.getCc()).append(" -->\n");
        }
        
        if (emailData.getBcc() != null) {
            emailHtml.append("<!-- BCC: ").append(emailData.getBcc()).append(" -->\n");
        }
        
        if (emailData.getReplyTo() != null) {
            emailHtml.append("<!-- REPLY-TO: ").append(emailData.getReplyTo()).append(" -->\n");
        }
        
        emailHtml.append("<!-- GENERATED: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append(" -->\n");
        emailHtml.append("<!-- END EMAIL METADATA -->\n\n");

        if (!processedContent.trim().toLowerCase().startsWith("<!doctype") && 
            !processedContent.trim().toLowerCase().startsWith("<html")) {
            
            emailHtml.append("<!DOCTYPE html>\n");
            emailHtml.append("<html lang=\"pt\">\n");
            emailHtml.append("<head>\n");
            emailHtml.append("    <meta charset=\"UTF-8\">\n");
            emailHtml.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
            emailHtml.append("    <title>").append(escapeHtml(emailData.getSubject())).append("</title>\n");
            emailHtml.append("    <style>\n");
            emailHtml.append("        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }\n");
            emailHtml.append("        .email-container { max-width: 600px; margin: 0 auto; padding: 20px; }\n");
            emailHtml.append("    </style>\n");
            emailHtml.append("</head>\n");
            emailHtml.append("<body>\n");
            emailHtml.append("    <div class=\"email-container\">\n");
            emailHtml.append(processedContent);
            emailHtml.append("    </div>\n");
            emailHtml.append("</body>\n");
            emailHtml.append("</html>");
        } else {
            emailHtml.append(processedContent);
        }
        
        LOG.debugf("HTML do email construído - tamanho final: %d caracteres", emailHtml.length());
        
        return emailHtml.toString();
    }

    /**
     * Escapa caracteres HTML básicos
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }

    /**
     * Classe interna para dados do email
     */
    @Setter
    @Getter
    private static class EmailData {
        private String to;
        private String from;
        private String subject;
        private String cc;
        private String bcc;
        private String replyTo;

    }
}