package pt.ama.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import pt.ama.dto.TemplateRequest;
import pt.ama.dto.TemplateResponse;
import pt.ama.model.Template;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TemplateMapper {

    public Template toEntity(TemplateRequest request) {
        return null;
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
    }
}