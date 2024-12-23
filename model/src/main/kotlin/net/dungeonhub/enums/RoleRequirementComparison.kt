package net.dungeonhub.enums

import dev.kordex.core.commands.application.slash.converters.ChoiceEnum
import dev.kordex.core.i18n.toKey
import dev.kordex.core.i18n.types.Key

enum class RoleRequirementComparison(override val readableName: Key) : ChoiceEnum {
    Equal("="),
    GreaterOrEqual(">="),
    Greater(">"),
    LessOrEqual("<="),
    Less("<");

    constructor(readableName: String) : this(readableName.toKey())

    fun compare(value: Int, expected: Int): Boolean {
        return when (this) {
            Equal -> value == expected
            GreaterOrEqual -> value >= expected
            Greater -> value > expected
            LessOrEqual -> value <= expected
            Less -> value < expected
        }
    }
}