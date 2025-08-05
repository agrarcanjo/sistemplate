package pt.ama.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@MongoEntity(collection = "templates")
public class Template {
    @BsonId
    private ObjectId id;
    
    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotNull(message = "Type is mandatory")
    private DocumentType type;

    @NotBlank(message = "Content is mandatory")
    private String content;
    private String description;
    private String author;
    private BigDecimal version;
    private String owner;
    private String manager;
    private boolean active = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String category;
    private List<String> tags;
    private TemplateMetadata metadata;
    private DocumentStatus status;

    @Data
    public static class TemplateMetadata {
        @NotBlank(message = "Required fields are mandatory")
        private List<String> requiredFields; // Campos obrigatórios no JSON
        private List<String> optionalFields; // Campos opcionais
        private String sampleData; // JSON de exemplo para teste
        private String documentation; // Documentação do template
    }

    @Getter
    public enum DocumentStatus {
        RASCUNHO ("Rascunho"),
        SUBMETIDO("Submetido"),
        A_AGUARDAR_VALIDACAO("A Aguardar Validação"),
        EM_VALIDACAO("Em Validação"),
        APROVADO("Aprovado"),
        REJEITADO("Rejeitado"),
        EM_ESPERA_INFORMACAO("Em Espera de Informação"),
        ATIVO("Ativo"),
        INATIVO("Inativo");

        private final String description;

        DocumentStatus(String description) { this.description = description; }

    }

}