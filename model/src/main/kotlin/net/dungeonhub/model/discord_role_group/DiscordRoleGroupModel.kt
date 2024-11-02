package net.dungeonhub.model.discord_role_group

import net.dungeonhub.structure.model.Model
import net.dungeonhub.model.discord_role.DiscordRoleModel

class DiscordRoleGroupModel(
    val id: Long,
    val discordRole: DiscordRoleModel,
    val roleGroup: DiscordRoleModel
) : Model