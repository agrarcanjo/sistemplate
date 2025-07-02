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
        
        // Mapear metadados
        if (request.getMetadata() != null) {
            Template.TemplateMetadata metadata = new Template.TemplateMetadata();
            metadata.setRequiredFields(request.getMetadata().getRequiredFields());
            metadata.setOptionalFields(request.getMetadata().getOptionalFields());
            metadata.setSampleData(request.getMetadata().getSampleData());
            metadata.setDocumentation(request.getMetadata().getDocumentation());
            
            // Mapear referências de imagens
            if (request.getMetadata().getImageReferences() != null) {
                List<Template.ImageReference> imageRefs = request.getMetadata().getImageReferences()
                    .stream()
                    .map(this::mapImageReference)
                    .collect(Collectors.toList());
                metadata.setImageReferences(imageRefs);
            }
            
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
        
        // Mapear metadados
        if (template.getMetadata() != null) {
            TemplateResponse.TemplateMetadataResponse metadata = new TemplateResponse.TemplateMetadataResponse();
            metadata.setRequiredFields(template.getMetadata().getRequiredFields());
            metadata.setOptionalFields(template.getMetadata().getOptionalFields());
            metadata.setSampleData(template.getMetadata().getSampleData());
            metadata.setDocumentation(template.getMetadata().getDocumentation());
            
            // Mapear referências de imagens
            if (template.getMetadata().getImageReferences() != null) {
                List<TemplateResponse.ImageReferenceResponse> imageRefs = template.getMetadata().getImageReferences()
                    .stream()
                    .map(this::mapImageReferenceResponse)
                    .collect(Collectors.toList());
                metadata.setImageReferences(imageRefs);
            }
            
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

    public void updateEntity(Template template, TemplateRequest request) {
        if (template == null || request == null) {
            return;
        }

        template.setName(request.getName());
        template.setType(request.getType());
        template.setContent(request.getContent());
        template.setDescription(request.getDescription());
        template.setAuthor(request.getAuthor());
        template.setOwner(request.getOwner());
        template.setManager(request.getManager());
        template.setCategory(request.getCategory());
        template.setTags(request.getTags());
        
        // Atualizar metadados
        if (request.getMetadata() != null) {
            Template.TemplateMetadata metadata = new Template.TemplateMetadata();
            metadata.setRequiredFields(request.getMetadata().getRequiredFields());
            metadata.setOptionalFields(request.getMetadata().getOptionalFields());
            metadata.setSampleData(request.getMetadata().getSampleData());
            metadata.setDocumentation(request.getMetadata().getDocumentation());
            
            // Mapear referências de imagens
            if (request.getMetadata().getImageReferences() != null) {
                List<Template.ImageReference> imageRefs = request.getMetadata().getImageReferences()
                    .stream()
                    .map(this::mapImageReference)
                    .collect(Collectors.toList());
                metadata.setImageReferences(imageRefs);
            }
            
            template.setMetadata(metadata);
        }
    }
    
    private Template.ImageReference mapImageReference(TemplateRequest.ImageReferenceRequest request) {
        Template.ImageReference imageRef = new Template.ImageReference();
        imageRef.setPlaceholder(request.getPlaceholder());
        imageRef.setDescription(request.getDescription());
        imageRef.setRecommendedSize(request.getRecommendedSize());
        imageRef.setRequired(request.isRequired());
        return imageRef;
    }
    
    private TemplateResponse.ImageReferenceResponse mapImageReferenceResponse(Template.ImageReference imageRef) {
        TemplateResponse.ImageReferenceResponse response = new TemplateResponse.ImageReferenceResponse();
        response.setPlaceholder(imageRef.getPlaceholder());
        response.setDescription(imageRef.getDescription());
        response.setRecommendedSize(imageRef.getRecommendedSize());
        response.setRequired(imageRef.isRequired());
        return response;
    }
}