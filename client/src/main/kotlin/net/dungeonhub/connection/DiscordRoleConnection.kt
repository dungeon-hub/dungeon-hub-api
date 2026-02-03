package net.dungeonhub.connection

import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.discord_role.DiscordRoleCreationModel
import net.dungeonhub.model.discord_role.DiscordRoleModel
import net.dungeonhub.model.discord_role.DiscordRoleUpdateModel
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection
import java.util.*

@OptIn(ExperimentalStdlibApi::class)
class DiscordRoleConnection(private val server: Long, override val client: AuthenticatedClient) : AuthenticatedModuleConnection(client) {
    override val moduleApiPrefix = "server/$server/roles"

    suspend fun getById(id: Long): DiscordRoleModel? {
        return dhApiRequest(id) {}
    }

    suspend fun addNewRole(creationModel: DiscordRoleCreationModel): DiscordRoleModel? {
        return dhApiRequest {
            method = HttpMethod.Post
            setBody(creationModel)
        }
    }

    suspend fun updateRole(id: Long, updateModel: DiscordRoleUpdateModel): DiscordRoleModel? {
        return dhApiRequest(id) {
            method = HttpMethod.Put
            setBody(updateModel)
        }
    }

    suspend fun getAllRoles(): List<DiscordRoleModel>? = dhApiRequest("all") {}

    companion object {
        private val instances: MutableMap<Long, ClientlessDiscordRoleConnection> = HashMap()

        operator fun get(server: Long): ClientlessDiscordRoleConnection {
            return instances.computeIfAbsent(server) { ClientlessDiscordRoleConnection(it) }
        }

        class ClientlessDiscordRoleConnection(val server: Long) : ClientlessConnection<DiscordRoleConnection> {
            override fun authenticated(authenticationProvider: AuthenticationProvider): DiscordRoleConnection {
                return DiscordRoleConnection(server, AuthenticatedClient(authenticationProvider))
            }
        }
    }
}