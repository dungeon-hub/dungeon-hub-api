package net.dungeonhub.connection

import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.model.role_requirement.RoleRequirementCreationModel
import net.dungeonhub.model.role_requirement.RoleRequirementModel
import net.dungeonhub.model.role_requirement.RoleRequirementUpdateModel
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection

class RoleRequirementConnection(server: Long, override val client: AuthenticatedClient) : AuthenticatedModuleConnection(client) {
    override val moduleApiPrefix = "server/$server/role-requirement"

    suspend fun getById(id: Long): RoleRequirementModel? = dhApiRequest(id) {}

    suspend fun getAllRoleRequirements(): List<RoleRequirementModel>? = dhApiRequest("all") {}

    //TODO dedicated endpoint?
    suspend fun getByRoleId(id: Long): RoleRequirementModel? {
        return getAllRoleRequirements()?.firstOrNull { roleRequirement: RoleRequirementModel ->
            roleRequirement.discordRole.id == id
        }
    }

    suspend fun addNewRoleRequirement(creationModel: RoleRequirementCreationModel): RoleRequirementModel? = dhApiRequest {
        method = HttpMethod.Post
        setBody(creationModel)
    }

    suspend fun updateRoleRequirement(id: Long, updateModel: RoleRequirementUpdateModel): RoleRequirementModel? =
        dhApiRequest(id) {
            method = HttpMethod.Put
            setBody(updateModel)
        }

    suspend fun deleteRoleRequirement(roleRequirement: RoleRequirementModel): RoleRequirementModel? = dhApiRequest(roleRequirement.id) {
        method = HttpMethod.Delete
    }

    companion object {
        private val instances: MutableMap<Long, ClientlessRoleRequirementConnection> = HashMap()

        operator fun get(server: Long): ClientlessRoleRequirementConnection {
            return instances.computeIfAbsent(server) { ClientlessRoleRequirementConnection(it) }
        }

        operator fun get(server: DiscordServerModel): ClientlessRoleRequirementConnection {
            return get(server.id)
        }

        class ClientlessRoleRequirementConnection(val server: Long) : ClientlessConnection<RoleRequirementConnection> {
            override fun authenticated(authenticationProvider: AuthenticationProvider): RoleRequirementConnection {
                return RoleRequirementConnection(server, AuthenticatedClient(authenticationProvider))
            }
        }
    }
}