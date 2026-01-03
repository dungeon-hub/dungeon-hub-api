package net.dungeonhub.model.ticket

import net.dungeonhub.enums.TicketState
import net.dungeonhub.model.discord_channel.DiscordChannelModel
import net.dungeonhub.model.discord_user.DiscordUserModel
import net.dungeonhub.model.ticket_panel.TicketPanelModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateableModel
import java.time.Instant

class TicketModel(
    val id: Long,
    val state: TicketState,
    val channel: DiscordChannelModel?,
    val ticketPanel: TicketPanelModel,
    val user: DiscordUserModel,
    val claimer: DiscordUserModel?,
    val created: Instant
): UpdateableModel<TicketUpdateModel, TicketModel> {
    companion object {
        fun fromJson(json: String): TicketModel {
            return MoshiService.moshi.adapter(TicketModel::class.java).fromJson(json)!!
        }
    }

    override fun getUpdateModel(): TicketUpdateModel {
        return TicketUpdateModel(null, null, null)
    }
}