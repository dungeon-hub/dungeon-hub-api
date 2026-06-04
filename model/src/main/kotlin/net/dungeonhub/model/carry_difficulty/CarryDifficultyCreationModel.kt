package net.dungeonhub.model.carry_difficulty

import net.dungeonhub.enums.IngameCarryType
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.CreationModel

class CarryDifficultyCreationModel(
    var identifier: String,
    var displayName: String,
    var thumbnailUrl: String? = null,
    var bulkPrice: Int? = null,
    var bulkAmount: Int? = null,
    var priceName: String? = null,
    var price: Int,
    var score: Int,
    var ingameCarryType: IngameCarryType? = null
) : CreationModel {
    fun toJson(): String {
        return MoshiService.moshi.adapter(CarryDifficultyCreationModel::class.java).toJson(this)
    }
}