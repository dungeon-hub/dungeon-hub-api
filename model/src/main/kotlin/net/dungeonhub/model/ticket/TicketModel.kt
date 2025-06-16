package net.dungeonhub.model.ticket

import kotlinx.datetime.Instant
import net.dungeonhub.model.discord_channel.DiscordChannelModel
import net.dungeonhub.model.discord_user.DiscordUserModel
import net.dungeonhub.model.ticket_panel.TicketPanelModel

class TicketModel(
    val id: Long,
    val state: Int, //TODO enum -> open, closed, deleted
    val channel: DiscordChannelModel,
    val ticketPanel: TicketPanelModel,
    val user: DiscordUserModel,
    val claimed: DiscordUserModel?,
    val created: Instant
)