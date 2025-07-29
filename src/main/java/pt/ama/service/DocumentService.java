package pt.ama.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pt.ama.dto.DocumentRequest;
import pt.ama.exception.*;
import pt.ama.model.Template;
import pt.ama.service.validation.RequiredFieldsValidator;
import io.quarkus.qute.Engine;
import org.jboss.logging.Logger;

import java.util.Map;

@ApplicationScoped
public class DocumentService {

    private static final Logger LOG = Logger.getLogger(DocumentService.class);

    @Inject
    TemplateService templateService;

    @Inject
    PdfGenerator pdfGenerator;

    @Inject
    EmailGenerator emailGenerator;

    @Inject
    SmsGenerator smsGenerator;

    @Inject
    Engine engine;

    @Inject
    ObjectMapper objectMapper;
    
    @Inject
    RequiredFieldsValidator requiredFieldsValidator;

    public byte[] generateDocument(DocumentRequest request) {
        LOG.infof("DocumentService: Iniciando geração de documento para template: '%s'", request.getTemplateName());

        Template template = templateService.findByName(request.getTemplateName());
        if (template == null) {
            LOG.errorf("DocumentService: Template não encontrado: '%s'", request.getTemplateName());
            throw new TemplateNotFoundException(request.getTemplateName());
        }
        
        LOG.infof("DocumentService: Template encontrado: '%s', tipo: %s, ativo: %s", 
                 template.getName(), template.getType(), template.isActive());

        String processedContent = processTemplate(template, request);
        return generateDocumentByType(template, processedContent, request);
    }

    private String processTemplate(Template template, DocumentRequest request) {
        try {
            LOG.infof("DocumentService: Processando template com Qute engine");

            Map<String, Object> dataMap = convertJsonNodeToMap(request.getData());
            LOG.infof("DocumentService: Dados convertidos para Map com %d chaves", dataMap.size());

            requiredFieldsValidator.validateRequiredFields(template, dataMap);
            
            String processedContent = engine.parse(template.getContent())
                    .data(dataMap)
                    .render();
            LOG.infof("DocumentService: Template processado com sucesso, tamanho do conteúdo: %d caracteres", 
                     processedContent.length());
            return processedContent;
        } catch (RequiredFieldsValidationException e) {
            throw e;
        } catch (Exception e) {
            LOG.errorf("DocumentService: Erro ao processar template: %s", e.getMessage(), e);
            throw new TemplateProcessingException(template.getName(), e.getMessage(), e);
        }
    }

    private byte[] generateDocumentByType(Template template, String processedContent, DocumentRequest request) {
        try {
            byte[] result;
            switch (template.getType()) {
                case PDF:
                    LOG.infof("DocumentService: Gerando PDF");
                    result = pdfGenerator.generatePdf(processedContent, request.getData(), request.getOptions());
                    LOG.infof("DocumentService: PDF gerado com sucesso, tamanho: %d bytes", result.length);
                    return result;
                    
                case EMAIL:
                    LOG.infof("DocumentService: Gerando EMAIL");
                    result = emailGenerator.generateEmail(processedContent, request.getData());
                    if (result == null) {
                        Map<String, Object> dataMap = convertJsonNodeToMap(request.getData());
                        result = emailGenerator.generate(processedContent, dataMap);
                    }
                    LOG.infof("DocumentService: Email gerado com sucesso, tamanho: %d bytes", result.length);
                    return result;
                    
                case SMS:
                    LOG.infof("DocumentService: Gerando SMS");
                    result = smsGenerator.generateSms(processedContent, request.getData());
                    if (result == null) {
                        Map<String, Object> dataMap = convertJsonNodeToMap(request.getData());
                        result = smsGenerator.generate(processedContent, dataMap);
                    }
                    LOG.infof("DocumentService: SMS gerado com sucesso, tamanho: %d bytes", result.length);
                    return result;
                    
                default:
                    LOG.errorf("DocumentService: Tipo de documento não suportado: %s", template.getType());
                    throw new UnsupportedDocumentTypeException(template.getType());
            }
        } catch (Exception e) {
            LOG.errorf("DocumentService: Erro ao gerar documento: %s", e.getMessage());
            throw new DocumentGenerationException(template.getName(), template.getType().toString(), e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> convertJsonNodeToMap(JsonNode jsonNode) {
        try {
            LOG.debugf("DocumentService: Convertendo JsonNode para Map");
            Map<String, Object> result = objectMapper.convertValue(jsonNode, Map.class);
            LOG.debugf("DocumentService: Conversão concluída com sucesso");
            return result;
        } catch (Exception e) {
            LOG.errorf("DocumentService: Erro ao converter JsonNode para Map: %s", e.getMessage());
            throw new DataConversionException(e.getMessage(), e);
        }
    }
}