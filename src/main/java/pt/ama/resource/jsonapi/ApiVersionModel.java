package pt.ama.resource.jsonapi;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiVersionModel {

    private String version;

    public ApiVersionModel() {
        super();
    }

    public ApiVersionModel(String version) {
        this.version = version;
    }

}
