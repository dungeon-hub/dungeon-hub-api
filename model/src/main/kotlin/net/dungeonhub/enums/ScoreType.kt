package net.dungeonhub.enums

import dev.kordex.core.commands.application.slash.converters.ChoiceEnum

enum class ScoreType : ChoiceEnum {
    Default("current", "Current"),
    Alltime("alltime", "Alltime", " (all-time)"),
    Event("event", "Event", " (event)");

    override val readableName: String
    val displayName: String
    val leaderboardSuffix: String?

    constructor(name: String, displayName: String) : this(name, displayName, null)

    constructor(name: String, displayName: String, leaderboardSuffix: String?) {
        this.readableName = name
        this.displayName = displayName
        this.leaderboardSuffix = leaderboardSuffix
    }

    companion object {
        fun fromName(name: String): ScoreType? = entries.firstOrNull { currentType: ScoreType ->
            currentType.readableName.equals(name, ignoreCase = true)
        }
    }
}
