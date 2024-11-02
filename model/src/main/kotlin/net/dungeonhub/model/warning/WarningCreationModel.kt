package net.dungeonhub.model.warning

import net.dungeonhub.enums.WarningType
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.CreationModel

class WarningCreationModel(
    var user: Long,
    var striker: Long,
    var warningType: WarningType,
    var reason: String? = null,
    var active: Boolean = true
) : CreationModel {
    fun toJson(): String {
        return MoshiService.moshi.adapter(WarningCreationModel::class.java).toJson(this)
    }
}