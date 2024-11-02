package net.dungeonhub.model.discord_user

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateableModel
import java.util.*

class DiscordUserModel(
    val id: Long,
    val minecraftId: UUID?
) : UpdateableModel<DiscordUserUpdateModel, DiscordUserModel> {
    companion object {
        fun fromJson(json: String): DiscordUserModel {
            return MoshiService.moshi.adapter(DiscordUserModel::class.java).fromJson(json)!!
        }
    }

    override fun getUpdateModel(): DiscordUserUpdateModel {
        return DiscordUserUpdateModel(null)
    }
}