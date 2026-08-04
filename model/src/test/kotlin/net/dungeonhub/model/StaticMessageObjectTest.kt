package net.dungeonhub.model

import com.squareup.moshi.JsonDataException
import net.dungeonhub.enums.StaticMessageType
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.model.static_message.StaticMessageCreationModel
import net.dungeonhub.model.static_message.StaticMessageModel
import net.dungeonhub.model.static_message.StaticMessageObject
import net.dungeonhub.model.static_message.StaticMessageObjectType
import net.dungeonhub.model.static_message.StaticMessageUpdateModel
import net.dungeonhub.service.MoshiService
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class StaticMessageObjectTest {
    @Test
    fun testSupportedLinkedObjectTypes() {
        assertContentEquals(
            listOf(
                StaticMessageObjectType.TicketPanel,
                StaticMessageObjectType.CarryType,
                StaticMessageObjectType.CarryTier
            ),
            StaticMessageObjectType.entries
        )
    }

    @Test
    fun testStaticMessageTypesDefineTheirObjectRelation() {
        assertEquals(StaticMessageObjectType.TicketPanel, StaticMessageType.TicketPanel.linkedObjectType)
        assertEquals(StaticMessageObjectType.CarryType, StaticMessageType.ScoreLeaderboard.linkedObjectType)
        assertEquals(StaticMessageObjectType.CarryTier, StaticMessageType.PriceMessage.linkedObjectType)
        assertEquals(null, StaticMessageType.TotalLeaderboard.linkedObjectType)
        assertEquals(null, StaticMessageType.ReputationLeaderboard.linkedObjectType)
    }

    @Test
    fun testTypedObjectsSupportSeparators() {
        val objects = listOf(
            StaticMessageObject.ticketPanel(10),
            StaticMessageObject.ticketPanel(11),
            StaticMessageObject.separator(),
            StaticMessageObject.ticketPanel(12)
        )
        val model = StaticMessageCreationModel(
            1,
            2,
            StaticMessageType.TicketPanel,
            objects,
            null
        )

        assertEquals(objects, model.objects)
        assertIs<StaticMessageObject.Separator>(model.objects[2])

        val json = model.toJson()
        val parsed = MoshiService.moshi.adapter(StaticMessageCreationModel::class.java).fromJson(json)!!

        val linkedObject = assertIs<StaticMessageObject.LinkedObject>(parsed.objects[0])
        assertEquals(10, linkedObject.value)
        assertIs<StaticMessageObject.Separator>(parsed.objects[2])
    }

    @Test
    fun testUpdateModelCanSendTypedObjects() {
        val updateModel = StaticMessageUpdateModel(
            null,
            null,
            listOf(StaticMessageObject.ticketPanel(10), StaticMessageObject.separator()),
            null,
            null
        )

        assertIs<StaticMessageObject.Separator>(updateModel.objects!![1])
        updateModel.validateFor(StaticMessageType.TicketPanel)
    }

    @Test
    fun testOnlyTicketPanelsSupportSeparators() {
        assertEquals(true, StaticMessageType.TicketPanel.supportsSeparators)
        assertEquals(
            listOf(
                StaticMessageType.ScoreLeaderboard,
                StaticMessageType.TotalLeaderboard,
                StaticMessageType.ReputationLeaderboard,
                StaticMessageType.PriceMessage
            ),
            StaticMessageType.entries.filterNot(StaticMessageType::supportsSeparators)
        )

        assertFailsWith<IllegalArgumentException> {
            StaticMessageCreationModel(
                1,
                null,
                StaticMessageType.PriceMessage,
                listOf(StaticMessageObject.carryTier(10), StaticMessageObject.separator()),
                null
            )
        }
    }

    @Test
    fun testStaticMessageRejectsWrongLinkedObjectType() {
        assertFailsWith<IllegalArgumentException> {
            StaticMessageCreationModel(
                1,
                null,
                StaticMessageType.ScoreLeaderboard,
                listOf(StaticMessageObject.carryTier(10)),
                null
            )
        }

        assertFailsWith<IllegalArgumentException> {
            StaticMessageCreationModel(
                1,
                null,
                StaticMessageType.TotalLeaderboard,
                listOf(StaticMessageObject.carryType(10)),
                null
            )
        }
    }

    @Test
    fun testCreationSupportsPreviousIdListUseCases() {
        val scoreObjects = listOf(3L, 1L, 3L).map { StaticMessageObject.carryType(it) }
        val priceObjects = listOf(6L, 4L, 6L).map { StaticMessageObject.carryTier(it) }

        val scoreMessage = StaticMessageCreationModel(
            1,
            null,
            StaticMessageType.ScoreLeaderboard,
            scoreObjects,
            null
        )
        val priceMessage = StaticMessageCreationModel(
            1,
            null,
            StaticMessageType.PriceMessage,
            priceObjects,
            null
        )
        val totalMessage = StaticMessageCreationModel(
            1,
            null,
            StaticMessageType.TotalLeaderboard,
            emptyList(),
            null
        )

        assertEquals(listOf(3L, 1L, 3L), scoreMessage.objects.map { (it as StaticMessageObject.LinkedObject).value })
        assertEquals(listOf(6L, 4L, 6L), priceMessage.objects.map { (it as StaticMessageObject.LinkedObject).value })
        assertEquals(emptyList(), totalMessage.objects)
    }

    @Test
    fun testResponseModelAllowsServerDataWithoutValidation() {
        val serverData = listOf(
            StaticMessageObject.ticketPanel(10),
            StaticMessageObject.separator()
        )

        val model = StaticMessageModel(
            1,
            DiscordServerModel(2),
            3,
            4,
            StaticMessageType.PriceMessage,
            serverData,
            null,
            true
        )

        assertEquals(serverData, model.objects)
    }

    @Test
    fun testUpdateObjectsAreValidatedAgainstTargetType() {
        val updateModel = StaticMessageUpdateModel(
            null,
            null,
            listOf(StaticMessageObject.ticketPanel(10)),
            null,
            null
        )

        assertFailsWith<IllegalArgumentException> {
            updateModel.validateFor(StaticMessageType.PriceMessage)
        }
    }

    @Test
    fun testUpdatePreservesPreviousOptionalAndOrderedIdBehavior() {
        val omittedObjects = StaticMessageUpdateModel(null, null, null, null, null)
        omittedObjects.validateFor(StaticMessageType.ScoreLeaderboard)

        val orderedObjects = StaticMessageUpdateModel(
            null,
            null,
            listOf(9L, 7L, 9L).map { StaticMessageObject.carryType(it) },
            null,
            null
        )
        orderedObjects.validateFor(StaticMessageType.ScoreLeaderboard)

        assertEquals(null, omittedObjects.objects)
        assertEquals(
            listOf(9L, 7L, 9L),
            orderedObjects.objects!!.map { (it as StaticMessageObject.LinkedObject).value }
        )
    }

    @Test
    fun testLinkedObjectRequiresValue() {
        val adapter = MoshiService.moshi.adapter(StaticMessageObject::class.java)

        assertFailsWith<JsonDataException> {
            adapter.fromJson("""{"type":"TicketPanel"}""")
        }
    }
}
