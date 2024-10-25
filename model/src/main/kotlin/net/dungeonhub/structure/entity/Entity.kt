package net.dungeonhub.structure.entity

import net.dungeonhub.structure.model.Model

interface Entity<M : Model> {
    fun toModel(): M
}