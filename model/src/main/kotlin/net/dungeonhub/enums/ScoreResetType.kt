package net.dungeonhub.enums

import dev.kordex.i18n.Key

enum class ScoreResetType {
    Default,
    Event,
    Both;

    val readableName = Key(name)
}