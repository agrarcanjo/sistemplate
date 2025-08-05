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
import pt.ama.dto.DocumentRequest;
import pt.ama.dto.DocumentResponse;
import pt.ama.resource.JsonApiResource;
import pt.ama.service.DocumentService;
import org.jboss.logging.Logger;

@Path("/api/documents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Document", description = "Operações de geração de documentos")
public class DocumentResource extends JsonApiResource {

    private static final Logger LOG = Logger.getLogger(DocumentResource.class);

    @Inject
    DocumentService documentService;

    @POST
    @Path("/generate")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Operation(summary = "Gera um documento PDF para download")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Documento gerado com sucesso"),
        @APIResponse(responseCode = "400", description = "Dados inválidos"),
        @APIResponse(responseCode = "404", description = "Template não encontrado")
    })
    public Response generateDocument(@Valid DocumentRequest request) {
        LOG.infof("Iniciando geração de documento para template: %s", request.getTemplateName());
        
        byte[] document = documentService.generateDocument(request);
        String filename = documentService.buildFilename(request);
        
        LOG.infof("Documento gerado com sucesso - tamanho: %d bytes", document.length);

        return Response.ok(document)
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .header("Content-Type", "application/pdf")
                .build();
    }

    @POST
    @Path("/generate-base64")
    @Operation(summary = "Gera um documento PDF codificado em Base64")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Documento gerado com sucesso"),
        @APIResponse(responseCode = "400", description = "Dados inválidos"),
        @APIResponse(responseCode = "404", description = "Template não encontrado")
    })
    public Response generateBase64Document(@Valid DocumentRequest request) {
        LOG.infof("Iniciando geração de documento Base64 para template: %s", request.getTemplateName());
        
        DocumentResponse response = documentService.generateBase64Document(request);
        
        LOG.infof("Documento Base64 gerado com sucesso - tamanho: %d bytes", response.getSize());

        return Response.ok(ok(response)).build();
    }
}