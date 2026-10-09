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
import kotlin.test.assertFalse
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
        val adapter = MoshiService.moshi.adapter(StaticMessageCreationModel::class.java)
        val scoreMessage = adapter.fromJson(
            """{"channelId":1,"staticMessageType":"ScoreLeaderboard","objectIds":[3,1,3]}"""
        )!!

        assertEquals(listOf(3L, 1L, 3L), linkedObjectValues(scoreMessage.objects))
        assertEquals(StaticMessageObjectType.CarryType, linkedObject(scoreMessage.objects.first()).type)

        val currentJson = adapter.toJson(scoreMessage)
        assertFalse(currentJson.contains("objectIds"))
        assertEquals(scoreMessage.objects, adapter.fromJson(currentJson)!!.objects)
    }

    @Test
    fun testCurrentObjectsTakePrecedenceOverLegacyObjectIds() {
        val adapter = MoshiService.moshi.adapter(StaticMessageCreationModel::class.java)
        val model = adapter.fromJson(
            """
                {
                    "channelId": 1,
                    "staticMessageType": "PriceMessage",
                    "objects": [{"type": "CarryTier", "value": 4}],
                    "objectIds": [9]
                }
            """.trimIndent()
        )!!

        assertEquals(listOf(4L), linkedObjectValues(model.objects))
    }

    @Test
    fun testLegacyEmptyObjectIdsRemainValidForObjectlessMessageTypes() {
        val adapter = MoshiService.moshi.adapter(StaticMessageCreationModel::class.java)
        val model = adapter.fromJson(
            """{"channelId":1,"staticMessageType":"TotalLeaderboard","objectIds":[]}"""
        )!!

        assertEquals(emptyList(), model.objects)
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
    fun testResponseModelReadsLegacyObjectIdsAndWritesCurrentObjects() {
        val json = """
            {
                "id": 1,
                "server": {"id": 2},
                "channelId": 3,
                "messageId": 4,
                "staticMessageType": "TicketPanel",
                "objectIds": [8, 9],
                "active": true
            }
        """.trimIndent()
        val model = StaticMessageModel.fromJson(json)

        assertEquals(listOf(8L, 9L), linkedObjectValues(model.objects))
        val currentJson = MoshiService.moshi.adapter(StaticMessageModel::class.java).toJson(model)
        assertFalse(currentJson.contains("objectIds"))
        assertEquals(model.objects, StaticMessageModel.fromJson(currentJson).objects)
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
    fun testUpdateReadsLegacyObjectIdsAfterTargetTypeIsKnown() {
        val adapter = MoshiService.moshi.adapter(StaticMessageUpdateModel::class.java)
        val update = adapter.fromJson("""{"objectIds":[9,7,9]}""")!!

        update.validateFor(StaticMessageType.PriceMessage)

        assertEquals(listOf(9L, 7L, 9L), linkedObjectValues(update.objects!!))
        assertEquals(StaticMessageObjectType.CarryTier, linkedObject(update.objects!!.first()).type)
        assertFalse(adapter.toJson(update).contains("objectIds"))
    }

    @Test
    fun testLinkedObjectRequiresValue() {
        val adapter = MoshiService.moshi.adapter(StaticMessageObject::class.java)

        assertFailsWith<JsonDataException> {
            adapter.fromJson("""{"type":"TicketPanel"}""")
        }
    }

    private fun linkedObject(entry: StaticMessageObject): StaticMessageObject.LinkedObject {
        return assertIs<StaticMessageObject.LinkedObject>(entry)
    }

    private fun linkedObjectValues(objects: List<StaticMessageObject>): List<Long> {
        return objects.map { linkedObject(it).value }
    }
}
