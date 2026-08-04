package net.dungeonhub.model.stats

import net.dungeonhub.structure.model.Model

class DiscordServerStatsModel(
    val totalMoneySpent: Long,
    val totalCarries: Long,
    val totalTickets: Long,
    val totalCarriers: Long,
    val totalScore: Long,
    val activeWarns: Long,
    val totalWarns: Long,
    val yourMoneySpent: Long?,
    val yourMoneyEarned: Long?,
    val yourCompletedCarries: Long?,
    val yourBoughtCarries: Long?
) : Model