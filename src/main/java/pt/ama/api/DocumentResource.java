package pt.ama.api;

import pt.ama.model.DocumentRequest;
import pt.ama.service.DocumentService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/documents")
public class DocumentResource {

    @Inject
    DocumentService documentService;

    @POST
    @Path("/generate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM) 
    public Response generateDocument(DocumentRequest request) {
        try {
            byte[] document = documentService.generateDocument(request);
            
            String filename = request.templateName + ".pdf"; 
            return Response.ok(document)
                           .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                           .build();
        } catch (RuntimeException e) {
            if (e.getMessage().startsWith("Template not found")) {
                return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error generating document: " + e.getMessage()).build();
        }
    }
}
