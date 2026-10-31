package net.dungeonhub.model.carry

/**
 * Carry counts of one carrier on one server.
 *
 * @property logCount the number of logged carries, regardless of their amount
 * @property carryCount the summed amount of all logged carries
 * @property byType the summed amount per carry type id; carry types without carries are not included
 */
class CarryCountsModel(
    val logCount: Long,
    val carryCount: Long,
    val byType: Map<Long, Long>
)
