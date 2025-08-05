package pt.ama.service.generator;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import lombok.Setter;
import org.jboss.logging.Logger;
import pt.ama.dto.DocumentRequest;
import pt.ama.exception.SmsGenerationException;
import pt.ama.model.DocumentType;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Pattern;

import static pt.ama.util.Util.extractStringField;

/**
 * Gerador de SMS melhorado com validações específicas para mensagens de texto
 */
@ApplicationScoped
public class SmsGenerator implements DocumentGenerator {

    private static final Logger LOG = Logger.getLogger(SmsGenerator.class);
    private static final String SUPPORTED_TYPE = DocumentType.SMS.toString();

    private static final int SMS_SINGLE_LIMIT = 160;
    private static final int SMS_MULTIPART_LIMIT = 1600; // 10 partes
    private static final int SMS_UNICODE_SINGLE_LIMIT = 70;
    private static final int SMS_UNICODE_MULTIPART_LIMIT = 700; // 10 partes
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^(\\+?[1-9]\\d{1,14}|\\d{9,15})$"
    );


    private static final Pattern UNICODE_CHARS = Pattern.compile(
        "[^\\x00-\\x7F]|[€£¥§¿¡]"
    );

    @Override
    public byte[] generate(String processedContent, DocumentRequest request) {
        LOG.infof("Iniciando geração de SMS - tamanho do conteúdo: %d caracteres", processedContent.length());
        
        try {
            validateContent(processedContent);
            
            SmsData smsData = extractSmsData(request);
            validateSmsData(smsData);
            
            String smsContent = buildSmsContent(processedContent, smsData);
            
            LOG.infof("SMS gerado com sucesso - destinatário: %s, tamanho: %d caracteres", 
                     smsData.getTo(), smsContent.length());
            
            return smsContent.getBytes(StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao gerar SMS");
            throw new SmsGenerationException("Erro na geração do SMS: " + e.getMessage(), e);
        }
    }

    @Override
    public String getSupportedType() {
        return SUPPORTED_TYPE;
    }

    @Override
    public void validateContent(String content) {
        DocumentGenerator.super.validateContent(content);

        String cleanContent = removeHtmlTags(content);
        
        boolean isUnicode = UNICODE_CHARS.matcher(cleanContent).find();
        int limit = isUnicode ? SMS_UNICODE_MULTIPART_LIMIT : SMS_MULTIPART_LIMIT;
        
        if (cleanContent.length() > limit) {
            throw new IllegalArgumentException(
                String.format("Conteúdo muito longo para SMS (limite: %d caracteres, atual: %d)", 
                             limit, cleanContent.length())
            );
        }
        
        LOG.debugf("Conteúdo validado - tamanho: %d, unicode: %s", Optional.of(cleanContent.length()), isUnicode);
    }

    /**
     * Extrai dados específicos do SMS do request
     */
    private SmsData extractSmsData(DocumentRequest request) {
        SmsData smsData = new SmsData();
        
        try {
            JsonNode data = request.getData();

            smsData.setTo(extractStringField(data, "to", "phone", "telefone", "numero"));
            smsData.setFrom(extractStringField(data, "from", "sender", "remetente"));
            smsData.setReference(extractStringField(data, "reference", "ref", "referencia"));

            if (smsData.getFrom() == null) {
                smsData.setFrom("AMA");
            }
            
            LOG.debugf("Dados do SMS extraídos - para: %s, de: %s", smsData.getTo(), smsData.getFrom());
            
            return smsData;
            
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao extrair dados do SMS");
            throw new SmsGenerationException("Erro ao processar dados do SMS: " + e.getMessage(), e);
        }
    }

    /**
     * Valida os dados do SMS
     */
    private void validateSmsData(SmsData smsData) {
        if (smsData.getTo() == null || smsData.getTo().trim().isEmpty()) {
            throw new SmsGenerationException("Campo 'to' (número de telefone) é obrigatório para geração de SMS");
        }

        String cleanPhone = cleanPhoneNumber(smsData.getTo());
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            throw new SmsGenerationException("Formato de telefone inválido: " + smsData.getTo());
        }

        smsData.setTo(cleanPhone);

        if (smsData.getFrom() != null) {
            validateSender(smsData.getFrom());
        }
        
        LOG.debugf("Dados do SMS validados com sucesso - telefone: %s", cleanPhone);
    }

    /**
     * Limpa número de telefone removendo caracteres não numéricos (exceto +)
     */
    private String cleanPhoneNumber(String phone) {
        if (phone == null) return null;

        String cleaned = phone.replaceAll("[^\\d+]", "");

        if (cleaned.startsWith("+")) {
            cleaned = "+" + cleaned.substring(1).replaceAll("\\+", "");
        }
        
        return cleaned;
    }

    /**
     * Valida o remetente do SMS
     */
    private void validateSender(String sender) {
        if (sender.length() > 11) {
            throw new SmsGenerationException("Remetente muito longo (máximo 11 caracteres): " + sender);
        }

        if (sender.matches("\\d+")) {
            if (!PHONE_PATTERN.matcher(sender).matches()) {
                throw new SmsGenerationException("Formato de telefone remetente inválido: " + sender);
            }
        } else {
            if (!sender.matches("[a-zA-Z0-9\\s]+")) {
                throw new SmsGenerationException("Remetente contém caracteres inválidos: " + sender);
            }
        }
    }

    /**
     * Constrói o conteúdo final do SMS
     */
    private String buildSmsContent(String processedContent, SmsData smsData) {
        StringBuilder smsContent = new StringBuilder();

        smsContent.append("# SMS METADATA\n");
        smsContent.append("# TO: ").append(smsData.getTo()).append("\n");
        smsContent.append("# FROM: ").append(smsData.getFrom()).append("\n");
        
        if (smsData.getReference() != null) {
            smsContent.append("# REF: ").append(smsData.getReference()).append("\n");
        }
        
        smsContent.append("# GENERATED: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n");
        smsContent.append("# END METADATA\n\n");

        String cleanContent = removeHtmlTags(processedContent);

        cleanContent = normalizeWhitespace(cleanContent);

        smsContent.append(cleanContent);

        checkSmsLimits(cleanContent);
        
        LOG.debugf("Conteúdo do SMS construído - tamanho final: %d caracteres", cleanContent.length());
        
        return smsContent.toString();
    }

    /**
     * Remove tags HTML do conteúdo
     */
    private String removeHtmlTags(String content) {
        if (content == null) return "";

        String cleaned = content.replaceAll("<[^>]+>", "");

        cleaned = cleaned.replace("&amp;", "&")
                        .replace("&lt;", "<")
                        .replace("&gt;", ">")
                        .replace("&quot;", "\"")
                        .replace("&#39;", "'")
                        .replace("&nbsp;", " ");

        return cleaned;
    }

    /**
     * Normaliza espaços em branco e quebras de linha
     */
    private String normalizeWhitespace(String content) {
        if (content == null) return "";

        content = content.replaceAll("\\s+", " ");

        content = content.trim();
        
        return content;
    }

    /**
     * Verifica limites do SMS e registra avisos
     */
    private void checkSmsLimits(String content) {
        boolean isUnicode = UNICODE_CHARS.matcher(content).find();
        int singleLimit = isUnicode ? SMS_UNICODE_SINGLE_LIMIT : SMS_SINGLE_LIMIT;
        
        if (content.length() > singleLimit) {
            int parts = (int) Math.ceil((double) content.length() / singleLimit);
            LOG.warnf("SMS será enviado em %d partes (tamanho: %d, limite por parte: %d)", 
                     parts, content.length(), singleLimit);
        }
        
        if (isUnicode) {
            LOG.infof("SMS contém caracteres Unicode - limite reduzido aplicado");
        }
    }

    /**
     * Classe interna para dados do SMS
     */
    @Setter
    @Getter
    private static class SmsData {
        private String to;
        private String from;
        private String reference;

    }
}