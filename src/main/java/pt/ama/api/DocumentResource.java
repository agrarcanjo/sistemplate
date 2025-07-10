package pt.ama.api;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.ama.model.DocumentRequest;
import pt.ama.service.DocumentService;
import org.jboss.logging.Logger;

@Path("/documents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DocumentResource {

    private static final Logger LOG = Logger.getLogger(DocumentResource.class);

    @Inject
    DocumentService documentService;

    @POST
    @Path("/generate")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response generateDocument(@Valid DocumentRequest request) {
        LOG.infof("DocumentResource: Recebida solicitação para gerar documento - template: '%s'", 
                 request.getTemplateName());
        
        try {
            byte[] document = documentService.generateDocument(request);
            LOG.infof("DocumentResource: Documento gerado com sucesso - tamanho: %d bytes", document.length);

            String filename = request.getTemplateName();
            if (request.getOptions() != null && request.getOptions().getFilename() != null) {
                filename = request.getOptions().getFilename();
            }

            if (!filename.toLowerCase().endsWith(".pdf")) {
                filename += ".pdf";
            }
            
            LOG.infof("DocumentResource: Retornando documento com filename: '%s'", filename);
            
            return Response.ok(document)
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .header("Content-Type", "application/pdf")
                    .build();
                    
        } catch (RuntimeException e) {
            LOG.errorf("DocumentResource: Erro RuntimeException: %s", e.getMessage());
            
            if (e.getMessage().contains("Template not found")) {
                LOG.warnf("DocumentResource: Template não encontrado: '%s'", request.getTemplateName());
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Template não encontrado: " + request.getTemplateName())
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            } else {
                LOG.errorf("DocumentResource: Erro interno: %s", e.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Erro interno do servidor: " + e.getMessage())
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        } catch (Exception e) {
            LOG.errorf("DocumentResource: Erro genérico: %s", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno do servidor: " + e.getMessage())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}