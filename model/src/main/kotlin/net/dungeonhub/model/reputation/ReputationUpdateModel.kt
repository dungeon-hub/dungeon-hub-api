package net.dungeonhub.model.reputation

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateModel

class ReputationUpdateModel(
    var reason: String?,
    var amount: Int?,
    var active: Boolean?
) : UpdateModel<ReputationModel> {
    fun toJson(): String {
        return MoshiService.moshi.adapter(ReputationUpdateModel::class.java).toJson(this)
    }
}