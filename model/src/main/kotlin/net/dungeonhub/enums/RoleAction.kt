package net.dungeonhub.enums

import dev.kordex.core.commands.application.slash.converters.ChoiceEnum
import dev.kordex.core.i18n.types.Key
import net.dungeonhub.i18n.Translations

enum class RoleAction(override val readableName: Key) : ChoiceEnum {
    None(Translations.RoleActions.None.readableName),
    ApplyAndRemoveWhenVerified(Translations.RoleActions.ApplyAndRemoveWhenVerified.readableName),
    ApplyAndRemoveWhenUnverified(Translations.RoleActions.ApplyAndRemoveWhenUnverified.readableName),
    ApplyWhenVerified(Translations.RoleActions.ApplyWhenVerified.readableName),
    ApplyWhenUnverified(Translations.RoleActions.ApplyWhenUnverified.readableName),
    RemoveWhenVerified(Translations.RoleActions.RemoveWhenVerified.readableName),
    RemoveWhenUnverified(Translations.RoleActions.RemoveWhenUnverified.readableName),
    ApplyAlways(Translations.RoleActions.ApplyAlways.readableName);

    companion object {
        val applyWhenVerified = setOf(ApplyWhenVerified, ApplyAndRemoveWhenVerified, ApplyAlways)
        val applyWhenUnverified = setOf(ApplyWhenUnverified, ApplyAndRemoveWhenUnverified, ApplyAlways)

        val removeWhenVerified = setOf(RemoveWhenVerified, ApplyAndRemoveWhenUnverified)
        val removeWhenUnverified = setOf(RemoveWhenUnverified, ApplyAndRemoveWhenVerified)
    }
}