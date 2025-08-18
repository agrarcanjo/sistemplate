package pt.ama.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.qute.Engine;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.jboss.logging.Logger;
import pt.ama.dto.AsyncDocumentResponse;
import pt.ama.dto.DocumentRequest;
import pt.ama.dto.DocumentResponse;
import pt.ama.exception.*;
import pt.ama.mapper.DocumentRequestMapper;
import pt.ama.model.DocumentType;
import pt.ama.model.Template;
import pt.ama.service.generator.DocumentGenerator;
import pt.ama.service.generator.DocumentGeneratorFactory;
import pt.ama.service.kafka.DocumentKafkaProducer;
import pt.ama.service.validation.DocumentValidator;
import pt.ama.service.validation.RequiredFieldsValidator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;

@ApplicationScoped
public class DocumentService {

    private static final Logger LOG = Logger.getLogger(DocumentService.class);
    private static final String DEFAULT_CONTENT_TYPE = "application/pdf";

    @Inject
    TemplateService templateService;

    @Inject
    DocumentGeneratorFactory generatorFactory;

    @Inject
    Engine quteEngine;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    DocumentRequestMapper documentRequestMapper;

    @Inject
    RequiredFieldsValidator requiredFieldsValidator;

    @Inject
    DocumentValidator documentValidator;

    @Inject
    DocumentKafkaProducer kafkaProducer;

    /**
     * Processa requisição de documento - síncrono ou assíncrono baseado no flag
     */
    public Object processDocumentRequest(@Valid DocumentRequest request) {
        if (request.isAsync()) {
            return generateDocumentAsync(request);
        } else {
            return generateDocument(request);
        }
    }

    /**
     * Gera documento de forma assíncrona via Kafka
     */
    public AsyncDocumentResponse generateDocumentAsync(@Valid DocumentRequest request) {
        LOG.infof("Iniciando geração assíncrona de documento para template: %s", request.getTemplateName());

        try {
            // Validação básica da requisição
            documentValidator.validateDocumentRequest(request);

            // Publicar no Kafka
            String eventId = kafkaProducer.publishDocumentGenerationRequest(documentRequestMapper.toDocumentGenerationMessage(request));

            LOG.infof("Documento enviado para processamento assíncrono. Event ID: %s", eventId);

            return new AsyncDocumentResponse(eventId, "ACCEPTED", "Document generation request accepted");

        } catch (Exception e) {
            LOG.errorf("Erro ao processar requisição assíncrona: %s", e.getMessage());
            throw new DocumentGenerationException(request.getTemplateName(), "Failed to process async document request", e);
        }
    }

    /**
     * Gera um documento baseado no template e dados fornecidos
     */
    @Transactional
    public byte[] generateDocument(@Valid DocumentRequest request) {
        LOG.infof("Iniciando geração de documento para template: %s", request.getTemplateName());
        
        try {
            documentValidator.validateDocumentRequest(request);

            Template template = templateService.findByNameOrThrow(request.getTemplateName());

            validateTemplateIsActive(template);

            String processedContent = processTemplate(template, request);

            byte[] document = generateDocumentByType(template, processedContent, request);
            
            LOG.infof("Documento gerado com sucesso - template: %s, tamanho: %d bytes", 
                     template.getName(), document.length);
            
            return document;
            
        } catch (BusinessException e) {
            LOG.warnf("Erro de negócio ao gerar documento: %s", e.getMessage());
            throw e;
        } catch (Exception e) {
            LOG.errorf(e, "Erro inesperado ao gerar documento para template: %s", request.getTemplateName());
            throw new DocumentGenerationException(request.getTemplateName(), e.getMessage(), e);
        }
    }

    /**
     * Gera um documento em formato Base64
     */
    public DocumentResponse generateBase64Document(@Valid DocumentRequest request) {
        LOG.infof("Gerando documento Base64 para template: %s", request.getTemplateName());
        
        byte[] document = generateDocument(request);
        String filename = buildFilename(request);
        String base64Content = Base64.getEncoder().encodeToString(document);
        
        DocumentResponse response = DocumentResponse.builder()
                .filename(filename)
                .contentType(getContentTypeByTemplate(request.getTemplateName()))
                .content(base64Content)
                .size((long) document.length)
                .templateName(request.getTemplateName())
                .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
        
        LOG.infof("Documento Base64 gerado - filename: %s, tamanho: %d bytes", filename, document.length);
        
        return response;
    }

