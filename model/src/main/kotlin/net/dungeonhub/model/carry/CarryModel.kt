package net.dungeonhub.model.carry

import net.dungeonhub.structure.model.Model
import net.dungeonhub.model.carry_difficulty.CarryDifficultyModel
import net.dungeonhub.model.discord_user.DiscordUserModel
import net.dungeonhub.service.MoshiService
import java.time.Instant

data class CarryModel(
    val id: Long,
    val amount: Long,
    val carryDifficulty: CarryDifficultyModel,
    val player: DiscordUserModel,
    val carrier: DiscordUserModel,
    val approver: Long?,
    val attachmentLink: String?,
    val time: Instant?
): Model {
    val carryTier = carryDifficulty.carryTier
    val carryType = carryDifficulty.carryType

    val scoreMultiplier = carryDifficulty.score

    fun calculatePrice(): Long {
        val bulkPrice = carryDifficulty.bulkPrice
        val bulkAmount = carryDifficulty.bulkAmount

        if (bulkPrice != null && bulkAmount != null && bulkAmount <= amount) {
            return bulkPrice.toLong()
        }

        return carryDifficulty.price.toLong()
    }

    fun calculateScore(): Long {
        return scoreMultiplier * amount
    }

    companion object {
        fun fromString(json: String): CarryModel {
            return MoshiService.moshi.adapter(CarryModel::class.java).fromJson(json)!!
        }
    }
}
