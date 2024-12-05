package net.dungeonhub.enums

import dev.kordex.core.commands.application.slash.converters.ChoiceEnum
import dev.kordex.core.i18n.types.Key
import net.dungeonhub.api.model.i18n.Translations
import net.dungeonhub.model.carry_type.CarryTypeModel
import java.util.Locale

enum class ScoreType(
    override val readableName: Key,
    val leaderboardSuffix: Key?
) : ChoiceEnum {
    Default(Translations.ScoreType.Default.readableName),
    Alltime(Translations.ScoreType.Alltime.readableName, Translations.ScoreType.Alltime.suffix),
    Event(Translations.ScoreType.Event.readableName, Translations.ScoreType.Event.suffix);

    constructor(readableName: Key) : this(readableName, null)

    fun getLeaderboardTitle(carryType: CarryTypeModel?, locale: Locale? = Locale.ENGLISH): String {
        val suffix = leaderboardSuffix?.withLocale(locale)?.translate()

        return Translations.Leaderboard.title.withLocale(locale).translateNamed(
            "name" to (carryType?.displayName ?: Translations.Leaderboard.Title.total.withLocale(locale).translate()),
            "suffix" to if (suffix.isNullOrBlank()) "" else " $suffix"
        )
    }
}
