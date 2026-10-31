package net.dungeonhub.model.carry_difficulty

import net.dungeonhub.enums.IngameCarryType
import net.dungeonhub.model.carry_tier.CarryTierModel
import net.dungeonhub.model.carry_type.CarryTypeModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateableModel
import kotlin.math.max

class CarryDifficultyModel(
    val id: Long,
    val identifier: String,
    val displayName: String,
    val carryTier: CarryTierModel,
    val price: Int,
    bulkPrices: List<BulkPriceModel> = emptyList(),
    score: Int,
    thumbnailUrl: String?,
    priceName: String?,
    val ingameCarryType: IngameCarryType?
) : UpdateableModel<CarryDifficultyUpdateModel, CarryDifficultyModel> {
    val bulkPrices: List<BulkPriceModel> = bulkPrices
        get() = field
            .filter { it.amount > 0 && it.price > 0 }
            .sortedBy { it.amount }

    val score = score
        get() = max(field, 0)

    val thumbnailUrl: String? = thumbnailUrl
        get() = field ?: carryTier.thumbnailUrl

    val priceName = priceName
        get() = (if (!field.isNullOrBlank()) field else null) ?: displayName

    val carryType: CarryTypeModel
        get() = carryTier.carryType

    fun calculatePricePerCarry(amount: Int): Long = calculatePricePerCarry(amount, bulkPrices, price)

    fun calculateTotalPrice(amount: Int): Long = calculateTotalPrice(amount, bulkPrices, price)

    fun toJson(): String {
        return MoshiService.moshi.adapter(CarryDifficultyModel::class.java).toJson(this)
    }

    override fun getUpdateModel(): CarryDifficultyUpdateModel {
        return CarryDifficultyUpdateModel(null, null, null, null, null, null, null)
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

        /**
         * Uses the bulk price with the highest amount that is reached by [amount], or [price] if none applies.
         */
        fun calculatePricePerCarry(amount: Long, bulkPrices: List<BulkPriceModel>, price: Int): Long {
            val bulkPrice = bulkPrices
                .filter { it.amount > 0 && it.price > 0 && it.amount <= amount }
                .maxByOrNull { it.amount }

            return (bulkPrice?.price ?: price).toLong()
        }

        fun calculatePricePerCarry(amount: Int, bulkPrices: List<BulkPriceModel>, price: Int): Long {
            return calculatePricePerCarry(amount.toLong(), bulkPrices, price)
        }

        fun calculateTotalPrice(amount: Int, bulkPrices: List<BulkPriceModel>, price: Int): Long {
            return calculatePricePerCarry(amount, bulkPrices, price) * amount
        }
    }
}
