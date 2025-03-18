package net.dungeonhub.model.discord_channel

import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.CreationModel

class DiscordChannelCreationModel(
    var id: Long,
    var name: String?,
    var discordServer: DiscordServerModel
) : CreationModel {
    fun toJson(): String {
        return MoshiService.moshi.adapter(DiscordChannelCreationModel::class.java).toJson(this)
    }
}