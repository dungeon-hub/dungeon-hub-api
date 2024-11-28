package net.dungeonhub.model.role_requirement

import net.dungeonhub.enums.RoleRequirementComparison
import net.dungeonhub.enums.RoleRequirementType
import net.dungeonhub.model.discord_role.DiscordRoleModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateableModel

class RoleRequirementModel(
    val id: Long,
    val discordRole: DiscordRoleModel,
    val requirementType: RoleRequirementType,
    val comparison: RoleRequirementComparison,
    val count: Int,
    val extraData: String?
) : UpdateableModel<RoleRequirementUpdateModel, RoleRequirementModel> {
    fun checkExtraData(value: String?): Boolean {
        return requirementType.extraDataType.checkExtraData(value)
    }

    override fun getUpdateModel(): RoleRequirementUpdateModel {
        return RoleRequirementUpdateModel(null, null, null)
    }

    companion object {
        fun fromJson(json: String): RoleRequirementModel {
            return MoshiService.moshi.adapter(RoleRequirementModel::class.java).fromJson(json)!!
        }
    }
}