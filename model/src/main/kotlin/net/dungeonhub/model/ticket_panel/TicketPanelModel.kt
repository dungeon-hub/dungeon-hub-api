package net.dungeonhub.model.ticket_panel

import dev.kord.common.entity.Permissions
import net.dungeonhub.enums.TicketPermissionCandidate
import net.dungeonhub.enums.TicketPermissionType
import net.dungeonhub.enums.TranscriptTarget
import net.dungeonhub.model.discord_channel.DiscordChannelModel
import net.dungeonhub.model.discord_role.DiscordRoleModel
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateableModel

class TicketPanelModel(
    val id: Long,
    val name: String,
    val displayName: String?,
    val emoji: String?,
    val discordServer: DiscordServerModel,

    // ticket settings
    val closeable: Boolean,
    val closeConfirmation: Boolean,
    val claimable: Boolean,
    val openChannelName: String?,
    val claimedChannelName: String?,
    val closedChannelName: String?,
    val transcriptChannel: DiscordChannelModel?,
    val ticketMessage: String?,
    val requiresLinking: Boolean,
    val closeTranscriptTarget: TranscriptTarget,
    val deleteTranscriptTarget: TranscriptTarget,
    val userTranscriptDm: String?,
    val formQuestions: List<TicketPanelFormModel>,

    // role and permission stuff
    val supportRoles: List<DiscordRoleModel>,
    val additionalRoles: List<DiscordRoleModel>,
    val openCategories: List<Long>,
    val closedCategories: List<Long>,
    val permissions: Map<TicketPermissionCandidate, Map<TicketPermissionType, Permissions>>
): UpdateableModel<TicketPanelUpdateModel, TicketPanelModel> {
    companion object {
        fun fromJson(json: String): TicketPanelModel {
            return MoshiService.moshi.adapter(TicketPanelModel::class.java).fromJson(json)!!
        }
    }

    override fun getUpdateModel(): TicketPanelUpdateModel {
        return TicketPanelUpdateModel(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
    }
}