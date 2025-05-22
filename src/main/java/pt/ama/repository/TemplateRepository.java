package pt.ama.repository;

import pt.ama.model.Template;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TemplateRepository implements PanacheMongoRepository<Template> {
    // PanacheMongoRepository provides common MongoDB operations
}
