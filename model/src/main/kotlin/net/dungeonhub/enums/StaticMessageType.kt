package net.dungeonhub.enums

import dev.kordex.core.commands.application.slash.converters.ChoiceEnum
import dev.kordex.core.i18n.toKey

enum class StaticMessageType : ChoiceEnum {
    ScoreLeaderboard,
    TotalLeaderboard,
    ReputationLeaderboard,
    TicketPanel,
    PriceMessage;

    override val readableName = name.replace(Regex("([A-Z])"), " $1").trim().toKey()
}