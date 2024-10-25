package net.dungeonhub.enums

import net.dungeonhub.model.carry_type.CarryTypeModel
import net.dungeonhub.model.discord_server.DiscordServerModel
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

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
}