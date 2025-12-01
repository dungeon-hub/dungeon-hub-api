package net.dungeonhub.model.static_message

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateModel

class StaticMessageUpdateModel(
    var channelId: Long?,
    var messageId: Long?,
    var objectIds: List<Long>?
) : UpdateModel<StaticMessageModel> {
    fun toJson(): String {
        return MoshiService.moshi.adapter(StaticMessageUpdateModel::class.java).toJson(this)
    }
}
