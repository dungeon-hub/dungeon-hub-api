package net.dungeonhub.model.purge_type

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateModel

class PurgeTypeUpdateModel(
    var displayName: String?
) : UpdateModel<PurgeTypeModel> {
    fun toJson(): String {
        return MoshiService.moshi.adapter(PurgeTypeUpdateModel::class.java).toJson(this)
    }
}