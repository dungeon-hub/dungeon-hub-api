package net.dungeonhub.model.static_message

import net.dungeonhub.enums.StaticMessageType
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateableModel

class StaticMessageModel(
    val id: Long,
    val server: DiscordServerModel,
    val channelId: Long,
    val messageId: Long?,
    val staticMessageType: StaticMessageType,
    val objectIds: List<Long>
) : UpdateableModel<StaticMessageUpdateModel, StaticMessageModel> {
    override fun getUpdateModel(): StaticMessageUpdateModel {
        return StaticMessageUpdateModel(null, null, null)
    }

    companion object {
        fun fromJson(json: String): StaticMessageModel {
            return MoshiService.moshi.adapter(StaticMessageModel::class.java).fromJson(json)!!
        }
    }
}
