package net.dungeonhub.model.static_message

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.ToJson
import net.dungeonhub.enums.StaticMessageType
import net.dungeonhub.model.discord_server.DiscordServerModel

class StaticMessageModelsJsonAdapter {
    @FromJson
    fun creationFromJson(json: CreationJson): StaticMessageCreationModel = StaticMessageCreationModel(
        json.channelId,
        json.messageId,
        json.staticMessageType,
        resolveObjects(json.staticMessageType, json.objects, json.objectIds),
        json.embedOverride
    )

    @ToJson
    fun creationToJson(model: StaticMessageCreationModel): CreationOutputJson = CreationOutputJson(
        model.channelId,
        model.messageId,
        model.staticMessageType,
        model.objects,
        model.embedOverride
    )

    @FromJson
    fun modelFromJson(json: ModelJson): StaticMessageModel = StaticMessageModel(
        json.id,
        json.server,
        json.channelId,
        json.messageId,
        json.staticMessageType,
        resolveObjects(json.staticMessageType, json.objects, json.objectIds),
        json.embedOverride,
        json.active
    )

    @ToJson
    fun modelToJson(model: StaticMessageModel): ModelOutputJson = ModelOutputJson(
        model.id,
        model.server,
        model.channelId,
        model.messageId,
        model.staticMessageType,
        model.objects,
        model.embedOverride,
        model.active
    )

    @FromJson
    fun updateFromJson(json: UpdateJson): StaticMessageUpdateModel {
        return StaticMessageUpdateModel(
            json.channelId,
            json.messageId,
            json.objects,
            json.embedOverride,
            json.active
        ).also {
            it.legacyObjectIds = json.objectIds.takeIf { ids -> json.objects == null && ids != null }
            it.setResetEmbedOverride(json.resetEmbedOverride)
        }
    }

    @ToJson
    fun updateToJson(model: StaticMessageUpdateModel): UpdateOutputJson {
        if (model.legacyObjectIds != null) {
            throw JsonDataException("Legacy objectIds must be resolved with validateFor before serialization")
        }
        return UpdateOutputJson(
            model.channelId,
            model.messageId,
            model.objects,
            model.embedOverride,
            model.active,
            model.resetEmbedOverride
        )
    }

    private fun resolveObjects(
        staticMessageType: StaticMessageType,
        objects: List<StaticMessageObject>?,
        objectIds: List<Long>?
    ): List<StaticMessageObject> {
        if (objects != null) return objects
        if (objectIds == null) return emptyList()
        if (objectIds.isEmpty()) return emptyList()

        val linkedObjectType = staticMessageType.linkedObjectType
            ?: throw JsonDataException("$staticMessageType does not support linked objects")
        return objectIds.map { StaticMessageObject.linkedObject(linkedObjectType, it) }
    }

    data class CreationJson(
        val channelId: Long,
        val messageId: Long?,
        val staticMessageType: StaticMessageType,
        val objects: List<StaticMessageObject>?,
        val objectIds: List<Long>?,
        val embedOverride: String?
    )

    data class CreationOutputJson(
        val channelId: Long,
        val messageId: Long?,
        val staticMessageType: StaticMessageType,
        val objects: List<StaticMessageObject>,
        val embedOverride: String?
    )

    data class ModelJson(
        val id: Long,
        val server: DiscordServerModel,
        val channelId: Long,
        val messageId: Long?,
        val staticMessageType: StaticMessageType,
        val objects: List<StaticMessageObject>?,
        val objectIds: List<Long>?,
        val embedOverride: String?,
        val active: Boolean
    )

    data class ModelOutputJson(
        val id: Long,
        val server: DiscordServerModel,
        val channelId: Long,
        val messageId: Long?,
        val staticMessageType: StaticMessageType,
        val objects: List<StaticMessageObject>,
        val embedOverride: String?,
        val active: Boolean
    )

    data class UpdateJson(
        val channelId: Long?,
        val messageId: Long?,
        val objects: List<StaticMessageObject>?,
        val objectIds: List<Long>?,
        val embedOverride: String?,
        val active: Boolean?,
        val resetEmbedOverride: Boolean = false
    )

    data class UpdateOutputJson(
        val channelId: Long?,
        val messageId: Long?,
        val objects: List<StaticMessageObject>?,
        val embedOverride: String?,
        val active: Boolean?,
        val resetEmbedOverride: Boolean
    )
}
