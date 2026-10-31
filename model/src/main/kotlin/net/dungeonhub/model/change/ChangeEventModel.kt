package net.dungeonhub.model.change

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import net.dungeonhub.enums.ChangeAction
import net.dungeonhub.enums.ChangeEventType
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.Model

/**
 * A single change that is pushed to clients through the change stream.
 *
 * @param type the type of the changed entity, this also determines the class of [data]
 * @param action what happened to the entity
 * @param serverId the discord server the entity belongs to, or null if it doesn't belong to a specific server
 * @param data the entity after the change, or the last known state of it if it was deleted
 */
class ChangeEventModel<T : Model>(
    val type: ChangeEventType,
    val action: ChangeAction,
    val serverId: Long?,
    val data: T
) : Model {
    companion object {
        fun fromJson(type: ChangeEventType, json: String): ChangeEventModel<out Model> {
            return adapter(type).fromJson(json)!!
        }

        private fun adapter(type: ChangeEventType): JsonAdapter<ChangeEventModel<out Model>> {
            return MoshiService.moshi.adapter(
                Types.newParameterizedType(ChangeEventModel::class.java, type.modelClass)
            )
        }
    }
}
