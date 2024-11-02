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

        assertEquals("thumbnailUrl", carryDifficultyUpdateModel.thumbnailUrl)
        assertEquals(false, carryDifficultyUpdateModel.resetThumbnailUrl)

        carryDifficultyUpdateModel.thumbnailUrl = null

        assertEquals(null, carryDifficultyUpdateModel.thumbnailUrl)
        assertEquals(true, carryDifficultyUpdateModel.resetThumbnailUrl)

        val json = carryDifficultyUpdateModel.toJson()

        val newObject = CarryDifficultyUpdateModel.fromJson(json)

        assertEquals(null, newObject.thumbnailUrl)
        assertEquals(true, newObject.resetThumbnailUrl)
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

        assertEquals(null, updateModel.displayName)
        assertEquals(null, updateModel.thumbnailUrl)
        assertEquals(null, updateModel.bulkPrice)
        assertEquals(null, updateModel.bulkAmount)
        assertEquals(null, updateModel.priceName)
        assertEquals(null, updateModel.price)
        assertEquals(null, updateModel.score)

        updateModel.thumbnailUrl = null
    }
}