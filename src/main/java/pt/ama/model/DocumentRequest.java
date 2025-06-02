package pt.ama.model;

import lombok.Data;

import java.util.Map;

@Data
public class DocumentRequest {
    public String templateName;
    public Map<String, Object> metadata;
}
