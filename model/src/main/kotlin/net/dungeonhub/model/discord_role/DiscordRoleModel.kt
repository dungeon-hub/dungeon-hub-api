package net.dungeonhub.model.discord_role

import net.dungeonhub.structure.model.Model
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.service.MoshiService

class DiscordRoleModel(
    val id: Long,
    val nameSchema: String?,
    val verifiedRole: Boolean,
    val discordServerModel: DiscordServerModel
) : Model {
    companion object {
        fun fromJson(json: String): DiscordRoleModel {
            return MoshiService.moshi.adapter(DiscordRoleModel::class.java).fromJson(json)!!
        }
    }
}