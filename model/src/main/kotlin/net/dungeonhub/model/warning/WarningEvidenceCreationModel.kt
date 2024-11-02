package net.dungeonhub.model.warning

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.CreationModel

class WarningEvidenceCreationModel(
    var evidence: String,
    var submitter: Long
) : CreationModel {
    fun toJson(): String {
        return MoshiService.moshi.adapter(WarningEvidenceCreationModel::class.java).toJson(this)
    }
}