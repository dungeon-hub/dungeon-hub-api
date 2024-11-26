package net.dungeonhub.enums

import dev.kordex.core.commands.application.slash.converters.ChoiceEnum
import dev.kordex.core.i18n.toKey

enum class RoleRequirementType(val needsExtraData: ExtraDataType = ExtraDataType.None) : ChoiceEnum {
    SkyblockLevel,
    CatacombsLevel,
    FarmingLevel,
    MiningLevel,
    CombatLevel,
    FishingLevel,
    SkillAverage,
    HighestSkill,
    TotalAlltimeScore,
    TotalCurrentScore,
    CurrentScoreOfType(ExtraDataType.CarryType),
    AlltimeScoreOfType(ExtraDataType.CarryType),
    TotalCarries,
    TotalCarriesInTimeFrame(ExtraDataType.Duration),
    MoneySpent,
    MoneySpentInTimeFrame(ExtraDataType.Duration);

    override val readableName = name.replace(Regex("([A-Z])"), " $1").trim().toKey()

    enum class ExtraDataType(val checkExtraData: (String?) -> Boolean) {
        None({ true }),
        Duration({ !it.isNullOrBlank() && kotlin.time.Duration.parseOrNull(it) != null }),
        CarryType({ !it.isNullOrBlank() });
    }
}