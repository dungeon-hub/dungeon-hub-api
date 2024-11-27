package net.dungeonhub.enums

import net.dungeonhub.model.carry_type.CarryTypeModel
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.service.TestHelper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScoreTypeTest {
    @Test
    fun testLeaderboardTitle() {
        for(scoreType in ScoreType.entries) {
            val suffix = if (scoreType.leaderboardSuffix.isNullOrBlank()) "" else " ${scoreType.leaderboardSuffix}"

            val carryType = CarryTypeModel(0, "identifier", "DisplayName", DiscordServerModel(0), null, null, false)

            assertEquals(
                "Leaderboard | Total score$suffix",
                scoreType.getLeaderboardTitle(null)
            )

            assertEquals(
                "Leaderboard | DisplayName-Carries$suffix",
                scoreType.getLeaderboardTitle(carryType)
            )
        }
    }

    @Test
    fun testKeys() {
        TestHelper.buildKoinContext()

        val keys = mapOf(
            ScoreType.Default to "score-type.default.readable-name",
            ScoreType.Alltime to "score-type.alltime.readable-name",
            ScoreType.Event to "score-type.event.readable-name"
        )

        val translations = mapOf(
            ScoreType.Default to "Current",
            ScoreType.Alltime to "All time",
            ScoreType.Event to "Event"
        )

        for(scoreType in ScoreType.entries) {
            assertTrue(keys[scoreType] != null)
            assertTrue(translations[scoreType] != null)

            assertEquals(
                keys[scoreType],
                scoreType.readableName.key
            )

            assertEquals(
                translations[scoreType],
                scoreType.readableName.translate()
            )
        }
    }
}