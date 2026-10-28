package net.dungeonhub.model

import net.dungeonhub.model.carry_difficulty.BulkPriceModel
import net.dungeonhub.model.carry_difficulty.CarryDifficultyModel
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
}
