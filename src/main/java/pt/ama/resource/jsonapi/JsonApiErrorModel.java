package pt.ama.resource.jsonapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JsonApiErrorModel {

    private String id;
    private String status;
    private String code;
    private String title;
    private String detail;
    private SourceErrorModel source;

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
