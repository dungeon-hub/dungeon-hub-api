package net.dungeonhub.model.ticket_panel

import dev.kord.common.entity.Permissions
import net.dungeonhub.model.discord_channel.DiscordChannelModel
import net.dungeonhub.model.discord_role.DiscordRoleModel
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateableModel

//TODO settings about when a transcript should be dmed / logged?
//TODO panel settings
//TODO buttons settings
//TODO message settings
//TODO setting: DM a message to the user once a ticket is closed / created?
//TODO add a ticket form
class TicketPanelModel(
    val id: Long,
    val name: String,
    val discordServer: DiscordServerModel,
    val closeable: Boolean,
    val closeConfirmation: Boolean,
    val openChannelName: String,
    val claimedChannelName: String,
    val closedChannelName: String,
    val transcriptChannel: DiscordChannelModel,

    val supportRoles: List<DiscordRoleModel>,
    val additionalRoles: List<DiscordRoleModel>,
    val categories: List<DiscordChannelModel>, //TODO should this be separated?
    val supportTeamPermissions: Permissions,
    val additionalRolesPermissions: Permissions,
    val ticketCreatorPermissions: Permissions,
    val ticketClaimerPermissions: Permissions,
    val everyonePermissions: Permissions,
): UpdateableModel<TicketPanelUpdateModel, TicketPanelModel> {
    companion object {
        fun fromJson(json: String): TicketPanelModel {
            return MoshiService.moshi.adapter(TicketPanelModel::class.java).fromJson(json)!!
        }
    }

    override fun getUpdateModel(): TicketPanelUpdateModel {
        return TicketPanelUpdateModel(null)
    }
}