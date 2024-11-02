package net.dungeonhub.model.cnt_request

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.CreationModel
import java.time.Instant

class CntRequestCreationModel(
    var messageId: Long,
    var user: Long,
    var claimer: Long? = null,
    var time: Instant,
    var coinValue: String,
    var description: String,
    var requirement: String
) : CreationModel {
    fun toJson(): String {
        return MoshiService.moshi.adapter(CntRequestCreationModel::class.java).toJson(this)
    }
}