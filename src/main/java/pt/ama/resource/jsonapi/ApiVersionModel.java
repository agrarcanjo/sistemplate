package pt.ama.resource.jsonapi;

public class ApiVersionModel {

    private String version;

    public ApiVersionModel() {
        super();
    }

    public ApiVersionModel(String version) {
        this.version = version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

}
