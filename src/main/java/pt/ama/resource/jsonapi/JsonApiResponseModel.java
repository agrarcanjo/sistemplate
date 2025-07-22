package pt.ama.resource.jsonapi;

import lombok.Getter;

import java.util.List;

@Getter
public class JsonApiResponseModel<T> {

    private List<JsonApiErrorModel> errors;

    private T data;

    private ApiVersionModel jsonapi;

    public JsonApiResponseModel() {
        super();
    }

    public JsonApiResponseModel(T data, String version) {
        this.data = data;
        this.jsonapi = new ApiVersionModel(version);
    }

    public JsonApiResponseModel(List<JsonApiErrorModel> errors, String version) {
        this.errors = errors;
        this.jsonapi = new ApiVersionModel(version);
    }

    public static <T> JsonApiResponseModel<T> ok(T data, String version) {
        return new JsonApiResponseModel<>(data, version);
    }

    public static <T> JsonApiResponseModel<T> error(List<JsonApiErrorModel> errors, String version) {
        return new JsonApiResponseModel<>(errors, version);
    }

}
