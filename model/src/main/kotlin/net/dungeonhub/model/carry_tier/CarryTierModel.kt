package net.dungeonhub.model.carry_tier

import net.dungeonhub.entity.model.Model
import net.dungeonhub.model.carry_type.CarryTypeModel
import net.dungeonhub.service.MoshiService
import org.jetbrains.annotations.NotNull

class CarryTierModel(
    val id: Long,
    val identifier: String,
    var displayName: String,
    val carryType: CarryTypeModel,
    category: Long?,
    priceChannel: Long?,
    descriptiveName: String?,
    thumbnailUrl: String?,
    priceTitle: String?,
    priceDescription: String?
) : Model {
    var descriptiveName = descriptiveName
        @NotNull
        get() = (if(!field.isNullOrBlank()) field else null) ?: displayName

    var category = category
        get() = (if(field != null && field!! > 0L) field else null)

    val thumbnailUrl = thumbnailUrl
        get() = if(!field.isNullOrBlank()) field else null

    var priceTitle = priceTitle
        @NotNull
        get() = (if(!field.isNullOrBlank()) field else null) ?: descriptiveName

    var priceDescription = priceDescription
        get() = if(!field.isNullOrBlank()) field else null

    var priceChannel = priceChannel
        get() = if(field != null && field!! > 0L) field else null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CarryTierModel

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    fun toJson(): String {
        return MoshiService.moshi.adapter(CarryTierModel::class.java).toJson(this)
    }

    companion object {
        fun fromJson(json: String): CarryTierModel {
            return MoshiService.moshi.adapter(CarryTierModel::class.java).fromJson(json)!!
        }
    }
}