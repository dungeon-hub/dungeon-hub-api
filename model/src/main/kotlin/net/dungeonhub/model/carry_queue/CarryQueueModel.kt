package net.dungeonhub.model.carry_queue

import net.dungeonhub.enums.QueueStep
import net.dungeonhub.model.carry_difficulty.CarryDifficultyModel
import net.dungeonhub.model.discord_user.DiscordUserModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateableModel
import java.time.Instant

class CarryQueueModel(
    val id: Long,
    val queueStep: QueueStep,
    val carrier: DiscordUserModel,
    val player: DiscordUserModel,
    val amount: Long,
    val carryDifficulty: CarryDifficultyModel,
    val relationId: Long?,
    val attachmentLink: String?,
    val time: Instant?
) : UpdateableModel<CarryQueueUpdateModel, CarryQueueModel> {
    val carryTier = carryDifficulty.carryTier

    val carryType = carryTier.carryType

    fun calculateScore(): Long {
        return scoreMultiplier * amount
    }

    private val scoreMultiplier = carryDifficulty.score

    companion object {
        fun fromJson(json: String): CarryQueueModel {
            return MoshiService.moshi.adapter(CarryQueueModel::class.java).fromJson(json)!!
        }
    }

    override fun getUpdateModel(): CarryQueueUpdateModel {
        return CarryQueueUpdateModel(null, null, null, null, null, null, null, null, null)
    }
}