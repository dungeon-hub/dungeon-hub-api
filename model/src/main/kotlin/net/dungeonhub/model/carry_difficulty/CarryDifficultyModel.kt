package net.dungeonhub.model.carry_difficulty

import net.dungeonhub.entity.model.Model
import net.dungeonhub.model.carry_tier.CarryTierModel
import net.dungeonhub.model.carry_type.CarryTypeModel
import net.dungeonhub.service.MoshiService
import kotlin.math.max

class CarryDifficultyModel(
    val id: Long,
    val identifier: String,
    val displayName: String,
    val carryTier: CarryTierModel,
    val price: Int,
    bulkPrice: Int?,
    bulkAmount: Int?,
    score: Int,
    thumbnailUrl: String?,
    priceName: String?
) : Model {
    val bulkPrice = bulkPrice
        get() = if (field != null && field > 0) field else null

    val bulkAmount = bulkAmount
        get() = if (field != null && field > 0) field else null

    val score = score
        get() = max(field, 0)

    val thumbnailUrl: String? = thumbnailUrl
        //TODO
        get() = field //field ?: carryTier.thumbnailUrl

    val priceName = priceName
        get() = (if(!field.isNullOrBlank()) field else null) ?: displayName

    val carryType: CarryTypeModel
        //TODO
        get() = CarryTypeModel() //carryTier.carryType

    fun toJson(): String {
        return MoshiService.moshi.adapter(CarryDifficultyModel::class.java).toJson(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CarryDifficultyModel

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        fun fromJson(json: String): CarryDifficultyModel {
            return MoshiService.moshi.adapter(CarryDifficultyModel::class.java).fromJson(json)!!
        }
    }
}