    /**
     * Constrói o nome do arquivo baseado no request
     */
    public String buildFilename(DocumentRequest request) {
        String filename = request.getTemplateName();
        
        if (request.getOptions() != null && request.getOptions().getFilename() != null) {
            filename = request.getOptions().getFilename();
        }

        String extension = getFileExtensionByTemplate(request.getTemplateName());
        if (!filename.toLowerCase().endsWith(extension)) {
            filename += extension;
        }
        
        return filename;
    }

    /**
     * Processa o template com os dados fornecidos usando Qute
     */
    private String processTemplate(Template template, DocumentRequest request) {
        try {
            LOG.debugf("Processando template %s com Qute engine", template.getName());

            Map<String, Object> dataMap = convertJsonNodeToMap(request.getData());
            LOG.debugf("Dados convertidos para Map com %d chaves", dataMap.size());

            // Validar campos obrigatórios
            requiredFieldsValidator.validateRequiredFields(template, dataMap);
            
            // Processar template
            String processedContent = quteEngine.parse(template.getContent())
                    .data(dataMap)
                    .render();
                    
            LOG.debugf("Template processado com sucesso - tamanho: %d caracteres", processedContent.length());
            
            return processedContent;
            
        } catch (RequiredFieldsValidationException e) {
            LOG.warnf("Campos obrigatórios não fornecidos para template %s: %s", template.getName(), e.getMessage());
            throw e;
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao processar template %s", template.getName());
            throw new TemplateProcessingException(template.getName(), e.getMessage(), e);
        }
    }

    /**
     * Gera documento baseado no tipo do template
     */
    private byte[] generateDocumentByType(Template template, String processedContent, DocumentRequest request) {
        try {
            DocumentGenerator generator = generatorFactory.getGenerator(template.getType());
            
            LOG.debugf("Gerando documento do tipo %s usando %s", 
                      template.getType(), generator.getClass().getSimpleName());
            
            byte[] result = generator.generate(processedContent, request);
            
            LOG.debugf("Documento %s gerado com sucesso - tamanho: %d bytes", 
                      template.getType(), result.length);
            
            return result;
            
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao gerar documento do tipo %s", template.getType());
            throw new DocumentGenerationException(template.getName(), e.getMessage(), e);
        }
    }

    /**
     * Converte JsonNode para Map
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertJsonNodeToMap(JsonNode jsonNode) {
        try {
            if (jsonNode == null) {
                throw new DataConversionException("JsonNode não pode ser nulo");
            }
            
            Map<String, Object> result = objectMapper.convertValue(jsonNode, Map.class);
            LOG.debugf("JsonNode convertido para Map com %d entradas", result.size());
            
            return result;
            
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao converter JsonNode para Map");
            throw new DataConversionException("Erro na conversão de dados: " + e.getMessage(), e);
        }
    }

    /**
     * Valida se o template está ativo
     */
    private void validateTemplateIsActive(Template template) {
        if (!template.isActive()) {
            LOG.warnf("Tentativa de usar template inativo: %s", template.getName());
            throw new InactiveTemplateException(template.getName());
        }
    }

    /**
     * Obtém o content type baseado no template
     */
    public String getContentTypeByTemplate(String templateName) {
        try {
            Template template = templateService.findByNameOrThrow(templateName);
            return getContentTypeByDocumentType(template.getType());
        } catch (Exception e) {
            LOG.warnf("Erro ao obter content type para template %s, usando padrão", templateName);
            return DEFAULT_CONTENT_TYPE;
        }
    }

    /**
     * Obtém content type baseado no tipo de documento
     */
    private String getContentTypeByDocumentType(DocumentType type) {
        return switch (type) {
            case PDF -> "application/pdf";
            case EMAIL -> "text/html";
            case SMS -> "text/plain";
            default -> DEFAULT_CONTENT_TYPE;
        };
    }

    /**
     * Obtém extensão do arquivo baseado no template
     */
    private String getFileExtensionByTemplate(String templateName) {
        try {
            Template template = templateService.findByNameOrThrow(templateName);
            return getFileExtensionByDocumentType(template.getType());
        } catch (Exception e) {
            LOG.warnf("Erro ao obter extensão para template %s, usando .pdf", templateName);
            return ".pdf";
        }
    }

    /**
     * Obtém extensão baseada no tipo de documento
     */
    private String getFileExtensionByDocumentType(DocumentType type) {
        return switch (type) {
            case PDF -> ".pdf";
            case EMAIL -> ".html";
            case SMS -> ".txt";
        };
    }
}