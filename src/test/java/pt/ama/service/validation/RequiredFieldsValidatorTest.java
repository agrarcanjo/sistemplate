package pt.ama.service.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ama.exception.RequiredFieldsValidationException;
import pt.ama.model.Template;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RequiredFieldsValidatorTest {

    private RequiredFieldsValidator validator;
    private Template template;

    @BeforeEach
    void setUp() {
        validator = new RequiredFieldsValidator();
        template = new Template();
        template.setName("test-template");
    }

    @Test
    void shouldPassValidationWhenAllRequiredFieldsArePresent() {
        Template.TemplateMetadata metadata = new Template.TemplateMetadata();
        metadata.setRequiredFields(Arrays.asList("name", "email", "age"));
        template.setMetadata(metadata);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", "João Silva");
        dataMap.put("email", "joao@example.com");
        dataMap.put("age", 30);
        dataMap.put("optional", "valor opcional");

        assertDoesNotThrow(() -> validator.validateRequiredFields(template, dataMap));
    }

    @Test
    void shouldThrowExceptionWhenRequiredFieldsAreMissing() {
        Template.TemplateMetadata metadata = new Template.TemplateMetadata();
        metadata.setRequiredFields(Arrays.asList("name", "email", "age"));
        template.setMetadata(metadata);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", "João Silva");
        RequiredFieldsValidationException exception = assertThrows(
                RequiredFieldsValidationException.class,
                () -> validator.validateRequiredFields(template, dataMap)
        );

        assertEquals("test-template", exception.getTemplateName());
        List<String> missingFields = exception.getMissingFields();
        assertEquals(2, missingFields.size());
        assertTrue(missingFields.contains("email"));
        assertTrue(missingFields.contains("age"));
    }

    @Test
    void shouldPassValidationWhenNoRequiredFieldsAreDefined() {
        Template.TemplateMetadata metadata = new Template.TemplateMetadata();
        metadata.setRequiredFields(null);
        template.setMetadata(metadata);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("any", "value");

        assertDoesNotThrow(() -> validator.validateRequiredFields(template, dataMap));
    }

    @Test
    void shouldPassValidationWhenMetadataIsNull() {
        template.setMetadata(null);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("any", "value");

        assertDoesNotThrow(() -> validator.validateRequiredFields(template, dataMap));
    }

    @Test
    void shouldHandleNestedFieldsCorrectly() {
        Template.TemplateMetadata metadata = new Template.TemplateMetadata();
        metadata.setRequiredFields(Arrays.asList("user.name", "user.address.city"));
        template.setMetadata(metadata);

        Map<String, Object> userAddress = new HashMap<>();
        userAddress.put("city", "Lisboa");
        userAddress.put("country", "Portugal");

        Map<String, Object> user = new HashMap<>();
        user.put("name", "João Silva");
        user.put("address", userAddress);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("user", user);

        assertDoesNotThrow(() -> validator.validateRequiredFields(template, dataMap));
    }

    @Test
    void shouldThrowExceptionForMissingNestedFields() {

        Template.TemplateMetadata metadata = new Template.TemplateMetadata();
        metadata.setRequiredFields(Arrays.asList("user.name", "user.address.city"));
        template.setMetadata(metadata);

        Map<String, Object> user = new HashMap<>();
        user.put("name", "João Silva");

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("user", user);

        RequiredFieldsValidationException exception = assertThrows(
                RequiredFieldsValidationException.class,
                () -> validator.validateRequiredFields(template, dataMap)
        );

        assertTrue(exception.getMissingFields().contains("user.address.city"));
    }

    @Test
    void shouldTreatNullValuesAsMissingFields() {
        Template.TemplateMetadata metadata = new Template.TemplateMetadata();
        metadata.setRequiredFields(Arrays.asList("name", "email"));
        template.setMetadata(metadata);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", "João Silva");
        dataMap.put("email", null);

        RequiredFieldsValidationException exception = assertThrows(
                RequiredFieldsValidationException.class,
                () -> validator.validateRequiredFields(template, dataMap)
        );

        assertTrue(exception.getMissingFields().contains("email"));
    }
}