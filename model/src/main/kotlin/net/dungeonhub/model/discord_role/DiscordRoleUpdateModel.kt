package net.dungeonhub.model.discord_role

import net.dungeonhub.enums.RoleAction
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateModel

class DiscordRoleUpdateModel(
    nameSchema: String?,
    var roleAction: RoleAction?
) : UpdateModel<DiscordRoleModel> {
    var nameSchema = nameSchema
        set(value) {
            resetNameSchema = value == null
            field = value
        }

    var resetNameSchema = false
        private set

    fun toJson(): String {
        return MoshiService.moshi.adapter(DiscordRoleUpdateModel::class.java).toJson(this)
    }
}