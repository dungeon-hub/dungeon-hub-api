package net.dungeonhub.model.discord_user

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.CreationModel
import java.util.*

class DiscordUserCreationModel(
    var id: Long,
    var minecraftId: UUID? = null
) : CreationModel {
    fun toJson(): String {
        return MoshiService.moshi.adapter(DiscordUserCreationModel::class.java).toJson(this)
    }
}