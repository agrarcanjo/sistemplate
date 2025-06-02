package pt.ama.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import pt.ama.model.Template;
import pt.ama.model.DocumentType;
import pt.ama.service.TemplateService;

import java.util.List;
import java.util.Map;

//@Path("/api/templates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Template", description = "Operações de gerenciamento de templates")
public class TemplateResource {

    @Inject
    TemplateService templateService;

    @GET
    @Operation(summary = "Lista todos os templates")
    public List<Template> getAllTemplates() {
        return templateService.findAll();
    }

    @GET
    @Path("/{name}")
    @Operation(summary = "Busca um template pelo nome")
    public Response getTemplateByName(@PathParam("name") String name) {
        Template template = templateService.findByName(name);
        if (template == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(template).build();
    }

    @GET
    @Path("/type/{type}")
    @Operation(summary = "Lista templates por tipo de documento")
    public List<Template> getTemplatesByType(@PathParam("type") DocumentType type) {
        return templateService.findByType(type);
    }

    @GET
    @Path("/search")
    @Operation(summary = "Busca templates por parte do nome")
    public List<Template> searchTemplates(@QueryParam("name") String namePattern) {
        return templateService.findByNameContaining(namePattern);
    }

    @POST
    @Operation(summary = "Cria um novo template")
    public Response createTemplate(Template template) {
        if (templateService.exists(template.getName())) {
            return Response.status(Response.Status.CONFLICT)
                         .entity("Template com este nome já existe")
                         .build();
        }
        templateService.save(template);
        return Response.status(Response.Status.CREATED).entity(template).build();
    }

    @PUT
    @Path("/{name}")
    @Operation(summary = "Atualiza um template existente")
    public Response updateTemplate(@PathParam("name") String name, Template template) {
        if (!templateService.exists(name)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        template.setName(name);
        templateService.save(template);
        return Response.ok(template).build();
    }

    @DELETE
    @Path("/{name}")
    @Operation(summary = "Remove um template")
    public Response deleteTemplate(@PathParam("name") String name) {
        if (!templateService.exists(name)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        templateService.delete(name);
        return Response.noContent().build();
    }

    @POST
    @Path("/{name}/generate-pdf")
    @Produces("application/pdf")
    @Operation(summary = "Gera um PDF a partir de um template")
    public Response generatePdf(@PathParam("name") String name, Map<String, Object> data) {
        try {
            byte[] pdf = templateService.generatePdf(name, data);
            StreamingOutput stream = output -> output.write(pdf);
            
            return Response.ok(stream)
                         .header("Content-Disposition", "attachment; filename=" + name + ".pdf")
                         .build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity("Erro ao gerar PDF: " + e.getMessage())
                         .build();
        }
    }
}