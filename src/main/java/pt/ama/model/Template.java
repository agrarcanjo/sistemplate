package pt.ama.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Data;
import org.bson.types.ObjectId;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonId;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@MongoEntity(collection = "templates")
public class Template {
    @BsonId
    @JsonIgnore
    private ObjectId id;
    
    private String name;
    private DocumentType type;
    private String content; // HTML content
    private String description;
    private String author;
    private BigDecimal version;
    private String owner;
    private String manager;
    private boolean active = true;
    
    // Novos campos para melhor gestão
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String category; // Para organizar templates por categoria
    private List<String> tags; // Tags para facilitar busca
    private TemplateMetadata metadata;
    
    @Data
    public static class TemplateMetadata {
        private List<String> requiredFields; // Campos obrigatórios no JSON
        private List<String> optionalFields; // Campos opcionais
        private List<ImageReference> imageReferences; // Referências de imagens usadas
        private String sampleData; // JSON de exemplo para teste
        private String documentation; // Documentação do template
    }
    
    @Data
    public static class ImageReference {
        private String placeholder; // Nome do placeholder no template (ex: {logo})
        private String description; // Descrição da imagem
        private String recommendedSize; // Tamanho recomendado (ex: "200x100")
        private boolean required; // Se a imagem é obrigatória
    }
}