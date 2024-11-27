package net.dungeonhub.enums

import dev.kordex.core.commands.application.slash.converters.ChoiceEnum
import dev.kordex.core.i18n.types.Key
import net.dungeonhub.api.model.i18n.Translations
import net.dungeonhub.model.carry_type.CarryTypeModel

enum class ScoreType(
    override val readableName: Key,
    val leaderboardSuffix: String?
) : ChoiceEnum {
    Default(Translations.ScoreType.Default.readableName),
    Alltime(Translations.ScoreType.Alltime.readableName, "(all-time)"),
    Event(Translations.ScoreType.Event.readableName, "(event)");

    constructor(readableName: Key) : this(readableName, null)

    fun getLeaderboardTitle(carryType: CarryTypeModel?): String {
        val suffix = if (leaderboardSuffix.isNullOrBlank()) "" else " $leaderboardSuffix"

        if (carryType == null) {
            return "Leaderboard | Total score$suffix"
        }

        return "Leaderboard | ${carryType.displayName}-Carries$suffix"
    }
}
