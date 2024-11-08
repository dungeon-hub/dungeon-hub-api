package net.dungeonhub.model.carry_queue

import net.dungeonhub.enums.QueueStep
import net.dungeonhub.model.carry_difficulty.CarryDifficultyModel
import net.dungeonhub.model.discord_user.DiscordUserModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateModel
import java.time.Instant

class CarryQueueUpdateModel(
    var queueStep: QueueStep?,
    var carrier: DiscordUserModel?,
    var player: DiscordUserModel?,
    var amount: Long?,
    var carryDifficulty: CarryDifficultyModel?,
    relationId: Long?,
    attachmentLink: String?,
    time: Instant?,
    var approver: Long?
) : UpdateModel<CarryQueueModel> {
    var relationId = relationId
        set(value) {
            field = value
            resetRelationId = value == null
        }

    var attachmentLink = attachmentLink
        set(value) {
            field = value
            resetAttachmentLink = value == null
        }

    var time = time
        set(value) {
            field = value
            resetTime = value == null
        }

    var resetRelationId = false
        private set
    var resetAttachmentLink = false
        private set
    var resetTime = false
        private set

    fun toJson(): String {
        return MoshiService.moshi.adapter(CarryQueueUpdateModel::class.java).toJson(this)
    }
}
