package net.dungeonhub.model

import net.dungeonhub.model.carry.CarryModel
import net.dungeonhub.model.carry_difficulty.BulkPriceModel
import net.dungeonhub.model.carry_difficulty.CarryDifficultyModel
import net.dungeonhub.model.carry_tier.CarryTierModel
import net.dungeonhub.model.carry_type.CarryTypeModel
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.model.discord_user.DiscordUserModel
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CarryDifficultyPriceTest {
    private val bulkPrices = listOf(
        BulkPriceModel(10, 70),
        BulkPriceModel(5, 80),
        BulkPriceModel(20, 0)
    )

    @Test
    fun testBasePriceWithoutBulkPrices() {
        assertEquals(100, CarryDifficultyModel.calculatePricePerCarry(50, emptyList(), 100))
    }

    @Test
    fun testBasePriceBelowLowestBulkAmount() {
        assertEquals(100, CarryDifficultyModel.calculatePricePerCarry(4, bulkPrices, 100))
        assertEquals(400, CarryDifficultyModel.calculateTotalPrice(4, bulkPrices, 100))
    }

    @Test
    fun testHighestReachedBulkPriceIsUsed() {
        assertEquals(80, CarryDifficultyModel.calculatePricePerCarry(5, bulkPrices, 100))
        assertEquals(80, CarryDifficultyModel.calculatePricePerCarry(9, bulkPrices, 100))
        assertEquals(70, CarryDifficultyModel.calculatePricePerCarry(10, bulkPrices, 100))
        assertEquals(1050, CarryDifficultyModel.calculateTotalPrice(15, bulkPrices, 100))
    }

    @Test
    fun testInvalidBulkPricesAreIgnored() {
        assertEquals(70, CarryDifficultyModel.calculatePricePerCarry(25, bulkPrices, 100))
    }

    @Test
    fun testNonPositiveBulkAmountsAreIgnored() {
        assertEquals(100, CarryDifficultyModel.calculatePricePerCarry(4, listOf(BulkPriceModel(0, 90)), 100))
    }

    @Test
    fun testCarryUsesReachedBulkPrice() {
        val carryDifficulty = CarryDifficultyModel(
            0,
            "identifier",
            "displayName",
            CarryTierModel(
                0,
                "identifier",
                "displayName",
                CarryTypeModel(0, "identifier", "displayName", DiscordServerModel(0), 0, false),
                0,
                null,
                null,
                null,
                null
            ),
            100,
            bulkPrices,
            1,
            null,
            null,
            null
        )
        val user = DiscordUserModel(0, null, null)

        assertEquals(100, CarryModel(0, 4, carryDifficulty, user, user, null, null, null).calculatePrice())
        assertEquals(70, CarryModel(0, 12, carryDifficulty, user, user, null, null, null).calculatePrice())
    }

    @Test
    fun testCarryDifficultyJsonWithBulkPrices() {
        val json = """{"id":1,"identifier":"comp","displayName":"Comp","carryTier":{"id":1,"identifier":"f7","displayName":"F7","carryType":{"id":1,"identifier":"d","displayName":"D","server":{"id":1},"isEventActive":false}},"price":100,"bulkPrices":[{"amount":10,"price":70},{"amount":5,"price":80}],"score":1}"""

        val carryDifficulty = CarryDifficultyModel.fromJson(json)

        assertEquals(listOf(BulkPriceModel(5, 80), BulkPriceModel(10, 70)), carryDifficulty.bulkPrices)
        assertEquals(carryDifficulty.bulkPrices, CarryDifficultyModel.fromJson(carryDifficulty.toJson()).bulkPrices)
    }

    @Test
    fun testCarryDifficultyJsonWithoutBulkPrices() {
        val json = """{"id":1,"identifier":"comp","displayName":"Comp","carryTier":{"id":1,"identifier":"f7","displayName":"F7","carryType":{"id":1,"identifier":"d","displayName":"D","server":{"id":1},"isEventActive":false}},"price":100,"bulkPrice":80,"bulkAmount":5,"score":1}"""

        val carryDifficulty = CarryDifficultyModel.fromJson(json)

        assertEquals(emptyList(), carryDifficulty.bulkPrices)
        assertEquals(100, carryDifficulty.calculatePricePerCarry(10))
    }
}
