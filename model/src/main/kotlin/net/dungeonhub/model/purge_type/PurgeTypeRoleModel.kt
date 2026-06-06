package net.dungeonhub.model.purge_type

import net.dungeonhub.structure.model.Model
import net.dungeonhub.model.discord_role.DiscordRoleModel

class PurgeTypeRoleModel(
    val purgeTypeModel: SimplePurgeTypeModel,
    val discordRoleModel: DiscordRoleModel
) : Model