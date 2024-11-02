package net.dungeonhub.model.score

import net.dungeonhub.enums.ScoreType
import net.dungeonhub.model.carry_type.CarryTypeModel
import net.dungeonhub.model.discord_user.DiscordUserModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.Model

class ScoreModel(
    val carrier: DiscordUserModel,
    val carryType: CarryTypeModel?,
    val scoreType: ScoreType,
    val scoreAmount: Long?
) : Model {
    companion object {
        fun fromJson(json: String): ScoreModel {
            return MoshiService.moshi.adapter(ScoreModel::class.java).fromJson(json)!!
        }
    }
}