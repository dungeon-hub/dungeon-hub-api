package net.dungeonhub.model.discord_user

import net.dungeonhub.structure.model.Model
import net.dungeonhub.service.MoshiService
import java.util.*

class DiscordUserModel(
    val id: Long,
    val minecraftId: UUID?
) : Model {
    companion object {
        fun fromJson(json: String): DiscordUserModel {
            return MoshiService.moshi.adapter(DiscordUserModel::class.java).fromJson(json)!!
        }
    }
}