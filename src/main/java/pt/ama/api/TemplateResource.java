package pt.ama.api;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import pt.ama.dto.TemplateRequest;
import pt.ama.dto.TemplateResponse;
import pt.ama.mapper.TemplateMapper;
import pt.ama.model.DocumentType;
import pt.ama.model.Template;
import pt.ama.resource.JsonApiResource;
import pt.ama.service.TemplateService;

import java.util.List;
import java.util.Map;

@Path("/api/templates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Template", description = "Operações de gerenciamento de templates")
public class TemplateResource extends JsonApiResource {

    @Inject
    TemplateService templateService;

    @Inject
    TemplateMapper templateMapper;

    @GET
    @Operation(summary = "Lista todos os templates")
    public Response getAllTemplates() {
        List<Template> templates = templateService.findAll();
        return Response.ok(templateMapper.toResponseList(templates)).build();
    }

    @GET
    @Path("/{name}/versions")
    @Operation(summary = "Versões do template")
    public Response getAllTemplateVersions(@PathParam("name") String name) {
        List<Template> templates = templateService.findByTemplateVersion(name);
        return Response.ok(templateMapper.toResponseList(templates)).build();
    }


    @GET
    @Path("/{name}")
    @Operation(summary = "Busca um template pelo nome")
    public Response getTemplateByName(@PathParam("name") String name) {
        Template template = templateService.findByName(name);
        if (template == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        TemplateResponse response = templateMapper.toResponse(template);
        return Response.ok(response).build();
    }

    @GET
    @Path("/type/{type}")
    @Operation(summary = "Lista templates por tipo de documento")
    public List<TemplateResponse> getTemplatesByType(@PathParam("type") DocumentType type){
            List<Template> templates = templateService.findByType(type);
            return templateMapper.toResponseList(templates);
        }

    @GET
    @Path("/search")
    @Operation(summary = "Busca templates por parte do nome")
    public List<TemplateResponse> searchTemplates(@QueryParam("name") String namePattern) {
        List<Template> templates = templateService.findByNameContaining(namePattern);
        return templateMapper.toResponseList(templates);
    }


    @GET
    @Path("/fake")
    public Response createFakeTemplate() {
        TemplateRequest temp = new TemplateRequest();
        temp.setType(DocumentType.PDF);
        temp.setName("default-temp");
        temp.setContent("<!DOCTYPE html> <html> <head> <meta charset=\"UTF-8\"> <style> body { font-family: Arial, sans-serif; margin: 40px; line-height: 1.6; } .header { text-align: center; margin-bottom: 30px; } .content { margin: 20px 0; } .footer { margin-top: 50px; text-align: center; font-size: 0.9em; } </style> </head> <body> <div class=\"header\"> <h1>{header}</h1> </div> <div class=\"content\"> {#if recipient} <p>Prezado(a) {recipient},</p> {/if} {content} {#if items} <ul> {#for item in items} <li>{item}</li> {/for} </ul> {/if} </div> <div class=\"footer\"> {#if date} <p>Data: {date}</p> {/if} {#if signature} <p>{signature}</p> {/if} </div> </body> </html>");
        templateService.save(templateMapper.toEntity(temp));

        return Response.status(Response.Status.CREATED).entity(temp).build();
    }


    @POST
    @Operation(summary = "Cria um novo template")
    public Response createTemplate(@Valid TemplateRequest templateRequest) {
        try {
            if (templateService.exists(templateRequest.getName())) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Template com este nome já existe").build();
            }
            
            Template template = templateMapper.toEntity(templateRequest);
            templateService.save(template);
            
            TemplateResponse response = templateMapper.toResponse(template);
            return Response.status(Response.Status.CREATED).entity(response).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Erro ao criar template: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{name}")
    @Operation(summary = "Atualiza um template existente")
    public Response updateTemplate(@PathParam("name") String name, @Valid TemplateRequest templateRequest) {
        try {
            Template existingTemplate = templateService.findByName(name);
            if (existingTemplate == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Template não encontrado").build();
            }
            
            templateMapper.updateEntity(existingTemplate, templateRequest);
            templateService.update(existingTemplate);
            
            TemplateResponse response = templateMapper.toResponse(existingTemplate);
            return Response.ok(response).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Erro ao atualizar template: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{name}")
    @Operation(summary = "Remove um template")
    public Response deleteTemplate(@PathParam("name") String name) {
        try {
            if (!templateService.exists(name)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Template não encontrado").build();
            }
            
            templateService.delete(name);
            return Response.noContent().build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao deletar template: " + e.getMessage()).build();
        }
    }
    @POST
    @Path("/{name}/generate-pdf")
    @Operation(summary = "Gera um PDF a partir de um template")
    public Response generatePdf(@PathParam("name") String name, @Valid  Map<String, Object> data) {
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