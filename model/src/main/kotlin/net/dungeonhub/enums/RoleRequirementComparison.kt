package net.dungeonhub.enums

import dev.kordex.i18n.Key

enum class RoleRequirementComparison(val readableName: Key) {
    Equal("="),
    GreaterOrEqual(">="),
    Greater(">"),
    LessOrEqual("<="),
    Less("<");

    constructor(readableName: String) : this(Key(readableName))

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