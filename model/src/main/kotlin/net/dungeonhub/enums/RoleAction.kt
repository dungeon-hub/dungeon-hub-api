package net.dungeonhub.enums

import dev.kordex.core.commands.application.slash.converters.ChoiceEnum

enum class RoleAction(override val readableName: String) : ChoiceEnum {
    None("Apply never"),
    ApplyAndRemoveWhenVerified("Apply when verified, remove when unverified"),
    ApplyAndRemoveWhenUnverified("Apply when unverified, remove when verified"),
    ApplyWhenVerified("Apply when verified"),
    ApplyWhenUnverified("Apply when unverified"),
    RemoveWhenVerified("Remove when verified"),
    RemoveWhenUnverified("Remove when unverified"),
    ApplyAlways("Apply always");

    companion object {
        val applyWhenVerified = setOf(ApplyWhenVerified, ApplyAndRemoveWhenVerified, ApplyAlways)
        val applyWhenUnverified = setOf(ApplyWhenUnverified, ApplyAndRemoveWhenUnverified, ApplyAlways)

        val removeWhenVerified = setOf(RemoveWhenVerified, ApplyAndRemoveWhenUnverified)
        val removeWhenUnverified = setOf(RemoveWhenUnverified, ApplyAndRemoveWhenVerified)
    }
}