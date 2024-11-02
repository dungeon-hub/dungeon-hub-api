package net.dungeonhub.structure.model

import net.dungeonhub.structure.entity.Entity
import net.dungeonhub.structure.entity.InitializingFactory

interface InitializeModel<E : Entity<M>, M : Model, C : CreationModel?> : Model {
    fun toEntity(): E

    fun fromCreationModel(creationModel: C): InitializeModel<E, M, C>

    fun toEntity(factory: InitializingFactory<E, M>): E {
        return factory.transform(toEntity())
    }
}