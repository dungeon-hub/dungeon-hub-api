package net.dungeonhub.model.purge_type

import net.dungeonhub.structure.model.Model
import net.dungeonhub.model.carry_type.CarryTypeModel

class PurgeTypeModel(
    val id: Long,
    val identifier: String,
    val displayName: String,
    val carryType: CarryTypeModel,
    val purgeTypeRoleModels: List<PurgeTypeRoleModel>
) : Model