package net.dungeonhub.model.cnt_request

import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.model.discord_user.DiscordUserModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateableModel
import java.time.Instant

class CntRequestModel(
    val id: Long,
    val messageId: Long,
    val discordServer: DiscordServerModel,
    val user: DiscordUserModel,
    val claimer: DiscordUserModel?,
    val time: Instant,
    val coinValue: String,
    val description: String,
    val requirement: String
) : UpdateableModel<CntRequestUpdateModel, CntRequestModel> {
    companion object {
        fun fromJson(json: String): CntRequestModel {
            return MoshiService.moshi.adapter(CntRequestModel::class.java).fromJson(json)!!
        }
    }

    override fun getUpdateModel(): CntRequestUpdateModel {
        return CntRequestUpdateModel(null, null, null, null)
    }
}