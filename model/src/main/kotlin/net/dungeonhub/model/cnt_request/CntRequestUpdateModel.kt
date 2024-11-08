package net.dungeonhub.model.cnt_request

import net.dungeonhub.model.discord_user.DiscordUserModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateModel

class CntRequestUpdateModel(
    claimer: DiscordUserModel?,
    var coinValue: String?,
    var description: String?,
    var requirement: String?,
    var completed: Boolean?
) : UpdateModel<CntRequestModel> {
    var claimer = claimer
        set(value) {
            resetClaimer = value == null
            field = value
        }

    var resetClaimer = false
        private set

    fun toJson(): String {
        return MoshiService.moshi.adapter(CntRequestUpdateModel::class.java).toJson(this)
    }
}