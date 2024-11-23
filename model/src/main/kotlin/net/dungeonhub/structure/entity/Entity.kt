package net.dungeonhub.structure.entity

import net.dungeonhub.structure.model.Model

fun interface Entity<M : Model> {
    fun toModel(): M
}