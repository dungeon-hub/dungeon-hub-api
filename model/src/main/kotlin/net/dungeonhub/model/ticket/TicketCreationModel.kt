package net.dungeonhub.model.ticket

import net.dungeonhub.enums.TicketState
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.CreationModel

class TicketCreationModel(
    val state: TicketState,
    val channel: Long?,
    val user: Long,
    val claimer: Long?
) : CreationModel {
    fun toJson(): String {
        return MoshiService.moshi.adapter(TicketCreationModel::class.java).toJson(this)
    }
}