package net.dungeonhub.structure.entity

import net.dungeonhub.structure.model.Model

fun interface InitializingFactory<E : Entity<M>, M : Model> {
    fun transform(entity: E): E
}