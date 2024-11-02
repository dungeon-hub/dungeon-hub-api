package net.dungeonhub.structure.model

interface UpdateableModel<U : UpdateModel<M>, M : Model> : Model {
    fun getUpdateModel(): U
}