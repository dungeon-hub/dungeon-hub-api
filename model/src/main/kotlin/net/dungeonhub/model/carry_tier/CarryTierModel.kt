package net.dungeonhub.model.carry_tier

import net.dungeonhub.structure.model.Model
import net.dungeonhub.model.carry_type.CarryTypeModel
import net.dungeonhub.service.MoshiService
import org.jetbrains.annotations.NotNull

class CarryTierModel(
    val id: Long,
    val identifier: String,
    val displayName: String,
    val carryType: CarryTypeModel,
    category: Long?,
    priceChannel: Long?,
    descriptiveName: String?,
    thumbnailUrl: String?,
    priceTitle: String?,
    priceDescription: String?
) : Model {
    val descriptiveName = descriptiveName
        @NotNull
        get() = (if(!field.isNullOrBlank()) field else null) ?: displayName

    val category = category
        get() = (if(field != null && field > 0L) field else null)

    val thumbnailUrl = thumbnailUrl
        get() = if(!field.isNullOrBlank()) field else null

    val priceTitle = priceTitle
        @NotNull
        get() = (if(!field.isNullOrBlank()) field else null) ?: descriptiveName

    val priceDescription = priceDescription
        get() = if(!field.isNullOrBlank()) field else null

    val priceChannel = priceChannel
        get() = if(field != null && field > 0L) field else null

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