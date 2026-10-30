package net.dungeonhub.model

import net.dungeonhub.enums.ChangeAction
import net.dungeonhub.enums.ChangeEventType
import net.dungeonhub.enums.ScoreType
import net.dungeonhub.model.change.ChangeEventModel
import net.dungeonhub.model.score.ScoreModel
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class ChangeEventModelTest {
    @Test
    fun testDataIsParsedAsTheModelOfTheType() {
        val json = """
            {
              "type": "Score",
              "action": "Updated",
              "serverId": 1,
              "data": {
                "carrier": {"id": 42, "minecraftId": null, "primarySkyblockProfile": null},
                "carryType": null,
                "scoreType": "Default",
                "scoreAmount": 10
              }
            }
        """.trimIndent()

        val change = ChangeEventModel.fromJson(ChangeEventType.Score, json)

        assertEquals(ChangeEventType.Score, change.type)
        assertEquals(ChangeAction.Updated, change.action)
        assertEquals(1L, change.serverId)

        val score = assertIs<ScoreModel>(change.data)
        assertEquals(42L, score.carrier.id)
        assertEquals(ScoreType.Default, score.scoreType)
        assertEquals(10L, score.scoreAmount)
    }

    @Test
    fun testUnknownEventNamesAreIgnored() {
        assertEquals(ChangeEventType.Ticket, ChangeEventType.fromEventName("Ticket"))
        assertNull(ChangeEventType.fromEventName("SomeFutureType"))
        assertNull(ChangeEventType.fromEventName(null))
    }
}
