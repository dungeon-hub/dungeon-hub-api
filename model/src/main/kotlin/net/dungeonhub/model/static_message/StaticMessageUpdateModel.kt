package net.dungeonhub.model.static_message

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateModel

class StaticMessageUpdateModel(
    var channelId: Long?,
    var messageId: Long?,
    var objectIds: List<Long>?,
    embedOverride: String?
) : UpdateModel<StaticMessageModel> {
    var embedOverride: String? = embedOverride
        set(value) {
            resetEmbedOverride = value == null
            field = value
        }
    var resetEmbedOverride = false
        private set

    fun toJson(): String {
        return MoshiService.moshi.adapter(StaticMessageUpdateModel::class.java).toJson(this)
    }
}
