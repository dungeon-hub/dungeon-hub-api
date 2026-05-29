package net.dungeonhub.enums

import dev.kordex.i18n.Key

enum class StaticMessageType {
    ScoreLeaderboard,
    TotalLeaderboard,
    ReputationLeaderboard,
    TicketPanel,
    PriceMessage;

    val readableName = Key(name.replace(Regex("([A-Z])"), " $1").trim())
}