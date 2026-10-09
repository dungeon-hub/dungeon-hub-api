package net.dungeonhub.model.static_message

import net.dungeonhub.enums.StaticMessageType
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.CreationModel

class StaticMessageCreationModel(
    val channelId: Long,
    val messageId: Long?,
    val staticMessageType: StaticMessageType,
    val objects: List<StaticMessageObject>,
    val embedOverride: String?
) : CreationModel {
    init {
        staticMessageType.validateObjects(objects)
    }

    fun toJson(): String {
        return MoshiService.moshi.adapter(StaticMessageCreationModel::class.java).toJson(this)
    }
}
