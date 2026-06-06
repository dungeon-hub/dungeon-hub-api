package net.dungeonhub.model.ticket_panel

import dev.kord.common.entity.Permissions
import net.dungeonhub.enums.TicketPermissionCandidate
import net.dungeonhub.enums.TicketPermissionType
import net.dungeonhub.enums.TranscriptTarget
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.CreationModel

class TicketPanelCreationModel(
    val name: String,
    val displayName: String?,
    val emoji: String?,

    // ticket settings
    val closeable: Boolean,
    val closeConfirmation: Boolean,
    val claimable: Boolean,
    val openChannelName: String?,
    val claimedChannelName: String?,
    val closedChannelName: String?,
    val transcriptChannel: Long?,
    val ticketMessage: String?,
    val requiresLinking: Boolean,
    val closeTranscriptTarget: TranscriptTarget?,
    val deleteTranscriptTarget: TranscriptTarget?,
    val userTranscriptDm: String?,
    val formQuestions: List<TicketPanelFormModel>?,

    val relatedCarryTier: Long?,
    val relatedCarryDifficulty: Long?,

    // role and permission stuff
    val supportRoles: List<Long>?,
    val additionalRoles: List<Long>?,
    val openCategories: List<Long>?,
    val closedCategories: List<Long>?,
    val permissions: Map<TicketPermissionCandidate, Map<TicketPermissionType, Permissions>>?
) : CreationModel {
    fun toJson(): String {
        return MoshiService.moshi.adapter(TicketPanelCreationModel::class.java).toJson(this)
    }
}