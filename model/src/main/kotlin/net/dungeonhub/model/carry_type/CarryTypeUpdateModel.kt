package net.dungeonhub.model.carry_type

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateModel

class CarryTypeUpdateModel(
    var displayName: String?,
    logChannel: Long?,
    leaderboardChannel: Long?,
    eventActive: Boolean?
) : UpdateModel<CarryTypeModel> {
    var logChannel = logChannel
        set(value) {
            field = value
            resetLogChannel = value == null
        }

    var leaderboardChannel = leaderboardChannel
        set(value) {
            field = value
            resetLeaderboardChannel = value == null
        }

    var eventActive = eventActive
        set(value) {
            field = value
            resetEventActive = value == null
        }

    var resetLogChannel = false
        private set
    var resetLeaderboardChannel = false
        private set
    var resetEventActive = false
        private set

    fun toJson(): String {
        return MoshiService.moshi.adapter(CarryTypeUpdateModel::class.java).toJson(this)
    }
}