package net.dungeonhub.model.ticket

import net.dungeonhub.enums.TicketState
import net.dungeonhub.model.discord_channel.DiscordChannelModel
import net.dungeonhub.model.discord_user.DiscordUserModel
import net.dungeonhub.model.ticket_panel.TicketPanelModel
import net.dungeonhub.structure.model.UpdateableModel
import java.time.Instant

class TicketModel(
    val id: Long,
    val state: TicketState,
    val channel: DiscordChannelModel?,
    val ticketPanel: TicketPanelModel,
    val user: DiscordUserModel,
    val claimer: DiscordUserModel?, // TODO add additionalClaimers property --> needed to load tickets for the mod
    val created: Instant,
    val formResponses: List<TicketFormResponseModel>
): UpdateableModel<TicketUpdateModel, TicketModel> {
    override fun getUpdateModel(): TicketUpdateModel {
        return TicketUpdateModel(null, null, null)
    }
}