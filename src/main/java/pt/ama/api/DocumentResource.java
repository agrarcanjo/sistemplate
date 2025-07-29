package pt.ama.api;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.ama.dto.DocumentRequest;
import pt.ama.dto.DocumentResponse;
import pt.ama.resource.JsonApiResource;
import pt.ama.service.DocumentService;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Path("/documents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DocumentResource extends JsonApiResource {

    private static final Logger LOG = Logger.getLogger(DocumentResource.class);
    private static final String CONTENT_TYPE = "application/pdf";

    @Inject
    DocumentService documentService;

    @POST
    @Path("/generate")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response generateDocument(@Valid DocumentRequest request) {
        LOG.infof("DocumentResource: Recebida solicitação para gerar documento - template: '%s'",
                request.getTemplateName());

        byte[] document = documentService.generateDocument(request);
        LOG.infof("DocumentResource: Documento gerado com sucesso - tamanho: %d bytes", document.length);

        String filename = buildFilename(request);
        LOG.infof("DocumentResource: Retornando documento com filename: '%s'", filename);

        return Response.ok(document)
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .header("Content-Type", CONTENT_TYPE)
                .build();
    }

    @POST
    @Path("/generate-code")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateCodeDocument(@Valid DocumentRequest request) {
        LOG.infof("DocumentResource: Recebida solicitação para gerar codigo em base64 do documento - template: '%s'",
                request.getTemplateName());

        byte[] document = documentService.generateDocument(request);
        LOG.infof("DocumentResource: Documento gerado com sucesso - tamanho: %d bytes", document.length);

        String filename = buildFilename(request);
        String base64Content = Base64.getEncoder().encodeToString(document);

        DocumentResponse response = new DocumentResponse(
                filename,
                CONTENT_TYPE,
                base64Content,
                (long) document.length,
                request.getTemplateName(),
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );

        LOG.infof("DocumentResource: Retornando documento codificado em base64 - filename: '%s', tamanho: %d bytes",
                filename, document.length);

        return Response.ok(response).build();
    }

    private String buildFilename(DocumentRequest request) {
        String filename = request.getTemplateName();
        if (request.getOptions() != null && request.getOptions().getFilename() != null) {
            filename = request.getOptions().getFilename();
        }

        if (!filename.toLowerCase().endsWith(".pdf")) {
            filename += ".pdf";
        }

        return filename;
    }
}