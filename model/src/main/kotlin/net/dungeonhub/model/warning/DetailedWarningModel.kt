package net.dungeonhub.model.warning

import net.dungeonhub.enums.WarningType
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.model.discord_user.DiscordUserModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.Model
import java.time.Instant

class DetailedWarningModel(
    private val id: Long,
    private val server: DiscordServerModel,
    private val user: DiscordUserModel,
    private val striker: DiscordUserModel,
    private val warningType: WarningType,
    private val reason: String?,
    private val active: Boolean,
    private val time: Instant,
    private val evidences: List<WarningEvidenceModel>
) : Model {
    companion object {
        fun fromJson(json: String): DetailedWarningModel {
            return MoshiService.moshi.adapter(DetailedWarningModel::class.java).fromJson(json)!!
        }
    }
}