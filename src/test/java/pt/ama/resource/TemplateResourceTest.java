package pt.ama.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ama.dto.TemplateRequest;
import pt.ama.dto.TemplateResponse;
import pt.ama.mapper.TemplateMapper;
import pt.ama.model.DocumentType;
import pt.ama.model.Template;
import pt.ama.service.TemplateService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static pt.ama.util.TemplateTestUtils.*;

@QuarkusTest
class TemplateResourceTest {

    @InjectMock
    TemplateService templateService;

    @InjectMock
    TemplateMapper templateMapper;

    private TemplateResource templateResource;

    @BeforeEach
    void setUp() {
        templateResource = new TemplateResource();
        templateResource.templateService = templateService;
        templateResource.templateMapper = templateMapper;
    }

    @Test
    @DisplayName("Should return all templates successfully")
    void shouldReturnAllTemplatesSuccessfully() {
        Template template1 = createTemplate("template1", DocumentType.PDF);
        Template template2 = createTemplate("template2", DocumentType.EMAIL);
        List<Template> templates = Arrays.asList(template1, template2);

        TemplateResponse response1 = createTemplateResponse("template1", DocumentType.PDF);
        TemplateResponse response2 = createTemplateResponse("template2", DocumentType.EMAIL);
        List<TemplateResponse> expectedResponses = Arrays.asList(response1, response2);

        when(templateService.findAll()).thenReturn(templates);
        when(templateMapper.toResponseList(templates)).thenReturn(expectedResponses);

        Response response = templateResource.getAllTemplates();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(expectedResponses, response.getEntity());
        verify(templateService).findAll();
        verify(templateMapper).toResponseList(templates);
    }

    @Test
    @DisplayName("Should return empty list when no templates exist")
    void shouldReturnEmptyListWhenNoTemplatesExist() {
        List<Template> emptyTemplates = List.of();
        List<TemplateResponse> emptyResponses = List.of();

        when(templateService.findAll()).thenReturn(emptyTemplates);
        when(templateMapper.toResponseList(emptyTemplates)).thenReturn(emptyResponses);

        Response response = templateResource.getAllTemplates();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(emptyResponses, response.getEntity());
        verify(templateService).findAll();
        verify(templateMapper).toResponseList(emptyTemplates);
    }

    @Test
    @DisplayName("Should create template successfully")
    void shouldCreateTemplateSuccessfully() {
        TemplateRequest templateRequest = createTemplateRequest("new-template", DocumentType.PDF);
        Template template = createTemplate("new-template", DocumentType.PDF);
        TemplateResponse expectedResponse = createTemplateResponse("new-template", DocumentType.PDF);

        when(templateService.exists(templateRequest.getName())).thenReturn(false);
        when(templateMapper.toEntity(templateRequest)).thenReturn(template);
        when(templateMapper.toResponse(template)).thenReturn(expectedResponse);

        Response response = templateResource.createTemplate(templateRequest);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(expectedResponse, response.getEntity());
        verify(templateService).exists(templateRequest.getName());
        verify(templateMapper).toEntity(templateRequest);
        //verify(templateService).save(template);
        verify(templateMapper).toResponse(template);
    }

    @Test
    @DisplayName("Should return conflict when template with same name already exists")
    void shouldReturnConflictWhenTemplateAlreadyExists() {
        TemplateRequest templateRequest = createTemplateRequest("existing-template", DocumentType.PDF);

        when(templateService.exists(templateRequest.getName())).thenReturn(true);

        Response response = templateResource.createTemplate(templateRequest);

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("Template com este nome já existe", response.getEntity());
        verify(templateService).exists(templateRequest.getName());
        verify(templateMapper, never()).toEntity(any());
        verify(templateService, never()).createTemplate(any());
    }

    @Test
    @DisplayName("Should return bad request when exception occurs during template creation")
    void shouldReturnBadRequestWhenExceptionOccurs() {
        TemplateRequest templateRequest = createTemplateRequest("error-template", DocumentType.PDF);
        Template template = createTemplate("error-template", DocumentType.PDF);

        when(templateService.exists(templateRequest.getName())).thenReturn(false);
        when(templateMapper.toEntity(templateRequest)).thenReturn(template);
        // doThrow(new RuntimeException("Database error")).when(templateService).createTemplate(template);

        Response response = templateResource.createTemplate(templateRequest);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Erro ao criar template: Database error"));
        verify(templateService).exists(templateRequest.getName());
        verify(templateMapper).toEntity(templateRequest);
        // verify(templateService).createTemplate(template);
    }

    @Test
    @DisplayName("Should handle mapper exception during template creation")
    void shouldHandleMapperExceptionDuringTemplateCreation() {
        TemplateRequest templateRequest = createTemplateRequest("mapper-error-template", DocumentType.PDF);

        when(templateService.exists(templateRequest.getName())).thenReturn(false);
        when(templateMapper.toEntity(templateRequest)).thenThrow(new RuntimeException("Mapping error"));

        Response response = templateResource.createTemplate(templateRequest);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Erro ao criar template: Mapping error"));
        verify(templateService).exists(templateRequest.getName());
        verify(templateMapper).toEntity(templateRequest);
        verify(templateService, never()).createTemplate(any());
    }

    @Test
    @DisplayName("Should create EMAIL template successfully")
    void shouldCreateEmailTemplateSuccessfully() {
        TemplateRequest templateRequest = createTemplateRequest("email-template", DocumentType.EMAIL);
        Template template = createTemplate("email-template", DocumentType.EMAIL);
        TemplateResponse expectedResponse = createTemplateResponse("email-template", DocumentType.EMAIL);

        when(templateService.exists(templateRequest.getName())).thenReturn(false);
        when(templateMapper.toEntity(templateRequest)).thenReturn(template);
        when(templateMapper.toResponse(template)).thenReturn(expectedResponse);

        Response response = templateResource.createTemplate(templateRequest);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(expectedResponse, response.getEntity());
        verify(templateService).exists(templateRequest.getName());
        verify(templateMapper).toEntity(templateRequest);
        // verify(templateService).createTemplate(template);
        verify(templateMapper).toResponse(template);
    }

    @Test
    @DisplayName("Should create SMS template successfully")
    void shouldCreateSmsTemplateSuccessfully() {
        TemplateRequest templateRequest = createTemplateRequest("sms-template", DocumentType.SMS);
        Template template = createTemplate("sms-template", DocumentType.SMS);
        TemplateResponse expectedResponse = createTemplateResponse("sms-template", DocumentType.SMS);

        when(templateService.exists(templateRequest.getName())).thenReturn(false);
        when(templateMapper.toEntity(templateRequest)).thenReturn(template);
        when(templateMapper.toResponse(template)).thenReturn(expectedResponse);

        Response response = templateResource.createTemplate(templateRequest);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(expectedResponse, response.getEntity());
        verify(templateService).exists(templateRequest.getName());
        verify(templateMapper).toEntity(templateRequest);
        // verify(templateService).createTemplate(template);
        verify(templateMapper).toResponse(template);
    }

}