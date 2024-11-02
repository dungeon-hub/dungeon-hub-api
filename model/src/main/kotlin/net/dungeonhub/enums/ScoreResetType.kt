package net.dungeonhub.enums

import dev.kordex.core.commands.application.slash.converters.ChoiceEnum

enum class ScoreResetType : ChoiceEnum {
    Default,
    Event,
    Both;

    override val readableName = name
}