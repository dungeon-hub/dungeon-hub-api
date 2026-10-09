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
    internal var legacyObjectIds: List<Long>? = null

    var embedOverride: String? = embedOverride
        set(value) {
            resetEmbedOverride = value == null
            field = value
        }
    var resetEmbedOverride = false
        private set

    internal fun setResetEmbedOverride(reset: Boolean) {
        resetEmbedOverride = reset
    }

    fun toJson(): String {
        return MoshiService.moshi.adapter(StaticMessageUpdateModel::class.java).toJson(this)
    }

    /** Validates object changes once the target static message type is known. */
    fun validateFor(staticMessageType: StaticMessageType): StaticMessageUpdateModel {
        legacyObjectIds?.let { ids ->
            if (ids.isEmpty()) {
                objects = emptyList()
                legacyObjectIds = null
                return@let
            }
            val linkedObjectType = requireNotNull(staticMessageType.linkedObjectType) {
                "$staticMessageType does not support linked objects"
            }
            objects = ids.map { StaticMessageObject.linkedObject(linkedObjectType, it) }
            legacyObjectIds = null
        }
        objects?.let(staticMessageType::validateObjects)
        return this
    }
}
