package net.dungeonhub.model.reputation

import net.dungeonhub.structure.model.UpdateModel

class ReputationUpdateModel(
    val reason: String?,
    val amount: Int?,
    val active: Boolean?
) : UpdateModel<ReputationModel>