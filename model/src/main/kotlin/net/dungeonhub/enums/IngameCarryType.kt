package net.dungeonhub.enums

enum class IngameCarryType(val includedTypes: Set<IngameCarryType>) {
    Floor1Completion,
    Floor1S,
    Floor1SPlus,
    Floor1(Floor1Completion, Floor1S, Floor1SPlus),

    Floor2Completion,
    Floor2S,
    Floor2SPlus,
    Floor2(Floor2Completion, Floor2S, Floor2SPlus),

    Floor3Completion,
    Floor3S,
    Floor3SPlus,
    Floor3(Floor3Completion, Floor3S, Floor3SPlus),

    Floor4Completion,
    Floor4S,
    Floor4SPlus,
    Floor4(Floor4Completion, Floor4S, Floor4SPlus),

    Floor5Completion,
    Floor5S,
    Floor5SPlus,
    Floor5(Floor5Completion, Floor5S, Floor5SPlus),

    Floor6Completion,
    Floor6S,
    Floor6SPlus,
    Floor6(Floor6Completion, Floor6S, Floor6SPlus),

    Floor7Completion,
    Floor7S,
    Floor7SPlus,
    Floor7(Floor7Completion, Floor7S, Floor7SPlus),

    MasterMode1Completion,
    MasterMode1S,
    MasterMode1SPlus,
    MasterMode1(MasterMode1Completion, MasterMode1S, MasterMode1SPlus),

    MasterMode2Completion,
    MasterMode2S,
    MasterMode2SPlus,
    MasterMode2(MasterMode2Completion, MasterMode2S, MasterMode2SPlus),

    MasterMode3Completion,
    MasterMode3S,
    MasterMode3SPlus,
    MasterMode3(MasterMode3Completion, MasterMode3S, MasterMode3SPlus),

    MasterMode4Completion,
    MasterMode4S,
    MasterMode4SPlus,
    MasterMode4(MasterMode4Completion, MasterMode4S, MasterMode4SPlus),

    MasterMode5Completion,
    MasterMode5S,
    MasterMode5SPlus,
    MasterMode5(MasterMode5Completion, MasterMode5S, MasterMode5SPlus),

    MasterMode6Completion,
    MasterMode6S,
    MasterMode6SPlus,
    MasterMode6(MasterMode6Completion, MasterMode6S, MasterMode6SPlus),

    MasterMode7Completion,
    MasterMode7S,
    MasterMode7SPlus,
    MasterMode7(MasterMode7Completion, MasterMode7S, MasterMode7SPlus),

    Revenant1,
    Revenant2,
    Revenant3,
    Revenant4,
    Revenant5,

    Tarantula1,
    Tarantula2,
    Tarantula3,
    Tarantula4,
    Tarantula5,

    Sven1,
    Sven2,
    Sven3,
    Sven4,

    Voidgloom1,
    Voidgloom2,
    Voidgloom3Sepulture,
    Voidgloom3Bruiser,
    Voidgloom3(Voidgloom3Sepulture, Voidgloom3Bruiser),
    Voidgloom4Sepulture,
    Voidgloom4Bruiser,
    Voidgloom4(Voidgloom4Sepulture, Voidgloom4Bruiser),

    Inferno1,
    Inferno2,
    Inferno3,
    Inferno4,

    KuudraBasic,
    KuudraHot,
    KuudraBurning,
    KuudraFiery,
    KuudraInfernal;

    constructor() : this(emptySet())

    constructor(vararg ingameCarryTypes: IngameCarryType) : this(ingameCarryTypes.toSet())

    fun includes(carryType: IngameCarryType): Boolean {
        return this == carryType || includedTypes.any { it.includes(carryType) }
    }
}