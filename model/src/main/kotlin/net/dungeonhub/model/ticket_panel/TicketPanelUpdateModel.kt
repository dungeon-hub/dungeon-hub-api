package net.dungeonhub.model.ticket_panel

import dev.kord.common.entity.Permissions
import net.dungeonhub.enums.TicketPermissionCandidate
import net.dungeonhub.enums.TicketPermissionType
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateModel

// TODO support nullable fields
class TicketPanelUpdateModel(
    var name: String?,
    displayName: String?,
    emoji: String?,
    var closeable: Boolean?,
    var closeConfirmation: Boolean?,
    val claimable: Boolean?,
    openChannelName: String?,
    claimedChannelName: String?,
    closedChannelName: String?,
    transcriptChannel: Long?,
    ticketMessage: String?,
    var requiresLinking: Boolean?,

    //TODO do those need to be changed?
    var supportRoles: List<Long>?,
    var additionalRoles: List<Long>?,
    var openCategories: List<Long>?,
    var closedCategories: List<Long>?,
    var permissions: Map<TicketPermissionCandidate, Map<TicketPermissionType, Permissions>>?
): UpdateModel<TicketPanelModel> {
    var displayName = displayName
        set(value) {
            resetDisplayName = value == null
            field = value
        }
    var resetDisplayName = false
        private set

    var emoji = emoji
        set(value) {
            resetEmoji = value == null
            field = value
        }
    var resetEmoji = false

    var openChannelName = openChannelName
        set(value) {
            resetOpenChannelName = value == null
            field = value
        }
    var resetOpenChannelName = false
        private set

    var claimedChannelName = claimedChannelName
        set(value) {
            resetClaimedChannelName = value == null
            field = value
        }
    var resetClaimedChannelName = false

    var closedChannelName = closedChannelName
        set(value) {
            resetClosedChannelName = value == null
            field = value
        }
    var resetClosedChannelName = false
        private set

    var transcriptChannel = transcriptChannel
        set(value) {
            resetTranscriptChannel = value == null
            field = value
        }
    var resetTranscriptChannel = false
        private set

    var ticketMessage = ticketMessage
        set(value) {
            resetTicketMessage = value == null
            field = value
        }
    var resetTicketMessage = false
        private set

    fun toJson(): String {
        return MoshiService.moshi.adapter(TicketPanelUpdateModel::class.java).toJson(this)
    }
}