package net.dungeonhub.model.carry_tier

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateModel

class CarryTierUpdateModel(
    var displayName: String?,
    category: Long?,
    priceChannel: Long?,
    descriptiveName: String?,
    thumbnailUrl: String?,
    priceTitle: String?,
    priceDescription: String?
) : UpdateModel<CarryTierModel> {
    var category = category
        set(value) {
            field = value
            resetCategory = value == null
        }

    var priceChannel = priceChannel
        set(value) {
            field = value
            resetPriceChannel = value == null
        }

    var descriptiveName = descriptiveName
        set(value) {
            field = value
            resetDescriptiveName = value == null
        }

    var thumbnailUrl = thumbnailUrl
        set(value) {
            field = value
            resetThumbnailUrl = value == null
        }

    var priceTitle = priceTitle
        set(value) {
            field = value
            resetPriceTitle = value == null
        }

    var priceDescription = priceDescription
        set(value) {
            field = value
            resetPriceDescription = value == null
        }

    var resetCategory = false
        private set
    var resetPriceChannel = false
        private set
    var resetDescriptiveName = false
        private set
    var resetThumbnailUrl = false
        private set
    var resetPriceTitle = false
        private set
    var resetPriceDescription = false
        private set

    fun toJson(): String {
        return MoshiService.moshi.adapter(CarryTierUpdateModel::class.java).toJson(this)
    }

    companion object {
        fun fromJson(json: String): CarryTierUpdateModel {
            return MoshiService.moshi.adapter(CarryTierUpdateModel::class.java).fromJson(json)!!
        }
    }
}