package net.dungeonhub.model.score

import net.dungeonhub.model.carry.CarryModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.Model

class LoggedCarryModel(val carryModel: CarryModel, val scoreModels: List<ScoreModel>) : Model {
    companion object {
        fun fromJson(json: String): LoggedCarryModel {
            return MoshiService.moshi.adapter(LoggedCarryModel::class.java).fromJson(json)!!
        }
    }
}