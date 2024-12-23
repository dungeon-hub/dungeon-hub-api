package net.dungeonhub.enums

enum class RoleRequirementComparison(val comparison: String) {
    Equal("="),
    GreaterOrEqual(">="),
    Greater(">"),
    LessOrEqual("<="),
    Less("<");

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