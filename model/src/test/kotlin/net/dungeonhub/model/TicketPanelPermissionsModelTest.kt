package net.dungeonhub.model

import net.dungeonhub.enums.DiscordChannelPermission
import net.dungeonhub.enums.TicketAction
import net.dungeonhub.enums.TicketPermissionCandidate
import net.dungeonhub.enums.TicketPhase
import net.dungeonhub.enums.TicketState
import net.dungeonhub.model.ticket_panel.TicketCandidatePermissionsModel
import net.dungeonhub.model.ticket_panel.TicketPanelPermissionsModel
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TicketPanelPermissionsModelTest {
    private val standardChannelPermissions = mapOf(
        DiscordChannelPermission.ViewChannel to true,
        DiscordChannelPermission.SendMessages to true,
        DiscordChannelPermission.ReadMessageHistory to true
    )

    @Test
    fun testTicketPhase() {
        assertEquals(TicketPhase.Open, TicketPhase.of(TicketState.Creating))
        assertEquals(TicketPhase.Open, TicketPhase.of(TicketState.Open))
        assertEquals(TicketPhase.Closed, TicketPhase.of(TicketState.Closed))
        assertEquals(TicketPhase.Closed, TicketPhase.of(TicketState.Deleted))
    }

    @Test
    fun testDefaultsAreComplete() {
        val defaults = TicketPanelPermissionsModel.DEFAULT

        assertEquals(defaults, defaults.resolved())

        for (phase in TicketPhase.entries) {
            assertEquals(TicketPermissionCandidate.entries.toSet(), defaults.forPhase(phase).keys)

            for (candidatePermissions in defaults.forPhase(phase).values) {
                assertEquals(TicketAction.forPhase(phase).toSet(), candidatePermissions.actions.keys)
            }
        }
    }

    @Test
    fun testDefaultActions() {
        val defaults = TicketPanelPermissionsModel.DEFAULT

        val expectedOpen = mapOf(
            TicketPermissionCandidate.SupportTeam to setOf(TicketAction.Close, TicketAction.Claim, TicketAction.Delete, TicketAction.AddUser, TicketAction.GenerateTranscript),
            TicketPermissionCandidate.AdditionalRoles to setOf(TicketAction.Close, TicketAction.Claim, TicketAction.Delete, TicketAction.AddUser, TicketAction.GenerateTranscript),
            TicketPermissionCandidate.TicketCreator to setOf(TicketAction.Close, TicketAction.Delete, TicketAction.AddUser, TicketAction.GenerateTranscript),
            TicketPermissionCandidate.TicketClaimer to setOf(TicketAction.Unclaim),
            TicketPermissionCandidate.Everyone to emptySet()
        )

        val expectedClosed = mapOf(
            TicketPermissionCandidate.SupportTeam to setOf(TicketAction.Reopen, TicketAction.Delete, TicketAction.GenerateTranscript),
            TicketPermissionCandidate.AdditionalRoles to setOf(TicketAction.Reopen, TicketAction.Delete, TicketAction.GenerateTranscript),
            TicketPermissionCandidate.TicketCreator to setOf(TicketAction.Reopen, TicketAction.Delete, TicketAction.GenerateTranscript),
            TicketPermissionCandidate.TicketClaimer to emptySet(),
            TicketPermissionCandidate.Everyone to emptySet()
        )

        for ((phase, expected) in mapOf(TicketPhase.Open to expectedOpen, TicketPhase.Closed to expectedClosed)) {
            for ((candidate, allowedActions) in expected) {
                assertEquals(
                    allowedActions,
                    defaults.forPhase(phase).getValue(candidate).actions.filterValues { it }.keys,
                    "Default actions of $candidate in phase $phase"
                )
            }
        }
    }

    @Test
    fun testDefaultChannelPermissions() {
        val defaults = TicketPanelPermissionsModel.DEFAULT
        val hidden = mapOf(DiscordChannelPermission.ViewChannel to false)

        for (phase in TicketPhase.entries) {
            assertEquals(standardChannelPermissions, defaults.channelPermissions(phase, TicketPermissionCandidate.SupportTeam))
            assertEquals(standardChannelPermissions, defaults.channelPermissions(phase, TicketPermissionCandidate.AdditionalRoles))
            assertEquals(standardChannelPermissions, defaults.channelPermissions(phase, TicketPermissionCandidate.TicketClaimer))
            assertEquals(hidden, defaults.channelPermissions(phase, TicketPermissionCandidate.Everyone))
        }

        assertEquals(standardChannelPermissions, defaults.channelPermissions(TicketPhase.Open, TicketPermissionCandidate.TicketCreator))
        assertEquals(emptyMap(), defaults.channelPermissions(TicketPhase.Closed, TicketPermissionCandidate.TicketCreator))
    }

    @Test
    fun testEmptyPermissionsResolveToDefaults() {
        assertEquals(TicketPanelPermissionsModel.DEFAULT, TicketPanelPermissionsModel().resolved())
    }

    @Test
    fun testOmittedCandidateUsesDefaults() {
        val permissions = TicketPanelPermissionsModel(
            open = mapOf(
                TicketPermissionCandidate.Everyone to TicketCandidatePermissionsModel(
                    channel = mapOf(DiscordChannelPermission.ViewChannel to true)
                )
            )
        ).resolved()

        for (candidate in TicketPermissionCandidate.entries - TicketPermissionCandidate.Everyone) {
            assertEquals(
                TicketPanelPermissionsModel.DEFAULT.open.getValue(candidate),
                permissions.open.getValue(candidate)
            )
        }

        assertEquals(TicketPanelPermissionsModel.DEFAULT.closed, permissions.closed)
    }

    @Test
    fun testOmittedActionUsesDefaultAndExplicitFalseIsKept() {
        val permissions = TicketPanelPermissionsModel(
            open = mapOf(
                TicketPermissionCandidate.SupportTeam to TicketCandidatePermissionsModel(
                    actions = mapOf(TicketAction.Close to false, TicketAction.Unclaim to true)
                )
            )
        ).resolved()

        val supportTeam = permissions.open.getValue(TicketPermissionCandidate.SupportTeam)

        assertEquals(false, supportTeam.actions[TicketAction.Close])
        assertEquals(true, supportTeam.actions[TicketAction.Unclaim])
        assertEquals(true, supportTeam.actions[TicketAction.Claim])
        assertEquals(TicketAction.forPhase(TicketPhase.Open).toSet(), supportTeam.actions.keys)
    }

    @Test
    fun testChannelPermissionsAreTakenAsGiven() {
        val permissions = TicketPanelPermissionsModel(
            open = mapOf(
                TicketPermissionCandidate.TicketCreator to TicketCandidatePermissionsModel(
                    channel = mapOf(
                        DiscordChannelPermission.AttachFiles to true,
                        DiscordChannelPermission.SendMessages to false
                    )
                )
            )
        ).resolved()

        val channel = permissions.channelPermissions(TicketPhase.Open, TicketPermissionCandidate.TicketCreator)

        assertEquals(
            mapOf(DiscordChannelPermission.SendMessages to false, DiscordChannelPermission.AttachFiles to true),
            channel
        )
        // absent means "not overwritten", it doesn't fall back to the default
        assertEquals(null, channel[DiscordChannelPermission.ViewChannel])
    }

    @Test
    fun testActionsOfOtherPhasesAreDropped() {
        val permissions = TicketPanelPermissionsModel(
            open = mapOf(
                TicketPermissionCandidate.SupportTeam to TicketCandidatePermissionsModel(
                    actions = mapOf(TicketAction.Reopen to true)
                )
            ),
            closed = mapOf(
                TicketPermissionCandidate.SupportTeam to TicketCandidatePermissionsModel(
                    actions = mapOf(TicketAction.Claim to true)
                )
            )
        ).resolved()

        assertFalse(TicketAction.Reopen in permissions.open.getValue(TicketPermissionCandidate.SupportTeam).actions)
        assertFalse(TicketAction.Claim in permissions.closed.getValue(TicketPermissionCandidate.SupportTeam).actions)
    }

    @Test
    fun testIsAllowedChecksAnyCandidate() {
        val permissions = TicketPanelPermissionsModel(
            open = mapOf(
                TicketPermissionCandidate.TicketCreator to TicketCandidatePermissionsModel(
                    actions = mapOf(TicketAction.Close to false)
                )
            )
        ).resolved()

        val creator = setOf(TicketPermissionCandidate.TicketCreator, TicketPermissionCandidate.Everyone)
        val supportMemberAsCreator = creator + TicketPermissionCandidate.SupportTeam

        assertFalse(permissions.isAllowed(TicketPhase.Open, creator, TicketAction.Close))
        assertTrue(permissions.isAllowed(TicketPhase.Open, supportMemberAsCreator, TicketAction.Close))
        assertFalse(permissions.isAllowed(TicketPhase.Open, emptySet(), TicketAction.Close))
    }

    @Test
    fun testIsAllowedRespectsPhases() {
        val permissions = TicketPanelPermissionsModel.DEFAULT
        val supportTeam = setOf(TicketPermissionCandidate.SupportTeam)

        assertTrue(permissions.isAllowed(TicketPhase.Closed, supportTeam, TicketAction.Reopen))
        assertFalse(permissions.isAllowed(TicketPhase.Open, supportTeam, TicketAction.Reopen))
        assertTrue(permissions.isAllowed(TicketPhase.Open, supportTeam, TicketAction.Claim))
        assertFalse(permissions.isAllowed(TicketPhase.Closed, supportTeam, TicketAction.Claim))
    }

    @Test
    fun testIsAllowedFallsBackToDefaults() {
        val permissions = TicketPanelPermissionsModel()

        assertTrue(permissions.isAllowed(TicketPhase.Open, setOf(TicketPermissionCandidate.TicketClaimer), TicketAction.Unclaim))
        assertFalse(permissions.isAllowed(TicketPhase.Open, setOf(TicketPermissionCandidate.Everyone), TicketAction.Close))
        assertEquals(
            standardChannelPermissions,
            permissions.channelPermissions(TicketPhase.Open, TicketPermissionCandidate.TicketClaimer)
        )
    }
}
