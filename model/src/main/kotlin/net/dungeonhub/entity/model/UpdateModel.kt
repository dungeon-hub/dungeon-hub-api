package net.dungeonhub.entity.model

interface UpdateModel<M : Model> {
    fun apply(model: M): M
}