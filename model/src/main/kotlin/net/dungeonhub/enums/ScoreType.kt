package net.dungeonhub.enums

import dev.kordex.core.commands.application.slash.converters.ChoiceEnum
import net.dungeonhub.model.carry_type.CarryTypeModel

enum class ScoreType(
    override val readableName: String,
    val displayName: String,
    val leaderboardSuffix: String?
) : ChoiceEnum {
    Default("current", "Current"),
    Alltime("alltime", "All time", "(all-time)"),
    Event("event", "Event", "(event)");

    constructor(name: String, displayName: String) : this(name, displayName, null)

    fun getLeaderboardTitle(carryType: CarryTypeModel?): String {
        val suffix = if (leaderboardSuffix.isNullOrBlank()) "" else " $leaderboardSuffix"

        if (carryType == null) {
            return "Leaderboard | Total score$suffix"
        }

        return "Leaderboard | ${carryType.displayName}-Carries$suffix"
    }

    companion object {
        fun fromName(name: String): ScoreType? = entries.firstOrNull { currentType: ScoreType ->
            currentType.readableName.equals(name, ignoreCase = true)
        }
    }
}
