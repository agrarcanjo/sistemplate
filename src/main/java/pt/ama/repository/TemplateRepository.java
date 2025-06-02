package pt.ama.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import pt.ama.model.Template;
import pt.ama.model.DocumentType;

import java.util.List;

@ApplicationScoped
public class TemplateRepository implements PanacheMongoRepositoryBase<Template, String> {
    
    public Template findByName(String name) {
        return find("name", name).firstResult();
    }
    
    public List<Template> findByType(DocumentType type) {
        return list("type", type);
    }

    public List<Template> findByNameContaining(String namePattern) {
        return list("name", new org.bson.Document("$regex", namePattern).append("$options", "i"));
    }
    
    public void deleteByName(String name) {
        delete("name", name);
    }
    
    public boolean exists(String name) {
        return count("name", name) > 0;
    }
}