package pt.ama.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Data;
import org.bson.types.ObjectId;
import org.bson.codecs.pojo.annotations.BsonId;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Data
@MongoEntity(collection = "images")
public class ImageAsset {
    @BsonId
    @JsonIgnore
    private ObjectId id;
    
    private String name; // Nome único da imagem
    private String originalFilename;
    private String contentType; // image/png, image/jpeg, etc.
    private long size; // Tamanho em bytes
    private String hash; // Hash MD5 para evitar duplicatas
    private byte[] data; // Dados binários da imagem
    
    // Metadados
    private int width;
    private int height;
    private String description;
    private String category;
    private String owner;
    
    // Controle
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active = true;
    
    // Para otimização
    private String base64Data; // Cache do base64 para uso em templates
    private String thumbnailBase64; // Thumbnail para preview
}