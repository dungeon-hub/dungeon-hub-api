package net.dungeonhub.model

import net.dungeonhub.model.carry_difficulty.CarryDifficultyModel
import net.dungeonhub.model.carry_difficulty.CarryDifficultyUpdateModel
import net.dungeonhub.model.carry_tier.CarryTierModel
import net.dungeonhub.model.carry_type.CarryTypeModel
import net.dungeonhub.model.discord_server.DiscordServerModel
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CarryDifficultyUpdateModelTest {
    @Test
    fun testCorrectSettingOfNullValues() {
        val carryDifficultyUpdateModel = CarryDifficultyUpdateModel(
            "displayName",
            "thumbnailUrl",
            1,
            1,
            "priceName",
            1,
            1
        )

        val resetThumbnailUrl = carryDifficultyUpdateModel.javaClass.getDeclaredField("resetThumbnailUrl")
        resetThumbnailUrl.trySetAccessible()

        assertEquals("thumbnailUrl", carryDifficultyUpdateModel.thumbnailUrl)
        assertEquals(false, resetThumbnailUrl.getBoolean(carryDifficultyUpdateModel))

        carryDifficultyUpdateModel.thumbnailUrl = null

        assertEquals(null, carryDifficultyUpdateModel.thumbnailUrl)
        assertEquals(true, resetThumbnailUrl.getBoolean(carryDifficultyUpdateModel))

        val json = carryDifficultyUpdateModel.toJson()

        val newObject = CarryDifficultyUpdateModel.fromJson(json)

        assertEquals(null, newObject.thumbnailUrl)
        assertEquals(true, resetThumbnailUrl.getBoolean(newObject))
    }

    @Test
    fun testCorrectCreationOfUpdateModel() {
        val carryDifficultyModel = CarryDifficultyModel(
            0,
            "identifier",
            "displayName",
            CarryTierModel(
                0,
                "identifier",
                "displayName",
                CarryTypeModel(
                    0,
                    "identifier",
                    "displayName",
                    DiscordServerModel(0),
                    0,
                    0,
                    false
                ),
                0,
                0,
                "descriptiveName",
                "thumbnailUrl",
                "priceTitle",
                "priceDescription"
            ),
            0,
            0,
            0,
            0,
            "thumbnailUrl",
            "priceName"
        )

        val updateModel = carryDifficultyModel.getUpdateModel()

        assertEquals(carryDifficultyModel.displayName, updateModel.displayName)
        assertEquals(carryDifficultyModel.thumbnailUrl, updateModel.thumbnailUrl)
        assertEquals(carryDifficultyModel.bulkPrice, updateModel.bulkPrice)
        assertEquals(carryDifficultyModel.bulkAmount, updateModel.bulkAmount)
        assertEquals(carryDifficultyModel.priceName, updateModel.priceName)
        assertEquals(carryDifficultyModel.price, updateModel.price)
        assertEquals(carryDifficultyModel.score, updateModel.score)
    }
}