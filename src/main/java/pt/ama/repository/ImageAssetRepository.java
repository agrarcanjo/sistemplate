package pt.ama.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import pt.ama.model.ImageAsset;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ImageAssetRepository implements PanacheMongoRepositoryBase<ImageAsset, ObjectId> {
    
    public Optional<ImageAsset> findByName(String name) {
        return find("name = ?1 and active = true", name).firstResultOptional();
    }
    
    public List<ImageAsset> findByCategory(String category) {
        return find("category = ?1 and active = true", category).list();
    }
    
    public Optional<ImageAsset> findByHash(String hash) {
        return find("hash = ?1 and active = true", hash).firstResultOptional();
    }
    
    public List<ImageAsset> findByOwner(String owner) {
        return find("owner = ?1 and active = true", owner).list();
    }
    
    public boolean existsByName(String name) {
        return count("name = ?1 and active = true", name) > 0;
    }
    
    public void softDelete(String name) {
        update("active = false where name = ?1", name);
    }
}