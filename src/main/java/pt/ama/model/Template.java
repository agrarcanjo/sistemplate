package pt.ama.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection="templates")
public class Template extends PanacheMongoEntity {
    public String name;
    public DocumentType type;
    public String content; // Qute template content

}
