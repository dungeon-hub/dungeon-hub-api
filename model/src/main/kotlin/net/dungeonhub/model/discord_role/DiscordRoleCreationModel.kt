package net.dungeonhub.model.discord_role

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.CreationModel

class DiscordRoleCreationModel(
    var id: Long,
    var nameSchema: String? = null,
    var verifiedRole: Boolean? = null
) : CreationModel {
    fun toJson(): String {
        return MoshiService.moshi.adapter(DiscordRoleCreationModel::class.java).toJson(this)
    }
}