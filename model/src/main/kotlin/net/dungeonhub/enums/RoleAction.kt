package net.dungeonhub.enums

enum class RoleAction {
    None,
    ApplyAndRemoveWhenVerified,
    ApplyAndRemoveWhenUnverified,
    ApplyWhenVerified,
    ApplyWhenUnverified,
    RemoveWhenVerified,
    RemoveWhenUnverified,
    ApplyAlways;

    companion object {
        val applyWhenVerified = setOf(ApplyWhenVerified, ApplyAndRemoveWhenVerified, ApplyAlways)
        val applyWhenUnverified = setOf(ApplyWhenUnverified, ApplyAndRemoveWhenUnverified, ApplyAlways)

        val removeWhenVerified = setOf(RemoveWhenVerified, ApplyAndRemoveWhenUnverified)
        val removeWhenUnverified = setOf(RemoveWhenUnverified, ApplyAndRemoveWhenVerified)
    }
}
