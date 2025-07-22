package pt.ama.resource;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import pt.ama.resource.jsonapi.JsonApiResponseModel;

public abstract class JsonApiResource {

    @ConfigProperty(name = "quarkus.application.version")
    String version;

    protected <T> JsonApiResponseModel<T> ok(T data) {
        return new JsonApiResponseModel<>(data, version);
    }

}
