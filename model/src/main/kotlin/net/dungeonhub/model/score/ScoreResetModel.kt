package net.dungeonhub.model.score

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.Model

class ScoreResetModel(
    val defaultCount: Long,
    val eventCount: Long
) : Model {
    companion object {
        fun fromJson(json: String): ScoreResetModel {
            return MoshiService.moshi.adapter(ScoreResetModel::class.java).fromJson(json)!!
        }
    }
}