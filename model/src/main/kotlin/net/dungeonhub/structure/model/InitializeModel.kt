package net.dungeonhub.structure.model

import net.dungeonhub.structure.entity.Entity

interface InitializeModel<E : Entity<M>, M : Model, C : CreationModel> : Model {
    fun toEntity(): E

    fun fromCreationModel(creationModel: C): InitializeModel<E, M, C>
}