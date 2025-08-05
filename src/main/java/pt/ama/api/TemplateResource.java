package pt.ama.api;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import pt.ama.dto.TemplateRequest;
import pt.ama.dto.TemplateResponse;
import pt.ama.mapper.TemplateMapper;
import pt.ama.model.DocumentType;
import pt.ama.model.Template;
import pt.ama.resource.JsonApiResource;
import pt.ama.service.TemplateService;

import java.util.List;

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
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Lista de templates retornada com sucesso"),
        @APIResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Response getAllTemplates() {
        List<Template> templates = templateService.findAll();
        List<TemplateResponse> responses = templateMapper.toResponseList(templates);
        return Response.ok(ok(responses)).build();
    }

    @GET
    @Path("/{name}")
    @Operation(summary = "Busca um template pelo nome")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Template encontrado"),
        @APIResponse(responseCode = "404", description = "Template não encontrado")
    })
    public Response getTemplateByName(@PathParam("name") String name) {
        Template template = templateService.findByNameOrThrow(name);
        TemplateResponse response = templateMapper.toResponse(template);
        return Response.ok(ok(response)).build();
    }

    @GET
    @Path("/type/{type}")
    @Operation(summary = "Lista templates por tipo de documento")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Templates encontrados"),
        @APIResponse(responseCode = "400", description = "Tipo de documento inválido")
    })
    public Response getTemplatesByType(@PathParam("type") DocumentType type) {
        List<Template> templates = templateService.findByType(type);
        List<TemplateResponse> responses = templateMapper.toResponseList(templates);
        return Response.ok(ok(responses)).build();
    }

    @GET
    @Path("/search")
    @Operation(summary = "Busca templates por parte do nome")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Busca realizada com sucesso"),
        @APIResponse(responseCode = "400", description = "Parâmetro de busca inválido")
    })
    public Response searchTemplates(@QueryParam("name") String namePattern) {
        if (namePattern == null || namePattern.trim().isEmpty()) {
            throw new IllegalArgumentException("Parâmetro 'name' é obrigatório para busca");
        }
        
        List<Template> templates = templateService.findByNameContaining(namePattern.trim());
        List<TemplateResponse> responses = templateMapper.toResponseList(templates);
        return Response.ok(ok(responses)).build();
    }

    @GET
    @Path("/{name}/versions")
    @Operation(summary = "Lista todas as versões de um template")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Versões encontradas"),
        @APIResponse(responseCode = "404", description = "Template não encontrado")
    })
    public Response getTemplateVersions(@PathParam("name") String name) {
        List<Template> templates = templateService.findByTemplateVersion(name);
        List<TemplateResponse> responses = templateMapper.toResponseList(templates);
        return Response.ok(ok(responses)).build();
    }

    @POST
    @Operation(summary = "Cria um novo template")
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Template criado com sucesso"),
        @APIResponse(responseCode = "400", description = "Dados inválidos"),
        @APIResponse(responseCode = "409", description = "Template já existe")
    })
    public Response createTemplate(@Valid TemplateRequest templateRequest) {
        Template template = templateService.createTemplate(templateRequest);
        TemplateResponse response = templateMapper.toResponse(template);
        return Response.status(Response.Status.CREATED).entity(ok(response)).build();
    }

    @PUT
    @Path("/{name}")
    @Operation(summary = "Atualiza um template existente")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Template atualizado com sucesso"),
        @APIResponse(responseCode = "400", description = "Dados inválidos"),
        @APIResponse(responseCode = "404", description = "Template não encontrado")
    })
    public Response updateTemplate(@PathParam("name") String name, @Valid TemplateRequest templateRequest) {
        Template template = templateService.updateTemplate(name, templateRequest);
        TemplateResponse response = templateMapper.toResponse(template);
        return Response.ok(ok(response)).build();
    }

    @DELETE
    @Path("/{name}")
    @Operation(summary = "Remove um template")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Template removido com sucesso"),
        @APIResponse(responseCode = "404", description = "Template não encontrado"),
        @APIResponse(responseCode = "409", description = "Template não pode ser removido")
    })
    public Response deleteTemplate(@PathParam("name") String name) {
        templateService.deleteTemplate(name);
        return Response.noContent().build();
    }
}