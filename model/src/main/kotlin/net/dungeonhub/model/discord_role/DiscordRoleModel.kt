package net.dungeonhub.model.discord_role

import net.dungeonhub.enums.RoleAction
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateableModel

class DiscordRoleModel(
    val id: Long,
    val nameSchema: String?,
    val roleAction: RoleAction,
    val discordServerModel: DiscordServerModel
) : UpdateableModel<DiscordRoleUpdateModel, DiscordRoleModel> {
    companion object {
        fun fromJson(json: String): DiscordRoleModel {
            return MoshiService.moshi.adapter(DiscordRoleModel::class.java).fromJson(json)!!
        }
    }

    fun shouldBeAdded(verified: Boolean): Boolean {
        return if (verified) {
            RoleAction.applyWhenVerified.contains(roleAction)
        } else {
            RoleAction.applyWhenUnverified.contains(roleAction)
        }
    }

    fun shouldBeRemoved(verified: Boolean): Boolean {
        return if (verified) {
            RoleAction.removeWhenVerified.contains(roleAction)
        } else {
            RoleAction.removeWhenUnverified.contains(roleAction)
        }
    }

    override fun getUpdateModel(): DiscordRoleUpdateModel {
        return DiscordRoleUpdateModel(null, null)
    }
}