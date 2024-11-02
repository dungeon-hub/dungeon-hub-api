package net.dungeonhub.model.carry_type

import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateableModel

class CarryTypeModel(
    val id: Long,
    val identifier: String,
    val displayName: String,
    val server: DiscordServerModel,
    logChannel: Long?,
    leaderboardChannel: Long?,
    isEventActive: Boolean?
) : UpdateableModel<CarryTypeUpdateModel, CarryTypeModel> {
    val isEventActive = isEventActive
        get() = java.lang.Boolean.TRUE == field

    val logChannel = logChannel
        get() = (if (field != null && field > 0L) field else null)

    val leaderboardChannel = leaderboardChannel
        get() = (if (field != null && field > 0L) field else null)

    override fun getUpdateModel(): CarryTypeUpdateModel {
        return CarryTypeUpdateModel(null, null, null, null)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CarryTypeModel

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    fun toJson(): String {
        return MoshiService.moshi.adapter(CarryTypeModel::class.java).toJson(this)
    }

    companion object {
        fun fromJson(json: String): CarryTypeModel {
            return MoshiService.moshi.adapter(CarryTypeModel::class.java).fromJson(json)!!
        }
    }
}