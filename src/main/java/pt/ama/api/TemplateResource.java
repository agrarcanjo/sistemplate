package pt.ama.api;

import pt.ama.model.Template;
import pt.ama.service.TemplateService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/templates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TemplateResource {

    @Inject
    TemplateService templateService;

    @GET
    public List<Template> getAllTemplates() {
        return templateService.listAll();
    }

    @POST
    public Response createTemplate(Template template) {
        templateService.add(template);
        return Response.status(Response.Status.CREATED).entity(template).build();
    }

    @GET
    @Path("/{name}")
    public Response getTemplateByName(@PathParam("name") String name) {
        Template template = templateService.findByName(name);
        if (template == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(template).build();
    }

    @POST
    @Path("/{name}/upload")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response uploadTemplateContent(@PathParam("name") String name, String content) {
        Template template = templateService.findByName(name);
        if (template == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        template.content = content;
        templateService.add(template); // Assuming add will update if it exists or use a specific update method
        return Response.ok(template).build();
    }
}
