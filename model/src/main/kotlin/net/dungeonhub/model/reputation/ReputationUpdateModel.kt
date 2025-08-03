package net.dungeonhub.model.reputation

import net.dungeonhub.structure.model.UpdateModel

class ReputationUpdateModel(
    var reason: String?,
    var amount: Int?,
    var active: Boolean?
) : UpdateModel<ReputationModel>