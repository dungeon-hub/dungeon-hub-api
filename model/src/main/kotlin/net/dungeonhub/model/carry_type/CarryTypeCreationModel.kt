package net.dungeonhub.model.carry_type

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.CreationModel

class CarryTypeCreationModel(
    var identifier: String,
    var displayName: String,
    var logChannel: Long? = null,
    var leaderboardChannel: Long? = null,
    var eventActive: Boolean? = null
) : CreationModel {
    fun toJson(): String {
        return MoshiService.moshi.adapter(CarryTypeCreationModel::class.java).toJson(this)
    }
}