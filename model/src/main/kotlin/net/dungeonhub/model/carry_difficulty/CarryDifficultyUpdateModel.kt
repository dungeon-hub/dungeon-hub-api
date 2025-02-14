package net.dungeonhub.model.carry_difficulty

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateModel

class CarryDifficultyUpdateModel(
    var displayName: String?,
    thumbnailUrl: String?,
    bulkPrice: Int?,
    bulkAmount: Int?,
    priceName: String?,
    var price: Int?,
    var score: Int?
) : UpdateModel<CarryDifficultyModel> {
    var thumbnailUrl = thumbnailUrl
        set(value) {
            field = value
            resetThumbnailUrl = value == null
        }

    var bulkPrice = bulkPrice
        set(value) {
            field = value
            resetBulkPrice = value == null
        }

    var bulkAmount = bulkAmount
        set(value) {
            field = value
            resetBulkAmount = value == null
        }

    var priceName = priceName
        set(value) {
            field = value
            resetPriceName = value == null
        }

    var resetThumbnailUrl = false
        private set
    var resetBulkPrice = false
        private set
    var resetBulkAmount = false
        private set
    var resetPriceName = false
        private set

    fun toJson(): String {
        return MoshiService.moshi.adapter(CarryDifficultyUpdateModel::class.java).toJson(this)
    }

    companion object {
        fun fromJson(json: String): CarryDifficultyUpdateModel {
            return MoshiService.moshi.adapter(CarryDifficultyUpdateModel::class.java).fromJson(json)!!
        }
    }
}