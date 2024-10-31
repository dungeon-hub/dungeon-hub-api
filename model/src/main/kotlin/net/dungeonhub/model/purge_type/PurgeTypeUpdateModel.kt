package net.dungeonhub.model.purge_type

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateModel

class PurgeTypeUpdateModel(
    var displayName: String?
) : UpdateModel<PurgeTypeModel> {
    /*override fun apply(purgeTypeModel: PurgeTypeModel): PurgeTypeModel {
        if (displayName != null) {
            purgeTypeModel.setDisplayName(displayName)
        }

        return purgeTypeModel
    }*/

    fun toJson(): String {
        return MoshiService.moshi.adapter(PurgeTypeUpdateModel::class.java).toJson(this)
    }
}