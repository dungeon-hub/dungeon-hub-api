package net.dungeonhub.model.carry_type

import net.dungeonhub.entity.model.Model
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.service.MoshiService

class CarryTypeModel(
    val id: Long,
    val identifier: String,
    var displayName: String,
    var server: DiscordServerModel,
    logChannel: Long?,
    leaderboardChannel: Long?,
    isEventActive: Boolean?
) : Model {
    var isEventActive = isEventActive
        get() = java.lang.Boolean.TRUE == field

    var logChannel = logChannel
        get() = (if (field != null && field!! > 0L) field else null)

    var leaderboardChannel = leaderboardChannel
        get() = (if (field != null && field!! > 0L) field else null)

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