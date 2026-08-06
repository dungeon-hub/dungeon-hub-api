package net.dungeonhub.model.static_message

import net.dungeonhub.enums.StaticMessageType
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateModel

class StaticMessageUpdateModel(
    var channelId: Long?,
    var messageId: Long?,
    var objects: List<StaticMessageObject>?,
    embedOverride: String?,
    var active: Boolean?
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

    /** Validates object changes once the target static message type is known. */
    fun validateFor(staticMessageType: StaticMessageType): StaticMessageUpdateModel {
        objects?.let(staticMessageType::validateObjects)
        return this
    }
}
