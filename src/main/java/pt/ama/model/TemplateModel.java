package pt.ama.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import lombok.Data;

@Data
@MongoEntity(collection="templates")
public class TemplateModel {
    @BsonId
    private ObjectId id;
    private String name;
    private DocumentType type;
    private String content;
    private String description;
}