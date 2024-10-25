package net.dungeonhub.model.discord_server

import net.dungeonhub.structure.model.CreationModel
import net.dungeonhub.structure.model.Model
import net.dungeonhub.structure.model.UpdateModel
import net.dungeonhub.service.MoshiService

class DiscordServerModel(
    val id: Long
) : Model, CreationModel, UpdateModel<DiscordServerModel> {
    companion object {
        fun fromJson(json: String): DiscordServerModel {
            return MoshiService.moshi.adapter(DiscordServerModel::class.java).fromJson(json)!!
        }
    }
}