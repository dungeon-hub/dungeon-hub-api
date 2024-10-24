package net.dungeonhub.model.discord_server

import net.dungeonhub.entity.model.CreationModel
import net.dungeonhub.entity.model.Model
import net.dungeonhub.entity.model.UpdateModel
import net.dungeonhub.service.MoshiService

class DiscordServerModel(
    val id: Long
) : Model, CreationModel, UpdateModel<DiscordServerModel> {
    override fun apply(model: DiscordServerModel): DiscordServerModel {
        return model
    }

    companion object {
        fun fromJson(json: String): DiscordServerModel {
            return MoshiService.moshi.adapter(DiscordServerModel::class.java).fromJson(json)!!
        }
    }
}