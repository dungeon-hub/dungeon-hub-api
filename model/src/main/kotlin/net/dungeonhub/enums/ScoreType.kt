package net.dungeonhub.enums

import dev.kordex.core.commands.application.slash.converters.ChoiceEnum
import dev.kordex.core.i18n.types.Key
import net.dungeonhub.api.model.i18n.Translations
import net.dungeonhub.model.carry_type.CarryTypeModel

enum class ScoreType(
    override val readableName: Key,
    val displayName: String,
    val leaderboardSuffix: String?
) : ChoiceEnum {
    Default(Translations.ScoreType.Default.readableName, "Current"),
    Alltime(Translations.ScoreType.Alltime.readableName, "All time", "(all-time)"),
    Event(Translations.ScoreType.Event.readableName, "Event", "(event)");

    constructor(readableName: Key, displayName: String) : this(readableName, displayName, null)

    fun getLeaderboardTitle(carryType: CarryTypeModel?): String {
        val suffix = if (leaderboardSuffix.isNullOrBlank()) "" else " $leaderboardSuffix"

        if (carryType == null) {
            return "Leaderboard | Total score$suffix"
        }

        return "Leaderboard | ${carryType.displayName}-Carries$suffix"
    }
}
