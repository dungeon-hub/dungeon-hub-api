package net.dungeonhub.model.discord_channel

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateModel

class DiscordChannelUpdateModel(
    var name: String?,
    var deleted: Boolean?
) : UpdateModel<DiscordChannelModel> {
    fun toJson(): String {
        return MoshiService.moshi.adapter(DiscordChannelUpdateModel::class.java).toJson(this)
    }
}