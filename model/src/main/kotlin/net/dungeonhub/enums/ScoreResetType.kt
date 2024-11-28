package net.dungeonhub.enums

import dev.kordex.core.commands.application.slash.converters.ChoiceEnum
import dev.kordex.core.i18n.toKey

enum class ScoreResetType : ChoiceEnum {
    Default,
    Event,
    Both;

    override val readableName = name.toKey()
}