package net.dungeonhub.model.purge_type

import net.dungeonhub.model.carry_type.CarryTypeModel
import net.dungeonhub.structure.model.Model

class SimplePurgeTypeModel(
    val id: Long,
    val identifier: String,
    val displayName: String,
    val carryType: CarryTypeModel,
) : Model