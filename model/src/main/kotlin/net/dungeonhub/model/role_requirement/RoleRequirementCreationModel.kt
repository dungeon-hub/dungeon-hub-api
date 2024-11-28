package net.dungeonhub.model.role_requirement

import net.dungeonhub.enums.RoleRequirementComparison
import net.dungeonhub.enums.RoleRequirementType
import net.dungeonhub.model.discord_role.DiscordRoleModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.CreationModel

class RoleRequirementCreationModel(
    val discordRole: Long,
    val requirementType: RoleRequirementType,
    val comparison: RoleRequirementComparison,
    val count: Int,
    val extraData: String?
) : CreationModel {
    fun toJson(): String {
        return MoshiService.moshi.adapter(RoleRequirementCreationModel::class.java).toJson(this)
    }
}