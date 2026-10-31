package net.dungeonhub.model.change

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.Model

/**
 * The first event of every change stream connection, sent with the event name [EVENT_NAME].
 *
 * @param resumed true if the client sent the id of the last event it received and every change it missed since then
 * was sent again. If false, changes might have been missed, so the client should reload the state it needs.
 */
class ChangeStreamConnectedModel(
    val resumed: Boolean
) : Model {
    companion object {
        const val EVENT_NAME = "Connected"

        fun fromJson(json: String): ChangeStreamConnectedModel {
            return MoshiService.moshi.adapter(ChangeStreamConnectedModel::class.java).fromJson(json)!!
        }
    }
}
