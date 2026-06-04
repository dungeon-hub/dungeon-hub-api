package net.dungeonhub.model.carry_queue

import net.dungeonhub.enums.IngameCarryType

class IngameQueueCreationModel(
    val type: IngameCarryType,
    val ticketIds: List<Long>
)