package net.dungeonhub.model.ticket

import net.dungeonhub.enums.TicketState
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateModel

class TicketUpdateModel(
    var state: TicketState?,
    var channel: Long?,
    claimer: Long?
): UpdateModel<TicketModel> {
    var claimer: Long? = claimer
        set(value) {
            resetClaimer = value == null
            field = value
        }
    var resetClaimer = false
        private set

    fun toJson(): String {
        return MoshiService.moshi.adapter(TicketUpdateModel::class.java).toJson(this)
    }
}