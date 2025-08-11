package pt.ama.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pt.ama.dto.TemplateRequest;
import pt.ama.exception.TemplateNotFoundException;
import pt.ama.mapper.TemplateMapper;
import pt.ama.model.DocumentStatus;
import pt.ama.model.Template;
import pt.ama.repository.TemplateRepository;
import pt.ama.service.validation.TemplateValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TemplateServiceTest {

    @Mock
    TemplateRepository templateRepository;

    @Mock
    TemplateMapper templateMapper;

    @Mock
    TemplateValidator templateValidator;

    @InjectMocks
    TemplateService templateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        when(templateRepository.listAll()).thenReturn(Collections.emptyList());
        List<Template> result = templateService.findAll();
        assertNotNull(result);
        verify(templateRepository).listAll();
    }

    @Test
    void testFindByName() {
        Template template = new Template();
        template.setName("test");
        when(templateRepository.findByName("test")).thenReturn(template);
        Template result = templateService.findByName("test");
        assertEquals("test", result.getName());
    }

    @Test
    void testFindByNameOrThrowFound() {
        Template template = new Template();
        template.setName("test");
        when(templateRepository.findByName("test")).thenReturn(template);
        Template result = templateService.findByNameOrThrow("test");
        assertEquals("test", result.getName());
    }

    @Test
    void testFindByNameOrThrowNotFound() {
        when(templateRepository.findByName("missing")).thenReturn(null);
        assertThrows(TemplateNotFoundException.class, () -> templateService.findByNameOrThrow("missing"));
    }

    @Test
    void testCreateTemplate() {
        TemplateRequest request = new TemplateRequest();
        request.setName("new-template");

        Template template = new Template();
        template.setName("new-template");

        when(templateMapper.toEntity(request)).thenReturn(template);

        Template created = templateService.createTemplate(request);

        assertEquals("new-template", created.getName());
        assertTrue(created.isActive());
        assertEquals(DocumentStatus.RASCUNHO, created.getStatus());
        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());

        verify(templateValidator).validateForCreation(request);
        verify(templateRepository).persist(template);
    }

    @Test
    void testUpdateTemplate() {
        String name = "existing";
        TemplateRequest request = new TemplateRequest();
        request.setName(name);

        Template existingTemplate = new Template();
        existingTemplate.setName(name);

        when(templateRepository.findByName(name)).thenReturn(existingTemplate);

        doAnswer(invocation -> {
            Template entity = invocation.getArgument(0);
            entity.setName(request.getName());
            return null;
        }).when(templateMapper).updateEntity(existingTemplate, request);

        Template result = templateService.updateTemplate(name, request);

        assertEquals(name, result.getName());
        assertNotNull(result.getUpdatedAt());

        verify(templateValidator).validateForUpdate(name, request);
        verify(templateRepository).persist(existingTemplate);
    }

    @Test
    void testDeleteTemplate() {
        String name = "to-delete";
        Template template = new Template();
        template.setName(name);
        template.setActive(true);

        when(templateRepository.findByName(name)).thenReturn(template);

        templateService.deleteTemplate(name);

        verify(templateRepository).delete(template);
    }

    @Test
    void testExists() {
        when(templateRepository.existsByName("exists")).thenReturn(true);
        assertTrue(templateService.exists("exists"));
    }

}
