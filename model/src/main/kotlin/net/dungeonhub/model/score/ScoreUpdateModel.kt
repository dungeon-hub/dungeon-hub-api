package net.dungeonhub.model.score

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateModel

class ScoreUpdateModel(
    val id: Long,
    var amount: Long
) : UpdateModel<ScoreModel> {
    fun toJson(): String {
        return MoshiService.moshi.adapter(ScoreUpdateModel::class.java).toJson(this)
    }
}