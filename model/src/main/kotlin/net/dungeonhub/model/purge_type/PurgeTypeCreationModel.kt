package net.dungeonhub.model.purge_type

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.CreationModel

class PurgeTypeCreationModel(
    var identifier: String,
    var displayName: String
) : CreationModel {
    fun toJson(): String {
        return MoshiService.moshi.adapter(PurgeTypeCreationModel::class.java).toJson(this)
    }
}