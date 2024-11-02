package net.dungeonhub.model.carry_tier

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.CreationModel

class CarryTierCreationModel(
    var identifier: String,
    var displayName: String,
    var category: Long? = null,
    var priceChannel: Long? = null,
    var descriptiveName: String? = null,
    var thumbnailUrl: String? = null,
    var priceTitle: String? = null,
    var priceDescription: String? = null,
) : CreationModel {
    fun toJson(): String {
        return MoshiService.moshi.adapter(CarryTierCreationModel::class.java).toJson(this)
    }
}