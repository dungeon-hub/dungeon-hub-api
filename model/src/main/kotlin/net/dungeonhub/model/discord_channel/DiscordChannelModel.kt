package net.dungeonhub.model.discord_channel

import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateableModel

class DiscordChannelModel (
    val id: Long,
    val name: String?,
    val discordServer: DiscordServerModel
) : UpdateableModel<DiscordChannelUpdateModel, DiscordChannelModel> {
    companion object {
        fun fromJson(json: String): DiscordChannelModel {
            return MoshiService.moshi.adapter(DiscordChannelModel::class.java).fromJson(json)!!
        }
    }

    override fun getUpdateModel(): DiscordChannelUpdateModel {
        return DiscordChannelUpdateModel(null)
    }
}