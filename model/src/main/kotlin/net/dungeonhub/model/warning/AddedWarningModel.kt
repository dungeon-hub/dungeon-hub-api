package net.dungeonhub.model.warning

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.Model

class AddedWarningModel(
    val warningModel: WarningModel,
    val warningActionModel: List<WarningActionModel>
) : Model {
    companion object {
        fun fromJson(json: String): AddedWarningModel {
            return MoshiService.moshi.adapter(AddedWarningModel::class.java).fromJson(json)!!
        }
    }
}