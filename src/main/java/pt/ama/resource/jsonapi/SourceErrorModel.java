package pt.ama.resource.jsonapi;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SourceErrorModel {

    private String pointer;
    private String parameter;

    @Override
    public String toString() {
        return "SourceErrorModel{" +
                "pointer='" + pointer + '\'' +
                ", parameter='" + parameter + '\'' +
                '}';
    }

}
