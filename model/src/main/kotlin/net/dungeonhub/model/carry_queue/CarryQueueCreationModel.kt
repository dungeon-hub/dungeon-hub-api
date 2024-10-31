package net.dungeonhub.model.carry_queue

import net.dungeonhub.enums.QueueStep
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.CreationModel
import java.time.Instant

class CarryQueueCreationModel(
    var queueStep: QueueStep,
    var carrier: Long,
    var player: Long,
    var amount: Long,
    var relationId: Long? = null,
    var attachmentLink: String? = null,
    var time: Instant? = null,
) : CreationModel {
    fun toJson(): String {
        return MoshiService.moshi.adapter(CarryQueueCreationModel::class.java).toJson(this)
    }
}
