package net.dungeonhub.entity.model;

import me.taubsie.dungeonhub.common.entity.Entity;
import me.taubsie.dungeonhub.common.entity.InitializingFactory;

public interface InitializeModel<E extends Entity, C extends CreationModel> extends Model {
    E toEntity();

    InitializeModel<E, C> fromCreationModel(C creationModel);

    default E toEntity(InitializingFactory<E> factory) {
        return factory.transform(toEntity());
    }
}