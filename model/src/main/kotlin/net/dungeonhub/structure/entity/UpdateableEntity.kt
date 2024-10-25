package net.dungeonhub.structure.entity

import net.dungeonhub.structure.model.Model
import net.dungeonhub.structure.model.UpdateModel

interface UpdateableEntity<M : Model, U : UpdateModel<M>> : Entity<M> {
    fun update(updateModel: U): Entity<M>
}