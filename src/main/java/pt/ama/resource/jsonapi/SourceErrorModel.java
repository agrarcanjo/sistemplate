package pt.ama.resource.jsonapi;

public class SourceErrorModel {

    private String pointer;
    private String parameter;

    public String getPointer() {
        return pointer;
    }

    public void setPointer(String pointer) {
        this.pointer = pointer;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        return "SourceErrorModel{" +
                "pointer='" + pointer + '\'' +
                ", parameter='" + parameter + '\'' +
                '}';
    }

}
