package net.dungeonhub.model.ticket_panel

import net.dungeonhub.enums.DiscordChannelPermission
import net.dungeonhub.enums.TicketAction
import net.dungeonhub.enums.TicketPermissionCandidate
import net.dungeonhub.enums.TicketPhase
import net.dungeonhub.exceptions.InvalidUpdateException

/**
 * The permissions of a ticket panel, separately for open and closed tickets.
 *
 * - An action is allowed if any candidate the member matches grants it in the ticket's current phase.
 *   `false` only means that this candidate doesn't grant it.
 *   Administrators and the panel's `closeable` / `claimable` switches are handled outside of this model.
 * - Channel permissions are applied as Discord permission overwrites.
 *   While a ticket has a claimer, the [TicketPermissionCandidate.SupportTeam] overwrite isn't applied.
 * - Missing phases, candidates and actions fall back to [DEFAULT].
 *   The channel permissions of a given candidate are used as they are, so a missing key isn't overwritten.
 */
data class TicketPanelPermissionsModel(
    val open: Map<TicketPermissionCandidate, TicketCandidatePermissionsModel> = emptyMap(),
    val closed: Map<TicketPermissionCandidate, TicketCandidatePermissionsModel> = emptyMap()
) {
    fun forPhase(phase: TicketPhase): Map<TicketPermissionCandidate, TicketCandidatePermissionsModel> = when (phase) {
        TicketPhase.Open -> open
        TicketPhase.Closed -> closed
    }

    fun isAllowed(phase: TicketPhase, candidates: Set<TicketPermissionCandidate>, action: TicketAction): Boolean {
        if (phase !in action.phases) return false

        return candidates.any { candidate ->
            forPhase(phase)[candidate]?.actions?.get(action)
                ?: DEFAULT.forPhase(phase).getValue(candidate).actions.getValue(action)
        }
    }

    fun channelPermissions(phase: TicketPhase, candidate: TicketPermissionCandidate): Map<DiscordChannelPermission, Boolean> {
        return forPhase(phase)[candidate]?.channel ?: DEFAULT.forPhase(phase).getValue(candidate).channel
    }

    /**
     * Validates the permissions and fills in the defaults for everything that's missing.
     * Actions that don't apply to a phase are dropped.
     *
     * @throws InvalidUpdateException if an action is `null` or a key is unknown
     */
    fun resolved(): TicketPanelPermissionsModel {
        return TicketPanelPermissionsModel(
            resolvePhase(TicketPhase.Open, open),
            resolvePhase(TicketPhase.Closed, closed)
        )
    }

    companion object {
        private val standardChannelPermissions = mapOf(
            DiscordChannelPermission.ViewChannel to true,
            DiscordChannelPermission.SendMessages to true,
            DiscordChannelPermission.ReadMessageHistory to true
        )

        private val hiddenChannelPermissions = mapOf(DiscordChannelPermission.ViewChannel to false)

        val DEFAULT = TicketPanelPermissionsModel(
            open = mapOf(
                TicketPermissionCandidate.SupportTeam to defaultPermissions(
                    TicketPhase.Open, standardChannelPermissions,
                    TicketAction.Close, TicketAction.Claim, TicketAction.Delete, TicketAction.AddUser, TicketAction.GenerateTranscript
                ),
                TicketPermissionCandidate.AdditionalRoles to defaultPermissions(
                    TicketPhase.Open, standardChannelPermissions,
                    TicketAction.Close, TicketAction.Claim, TicketAction.Delete, TicketAction.AddUser, TicketAction.GenerateTranscript
                ),
                TicketPermissionCandidate.TicketCreator to defaultPermissions(
                    TicketPhase.Open, standardChannelPermissions,
                    TicketAction.Close, TicketAction.Delete, TicketAction.AddUser, TicketAction.GenerateTranscript
                ),
                TicketPermissionCandidate.TicketClaimer to defaultPermissions(
                    TicketPhase.Open, standardChannelPermissions,
                    TicketAction.Unclaim
                ),
                TicketPermissionCandidate.Everyone to defaultPermissions(TicketPhase.Open, hiddenChannelPermissions)
            ),
            closed = mapOf(
                TicketPermissionCandidate.SupportTeam to defaultPermissions(
                    TicketPhase.Closed, standardChannelPermissions,
                    TicketAction.Reopen, TicketAction.Delete, TicketAction.GenerateTranscript
                ),
                TicketPermissionCandidate.AdditionalRoles to defaultPermissions(
                    TicketPhase.Closed, standardChannelPermissions,
                    TicketAction.Reopen, TicketAction.Delete, TicketAction.GenerateTranscript
                ),
                TicketPermissionCandidate.TicketCreator to defaultPermissions(
                    TicketPhase.Closed, emptyMap(),
                    TicketAction.Reopen, TicketAction.Delete, TicketAction.GenerateTranscript
                ),
                TicketPermissionCandidate.TicketClaimer to defaultPermissions(TicketPhase.Closed, standardChannelPermissions),
                TicketPermissionCandidate.Everyone to defaultPermissions(TicketPhase.Closed, hiddenChannelPermissions)
            )
        )

        private fun defaultPermissions(
            phase: TicketPhase,
            channel: Map<DiscordChannelPermission, Boolean>,
            vararg allowedActions: TicketAction
        ): TicketCandidatePermissionsModel {
            return TicketCandidatePermissionsModel(
                TicketAction.forPhase(phase).associateWith { it in allowedActions },
                channel
            )
        }

        // the parameters are nullable, since Gson ignores Kotlin's nullability when reading request bodies
        private fun resolvePhase(
            phase: TicketPhase,
            permissions: Map<out TicketPermissionCandidate?, TicketCandidatePermissionsModel?>?
        ): Map<TicketPermissionCandidate, TicketCandidatePermissionsModel> {
            val givenPermissions = (permissions ?: emptyMap()).entries.associate { (candidate, candidatePermissions) ->
                (candidate ?: throw InvalidUpdateException("Unknown ticket permission candidate in phase $phase.")) to candidatePermissions
            }

            return TicketPermissionCandidate.entries.associateWith { candidate ->
                val defaultPermissions = DEFAULT.forPhase(phase).getValue(candidate)

                givenPermissions[candidate]?.let { resolveCandidate(phase, candidate, it, defaultPermissions) }
                    ?: defaultPermissions
            }
        }

        private fun resolveCandidate(
            phase: TicketPhase,
            candidate: TicketPermissionCandidate,
            permissions: TicketCandidatePermissionsModel,
            defaultPermissions: TicketCandidatePermissionsModel
        ): TicketCandidatePermissionsModel {
            val actions: Map<out TicketAction?, Boolean?>? = permissions.actions
            val channel: Map<out DiscordChannelPermission?, Boolean?>? = permissions.channel

            val givenActions = (actions ?: emptyMap()).entries.associate { (action, allowed) ->
                if (action == null) {
                    throw InvalidUpdateException("Unknown ticket action for $candidate in phase $phase.")
                }

                if (allowed == null) {
                    throw InvalidUpdateException("The ticket action $action for $candidate in phase $phase can't be null.")
                }

                action to allowed
            }

            val givenChannel = (channel ?: emptyMap()).entries.mapNotNull { (permission, value) ->
                if (permission == null) {
                    throw InvalidUpdateException("Unknown Discord channel permission for $candidate in phase $phase.")
                }

                value?.let { permission to it }
            }.toMap()

            return TicketCandidatePermissionsModel(
                TicketAction.forPhase(phase).associateWith { givenActions[it] ?: defaultPermissions.actions.getValue(it) },
                DiscordChannelPermission.entries.filter { it in givenChannel }.associateWith { givenChannel.getValue(it) }
            )
        }
    }
}
