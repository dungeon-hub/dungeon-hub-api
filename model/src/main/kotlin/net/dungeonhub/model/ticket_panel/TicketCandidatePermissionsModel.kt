package net.dungeonhub.model.ticket_panel

import net.dungeonhub.enums.DiscordChannelPermission
import net.dungeonhub.enums.TicketAction

/**
 * The permissions of one [net.dungeonhub.enums.TicketPermissionCandidate] in one ticket phase.
 *
 * @property actions whether the candidate grants an action. A resolved model contains every action of the phase.
 * @property channel the Discord overwrite per permission: `true` allows, `false` denies, a missing key isn't overwritten.
 */
data class TicketCandidatePermissionsModel(
    val actions: Map<TicketAction, Boolean> = emptyMap(),
    val channel: Map<DiscordChannelPermission, Boolean> = emptyMap()
)
