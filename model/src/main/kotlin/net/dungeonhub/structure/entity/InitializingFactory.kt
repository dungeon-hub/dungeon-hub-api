package net.dungeonhub.structure.entity

import net.dungeonhub.structure.model.Model

interface InitializingFactory<E : Entity<M>, M : Model> {
    fun transform(entity: E): E
}