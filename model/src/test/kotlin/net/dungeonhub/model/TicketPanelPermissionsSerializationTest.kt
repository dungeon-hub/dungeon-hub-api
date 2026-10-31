package net.dungeonhub.model

import com.squareup.moshi.JsonDataException
import net.dungeonhub.enums.DiscordChannelPermission
import net.dungeonhub.enums.TicketAction
import net.dungeonhub.enums.TicketPermissionCandidate
import net.dungeonhub.enums.TranscriptTarget
import net.dungeonhub.exceptions.InvalidUpdateException
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.model.ticket_panel.TicketCandidatePermissionsModel
import net.dungeonhub.model.ticket_panel.TicketPanelCreationModel
import net.dungeonhub.model.ticket_panel.TicketPanelModel
import net.dungeonhub.model.ticket_panel.TicketPanelPermissionsModel
import net.dungeonhub.model.ticket_panel.TicketPanelUpdateModel
import net.dungeonhub.service.GsonService
import net.dungeonhub.service.MoshiService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TicketPanelPermissionsSerializationTest {
    private val moshiAdapter = MoshiService.moshi.adapter(TicketPanelPermissionsModel::class.java)

    // the server reads and writes the models with Gson
    @Suppress("DEPRECATION")
    private val gson = GsonService.gson

    private val partialPermissions = TicketPanelPermissionsModel(
        open = mapOf(
            TicketPermissionCandidate.TicketCreator to TicketCandidatePermissionsModel(
                actions = mapOf(TicketAction.Close to false),
                channel = mapOf(DiscordChannelPermission.AttachFiles to true)
            )
        )
    )

    @Test
    fun testJsonFormat() {
        val permissions = TicketPanelPermissionsModel(
            open = mapOf(
                TicketPermissionCandidate.SupportTeam to TicketCandidatePermissionsModel(
                    mapOf(TicketAction.Close to true),
                    mapOf(DiscordChannelPermission.ViewChannel to false)
                )
            )
        )

        val expected = """{"open":{"SupportTeam":{"actions":{"Close":true},"channel":{"ViewChannel":false}}},"closed":{}}"""

        assertEquals(expected, moshiAdapter.toJson(permissions))
        assertEquals(expected, gson.toJson(permissions))
    }

    @Test
    fun testRoundTrips() {
        val defaults = TicketPanelPermissionsModel.DEFAULT

        assertEquals(defaults, moshiAdapter.fromJson(moshiAdapter.toJson(defaults)))
        assertEquals(defaults, gson.fromJson(gson.toJson(defaults), TicketPanelPermissionsModel::class.java))
        assertEquals(gson.toJson(defaults), moshiAdapter.toJson(defaults))

        assertEquals(partialPermissions, moshiAdapter.fromJson(gson.toJson(partialPermissions)))
        assertEquals(partialPermissions, gson.fromJson(moshiAdapter.toJson(partialPermissions), TicketPanelPermissionsModel::class.java))
    }

    @Test
    fun testMissingFields() {
        val json = """{"open":{"SupportTeam":{}}}"""

        for (permissions in listOf(moshiAdapter.fromJson(json)!!, gson.fromJson(json, TicketPanelPermissionsModel::class.java))) {
            assertEquals(emptyMap(), permissions.closed)
            assertEquals(TicketCandidatePermissionsModel(), permissions.open[TicketPermissionCandidate.SupportTeam])

            // the candidate is given: its actions fall back to the defaults, its channel permissions aren't overwritten
            val supportTeam = permissions.resolved().open.getValue(TicketPermissionCandidate.SupportTeam)

            assertEquals(TicketPanelPermissionsModel.DEFAULT.open.getValue(TicketPermissionCandidate.SupportTeam).actions, supportTeam.actions)
            assertEquals(emptyMap(), supportTeam.channel)
        }
    }

    @Test
    fun testNullValuesFromGson() {
        assertThrows<InvalidUpdateException> {
            gson.fromJson("""{"open":{"SupportTeam":{"actions":{"Close":null}}}}""", TicketPanelPermissionsModel::class.java).resolved()
        }

        val nullChannelValue = gson.fromJson(
            """{"open":{"SupportTeam":{"channel":{"ViewChannel":null,"SendMessages":true}}}}""",
            TicketPanelPermissionsModel::class.java
        ).resolved()

        assertEquals(
            mapOf(DiscordChannelPermission.SendMessages to true),
            nullChannelValue.open.getValue(TicketPermissionCandidate.SupportTeam).channel
        )

        val nullFields = gson.fromJson(
            """{"open":null,"closed":{"SupportTeam":{"actions":null,"channel":null},"Everyone":null}}""",
            TicketPanelPermissionsModel::class.java
        ).resolved()

        assertEquals(TicketPanelPermissionsModel.DEFAULT.open, nullFields.open)
        assertEquals(
            TicketPanelPermissionsModel.DEFAULT.closed.getValue(TicketPermissionCandidate.SupportTeam).actions,
            nullFields.closed.getValue(TicketPermissionCandidate.SupportTeam).actions
        )
        assertEquals(emptyMap(), nullFields.closed.getValue(TicketPermissionCandidate.SupportTeam).channel)
        assertEquals(
            TicketPanelPermissionsModel.DEFAULT.closed.getValue(TicketPermissionCandidate.Everyone),
            nullFields.closed.getValue(TicketPermissionCandidate.Everyone)
        )
    }

    @Test
    fun testUnknownKeys() {
        for (json in listOf(
            """{"open":{"Moderators":{}}}""",
            """{"open":{"SupportTeam":{"actions":{"Fly":true}}}}""",
            """{"closed":{"SupportTeam":{"channel":{"Speak":true}}}}"""
        )) {
            assertThrows<InvalidUpdateException>(json) {
                gson.fromJson(json, TicketPanelPermissionsModel::class.java).resolved()
            }

            assertThrows<JsonDataException>(json) {
                moshiAdapter.fromJson(json)
            }
        }
    }

    @Test
    fun testTicketPanelModel() {
        val ticketPanel = createTicketPanelModel(TicketPanelPermissionsModel.DEFAULT)

        val moshiJson = MoshiService.moshi.adapter(TicketPanelModel::class.java).toJson(ticketPanel)

        assertEquals(TicketPanelPermissionsModel.DEFAULT, TicketPanelModel.fromJson(moshiJson).permissions)
        assertEquals(TicketPanelPermissionsModel.DEFAULT, TicketPanelModel.fromJson(gson.toJson(ticketPanel)).permissions)
    }

    @Test
    fun testCreationModel() {
        val withoutPermissions = createCreationModel(null).toJson()

        assertFalse("permissions" in withoutPermissions)
        assertEquals(null, gson.fromJson(withoutPermissions, TicketPanelCreationModel::class.java).permissions)

        val withPermissions = gson.fromJson(createCreationModel(partialPermissions).toJson(), TicketPanelCreationModel::class.java)

        assertEquals(partialPermissions, withPermissions.permissions)

        val creator = withPermissions.permissions!!.resolved().open.getValue(TicketPermissionCandidate.TicketCreator)

        assertEquals(false, creator.actions[TicketAction.Close])
        assertEquals(true, creator.actions[TicketAction.Delete])
        assertEquals(mapOf(DiscordChannelPermission.AttachFiles to true), creator.channel)
    }

    @Test
    fun testUpdateModel() {
        val updateModel = createTicketPanelModel(TicketPanelPermissionsModel.DEFAULT).getUpdateModel()

        assertEquals(null, updateModel.permissions)
        assertFalse("permissions" in updateModel.toJson())
        assertEquals(null, gson.fromJson(updateModel.toJson(), TicketPanelUpdateModel::class.java).permissions)

        updateModel.permissions = partialPermissions

        val json = updateModel.toJson()

        assertTrue("permissions" in json)
        assertEquals(partialPermissions, gson.fromJson(json, TicketPanelUpdateModel::class.java).permissions)
    }

    private fun createTicketPanelModel(permissions: TicketPanelPermissionsModel): TicketPanelModel {
        return TicketPanelModel(
            id = 1,
            name = "support",
            displayName = null,
            emoji = null,
            discordServer = DiscordServerModel(0),
            closeable = true,
            closeConfirmation = false,
            claimable = true,
            openChannelName = null,
            claimedChannelName = null,
            closedChannelName = null,
            transcriptChannel = null,
            ticketMessage = null,
            requiresLinking = false,
            closeTranscriptTarget = TranscriptTarget.User,
            deleteTranscriptTarget = TranscriptTarget.TranscriptChannel,
            userTranscriptDm = null,
            formQuestions = emptyList(),
            openTicketLimit = null,
            closedTicketLimit = null,
            relatedCarryTier = null,
            relatedCarryDifficulty = null,
            supportRoles = emptyList(),
            additionalRoles = emptyList(),
            openCategories = emptyList(),
            closedCategories = emptyList(),
            permissions = permissions
        )
    }

    private fun createCreationModel(permissions: TicketPanelPermissionsModel?): TicketPanelCreationModel {
        return TicketPanelCreationModel(
            name = "support",
            displayName = null,
            emoji = null,
            closeable = true,
            closeConfirmation = false,
            claimable = true,
            openChannelName = null,
            claimedChannelName = null,
            closedChannelName = null,
            transcriptChannel = null,
            ticketMessage = null,
            requiresLinking = false,
            closeTranscriptTarget = null,
            deleteTranscriptTarget = null,
            userTranscriptDm = null,
            formQuestions = null,
            openTicketLimit = null,
            closedTicketLimit = null,
            relatedCarryTier = null,
            relatedCarryDifficulty = null,
            supportRoles = null,
            additionalRoles = null,
            openCategories = null,
            closedCategories = null,
            permissions = permissions
        )
    }
}
