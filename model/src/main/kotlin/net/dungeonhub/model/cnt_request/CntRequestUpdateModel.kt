package net.dungeonhub.model.cnt_request

import net.dungeonhub.model.discord_user.DiscordUserModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateModel

class CntRequestUpdateModel(
    claimer: DiscordUserModel?,
    var coinValue: String?,
    var description: String?,
    var requirement: String?
) : UpdateModel<CntRequestModel> {
    var claimer = claimer
        set(value) {
            resetClaimer = value == null
            field = value
        }

    var resetClaimer = false
        private set
    /*override fun apply(model: CntRequestModel): CntRequestModel {
        if (removeClaimer) {
            model.setClaimer(null)
        }

        if (claimer != null) {
            model.setClaimer(claimer)
        }

        if (coinValue != null) {
            model.setCoinValue(coinValue)
        }

        if (description != null) {
            model.setDescription(description)
        }

        if (requirement != null) {
            model.setRequirement(requirement)
        }

        return model
    }*/

    fun toJson(): String {
        return MoshiService.moshi.adapter(CntRequestUpdateModel::class.java).toJson(this)
    }
}