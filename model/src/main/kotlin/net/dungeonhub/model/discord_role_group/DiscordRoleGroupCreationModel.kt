package net.dungeonhub.model.discord_role_group

import net.dungeonhub.model.discord_role.DiscordRoleModel
import net.dungeonhub.structure.model.CreationModel

class DiscordRoleGroupCreationModel(
    var discordRole: DiscordRoleModel,
    var roleGroup: DiscordRoleModel
) : CreationModel