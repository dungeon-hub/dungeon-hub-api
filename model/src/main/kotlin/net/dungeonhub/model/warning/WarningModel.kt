package net.dungeonhub.model.warning

import net.dungeonhub.structure.model.Model
import net.dungeonhub.enums.WarningType
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.model.discord_user.DiscordUserModel
import net.dungeonhub.service.MoshiService
import java.time.Instant

class WarningModel(
    val id: Long,
    val server: DiscordServerModel,
    val user: DiscordUserModel,
    val striker: DiscordUserModel,
    val warningType: WarningType,
    val reason: String?,
    val active: Boolean,
    val time: Instant
) : Model {
    companion object {
        fun fromJson(json: String): WarningModel {
            return MoshiService.moshi.adapter(WarningModel::class.java).fromJson(json)!!
        }
    }
}