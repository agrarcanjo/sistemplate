package pt.ama.resource.jsonapi;

import java.util.UUID;

public interface JsonApiModel {

    default String getId() {
        return UUID.randomUUID().toString();
    }

    String getType();

}
