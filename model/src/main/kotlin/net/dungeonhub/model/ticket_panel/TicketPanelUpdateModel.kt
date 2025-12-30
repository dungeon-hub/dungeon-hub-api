package net.dungeonhub.model.ticket_panel

import dev.kord.common.entity.Permissions
import net.dungeonhub.enums.TicketPermissionCandidate
import net.dungeonhub.enums.TicketPermissionType
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateModel

class TicketPanelUpdateModel(
    var name: String?,
    var displayName: String?,
    var emoji: String?,
    var closeable: Boolean?,
    var closeConfirmation: Boolean?,
    val claimable: Boolean?,
    var openChannelName: String?,
    var claimedChannelName: String?,
    var closedChannelName: String?,
    var transcriptChannel: Long?,

    //TODO do those need to be changed?
    var supportRoles: List<Long>?,
    var additionalRoles: List<Long>?,
    var openCategories: List<Long>?,
    var closedCategories: List<Long>?,
    var permissions: Map<TicketPermissionCandidate, Map<TicketPermissionType, Permissions>>?
): UpdateModel<TicketPanelModel> {
    fun toJson(): String {
        return MoshiService.moshi.adapter(TicketPanelUpdateModel::class.java).toJson(this)
    }
}