package net.dungeonhub.structure.model

import io.swagger.v3.oas.annotations.media.Schema

interface UpdateableModel<U : UpdateModel<M>, M : Model> : Model {
    @Schema(hidden = true)
    fun getUpdateModel(): U
}