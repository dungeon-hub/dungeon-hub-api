package net.dungeonhub.model.discord_user

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateModel
import java.util.*

class DiscordUserUpdateModel(
    minecraftId: UUID?
) : UpdateModel<DiscordUserModel> {
    var minecraftId = minecraftId
        set(value) {
            field = value
            removeMinecraftId = value == null
        }

    var removeMinecraftId = false
        private set

    /*override fun apply(model: DiscordUserModel): DiscordUserModel {
        if (removeMinecraftId) {
            model.setMinecraftId(null)
        }

        if (minecraftId != null) {
            model.setMinecraftId(minecraftId)
        }

        return model
    }*/

    fun toJson(): String {
        return MoshiService.moshi.adapter(DiscordUserUpdateModel::class.java).toJson(this)
    }
}