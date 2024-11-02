package net.dungeonhub.model.warning

import net.dungeonhub.enums.WarningType
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.model.discord_user.DiscordUserModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.Model
import java.time.Instant

class DetailedWarningModel(
    val id: Long,
    val server: DiscordServerModel,
    val user: DiscordUserModel,
    val striker: DiscordUserModel,
    val warningType: WarningType,
    val reason: String?,
    val active: Boolean,
    val time: Instant,
    val evidences: List<WarningEvidenceModel>
) : Model {
    companion object {
        fun fromJson(json: String): DetailedWarningModel {
            return MoshiService.moshi.adapter(DetailedWarningModel::class.java).fromJson(json)!!
        }
    }
}