package net.dungeonhub.model.role_requirement

import net.dungeonhub.enums.RoleRequirementComparison
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateModel

class RoleRequirementUpdateModel(
    val comparison: RoleRequirementComparison?,
    val count: Int?,
    extraData: String?
) : UpdateModel<RoleRequirementModel> {
    var extraData = extraData
        set(value) {
            resetExtraData = value == null
            field = value
        }

    var resetExtraData = false
        private set

    fun toJson(): String {
        return MoshiService.moshi.adapter(RoleRequirementUpdateModel::class.java).toJson(this)
    }
}