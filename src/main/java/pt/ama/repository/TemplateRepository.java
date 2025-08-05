package pt.ama.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import pt.ama.model.DocumentType;
import pt.ama.model.Template;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class TemplateRepository implements PanacheMongoRepositoryBase<Template, ObjectId> {

    private static final Logger LOG = Logger.getLogger(TemplateRepository.class);

    public Template findByName(String name) {
        LOG.infof("Buscando template com nome: '%s'", name);
        return find("{'name': {'$regex': ?1, '$options': 'i'}, 'active': true}", "^" + name + "$").firstResult();
    }

    public List<Template> findByType(DocumentType type) {
        return find("type = ?1 and active = true", type).list();
    }

    public List<Template> findByNameContaining(String namePattern) {
        return find("name like ?1 and active = true", ".*" + namePattern + ".*").list();
    }

    public void deleteByName(String name) {
        delete("name = ?1", name);
    }

    public boolean existsByName(String name) {
        return count("name = ?1 and active = true", name) > 0;
    }

    public List<Template> findByTemplateVersion(String name) {
        return find("name = ?1 and active = true", name).list();
    }
    
    public List<Template> findByCategory(String category) {
        return find("category = ?1 and active = true", category).list();
    }
    
    public List<Template> findByOwner(String owner) {
        return find("owner = ?1 and active = true", owner).list();
    }
    
    public List<Template> findByTags(String tag) {
        return find("tags in ?1 and active = true", tag).list();
    }
    
    public void softDelete(String name) {
        update("active = false where name = ?1", name);
    }
}