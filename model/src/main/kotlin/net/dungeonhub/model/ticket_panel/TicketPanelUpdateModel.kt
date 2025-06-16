package net.dungeonhub.model.ticket_panel

import dev.kord.common.entity.Permissions
import net.dungeonhub.model.discord_channel.DiscordChannelModel
import net.dungeonhub.model.discord_role.DiscordRoleModel
import net.dungeonhub.structure.model.UpdateModel

class TicketPanelUpdateModel(
    val name: String?,
    val closeable: Boolean?,
    val closeConfirmation: Boolean?,
    val openChannelName: String?,
    val claimedChannelName: String?,
    val closedChannelName: String?,
    val transcriptChannel: DiscordChannelModel?,

    //TODO do those need to be changed?
    val supportRoles: List<DiscordRoleModel>?,
    val additionalRoles: List<DiscordRoleModel>?,
    val categories: List<DiscordChannelModel>?, //TODO should this be separated?
    val supportTeamPermissions: Permissions?,
    val additionalRolesPermissions: Permissions?,
    val ticketCreatorPermissions: Permissions?,
    val everyonePermissions: Permissions?,
): UpdateModel<TicketPanelModel>