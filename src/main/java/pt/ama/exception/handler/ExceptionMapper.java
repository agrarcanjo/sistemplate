package pt.ama.exception.handler;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import pt.ama.exception.CallbackIdNotFoundException;
import pt.ama.resource.jsonapi.JsonApiErrorModel;
import pt.ama.resource.jsonapi.JsonApiResponseModel;

import java.util.List;
import java.util.UUID;

public class ExceptionMapper {

    private static final Logger LOG = Logger.getLogger(ExceptionMapper.class);

    @ConfigProperty(name = "quarkus.application.version")
    String version;

    @ServerExceptionMapper
    public RestResponse<JsonApiResponseModel<Void>> mapNotFoundException(NotFoundException e) {
        return mapException(e, Response.Status.NOT_FOUND);
    }

    @ServerExceptionMapper
    public RestResponse<JsonApiResponseModel<Void>> mapCallbackIdNotFoundException(CallbackIdNotFoundException e) {
        return mapException(e, Response.Status.NOT_FOUND);
    }

    @ServerExceptionMapper
    public RestResponse<JsonApiResponseModel<Void>> mapBadRequestException(BadRequestException e) {
        return mapException(e, Response.Status.BAD_REQUEST);
    }

    @ServerExceptionMapper
    public RestResponse<JsonApiResponseModel<Void>> mapException(Exception e) {
        return mapException(e, Response.Status.INTERNAL_SERVER_ERROR);
    }

    private RestResponse<JsonApiResponseModel<Void>> mapException(Exception e, Response.Status status) {
        return mapException(UUID.randomUUID().toString(), e, status);
    }

    private RestResponse<JsonApiResponseModel<Void>> mapException(String id, Exception e, Response.Status status) {
        LOG.error("Error on process resource", e);

        JsonApiErrorModel error = new JsonApiErrorModel();
        error.setId(id);
        error.setStatus(String.valueOf(status.getStatusCode()));
        error.setTitle(e.getMessage());
        error.setDetail(e.getMessage());

        JsonApiResponseModel<Void> response = new JsonApiResponseModel<>(List.of(error), version);

        return RestResponse.status(status, response);
    }

}
