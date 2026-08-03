package net.dungeonhub.model.stats

import net.dungeonhub.structure.model.Model

class GlobalStatsModel(
    val linkedUsers: Long,
    val carryStats: GlobalCarryStatsModel,
    val ticketStatsModel: GlobalTicketStatsModel,
    val carrierStatsModel: GlobalCarrierStatsModel
): Model