package net.dungeonhub.model.discord_channel

import net.dungeonhub.structure.model.UpdateModel

class DiscordChannelUpdateModel(
    var name: String?,
    var deleted: Boolean?
) : UpdateModel<DiscordChannelModel>