package net.dungeonhub.model.discord_channel

import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.structure.model.UpdateableModel

class DiscordChannelModel (
    val id: Long,
    val name: String?,
    val discordServer: DiscordServerModel,
    val deleted: Boolean
) : UpdateableModel<DiscordChannelUpdateModel, DiscordChannelModel> {
    override fun getUpdateModel(): DiscordChannelUpdateModel {
        return DiscordChannelUpdateModel(null, null)
    }
}