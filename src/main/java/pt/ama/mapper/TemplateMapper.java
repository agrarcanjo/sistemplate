package pt.ama.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import pt.ama.dto.TemplateRequest;
import pt.ama.dto.TemplateResponse;
import pt.ama.model.Template;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TemplateMapper {

    public Template toEntity(TemplateRequest request) {
        if (request == null) {
            return null;
        }

        Template template = new Template();
        template.setName(request.getName());
        template.setType(request.getType());
        template.setContent(request.getContent());
        template.setDescription(request.getDescription());
        template.setAuthor(request.getAuthor());
        template.setOwner(request.getOwner());
        template.setManager(request.getManager());
        template.setCategory(request.getCategory());
        template.setTags(request.getTags());
        template.setVersion(BigDecimal.valueOf(1.0));
        template.setStatus(request.getStatus());

        if (request.getMetadata() != null) {
            Template.TemplateMetadata metadata = new Template.TemplateMetadata();
            metadata.setRequiredFields(request.getMetadata().getRequiredFields());
            metadata.setOptionalFields(request.getMetadata().getOptionalFields());
            metadata.setSampleData(request.getMetadata().getSampleData());
            metadata.setDocumentation(request.getMetadata().getDocumentation());
            
            template.setMetadata(metadata);
        }

        return template;
    }

    public TemplateResponse toResponse(Template template) {
        if (template == null) {
            return null;
        }

        TemplateResponse response = new TemplateResponse();
        response.setName(template.getName());
        response.setType(template.getType());
        response.setContent(template.getContent());
        response.setDescription(template.getDescription());
        response.setAuthor(template.getAuthor());
        response.setVersion(template.getVersion());
        response.setOwner(template.getOwner());
        response.setManager(template.getManager());
        response.setActive(template.isActive());
        response.setCreatedAt(template.getCreatedAt());
        response.setUpdatedAt(template.getUpdatedAt());
        response.setCategory(template.getCategory());
        response.setTags(template.getTags());

        if (template.getMetadata() != null) {
            TemplateResponse.TemplateMetadataResponse metadata = new TemplateResponse.TemplateMetadataResponse();
            metadata.setRequiredFields(template.getMetadata().getRequiredFields());
            metadata.setOptionalFields(template.getMetadata().getOptionalFields());
            metadata.setSampleData(template.getMetadata().getSampleData());
            metadata.setDocumentation(template.getMetadata().getDocumentation());

            response.setMetadata(metadata);
        }

        return response;
    }

    public List<TemplateResponse> toResponseList(List<Template> templates) {
        if (templates == null) {
            return null;
        }
        return templates.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void updateEntity(Template existingTemplate, TemplateRequest request) {
        if (existingTemplate == null || request == null) {
            return;
        }

        existingTemplate.setName(request.getName());
        existingTemplate.setType(request.getType());
        existingTemplate.setContent(request.getContent());
        existingTemplate.setDescription(request.getDescription());
        existingTemplate.setAuthor(request.getAuthor());
        existingTemplate.setOwner(request.getOwner());
        existingTemplate.setManager(request.getManager());
        existingTemplate.setCategory(request.getCategory());
        existingTemplate.setTags(request.getTags());

        if (existingTemplate.getVersion() != null) {
            existingTemplate.setVersion(existingTemplate.getVersion().add(BigDecimal.valueOf(0.1)));
        } else {
            existingTemplate.setVersion(BigDecimal.valueOf(1.1));
        }

        if (request.getMetadata() != null) {
            Template.TemplateMetadata metadata = new Template.TemplateMetadata();
            metadata.setRequiredFields(request.getMetadata().getRequiredFields());
            metadata.setOptionalFields(request.getMetadata().getOptionalFields());
            metadata.setSampleData(request.getMetadata().getSampleData());
            metadata.setDocumentation(request.getMetadata().getDocumentation());
            
            existingTemplate.setMetadata(metadata);
        } else {
            existingTemplate.setMetadata(null);
        }
    }
}