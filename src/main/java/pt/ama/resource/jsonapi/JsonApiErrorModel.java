package pt.ama.resource.jsonapi;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JsonApiErrorModel {

    private String id;
    private String status;
    private String code;
    private String title;
    private String detail;
    private SourceErrorModel source;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public SourceErrorModel getSource() {
        return source;
    }

    public void setSource(SourceErrorModel source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "JsonApiErrorModel{" +
                "code='" + code + '\'' +
                ", title='" + title + '\'' +
                ", detail='" + detail + '\'' +
                ", source=" + source +
                '}';
    }

}
