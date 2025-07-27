package net.dungeonhub.model.reputation

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.CreationModel

class ReputationCreationModel(
    val user: Long,
    val reputor: Long,
    val amount: Int,
    val reason: String?
) : CreationModel {
    fun toJson(): String {
        return MoshiService.moshi.adapter(ReputationCreationModel::class.java).toJson(this)
    }
}