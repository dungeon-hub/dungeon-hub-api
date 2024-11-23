package net.dungeonhub.model.role_requirement

import net.dungeonhub.enums.RoleRequirementComparison
import net.dungeonhub.enums.RoleRequirementType
import net.dungeonhub.model.discord_role.DiscordRoleModel
import net.dungeonhub.structure.model.Model

class RoleRequirementModel(
    val id: Long,
    val discordRole: DiscordRoleModel,
    val requirementType: RoleRequirementType,
    val comparison: RoleRequirementComparison,
    val count: Int,
    val extraData: String
) : Model