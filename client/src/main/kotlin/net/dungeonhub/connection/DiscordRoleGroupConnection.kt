package net.dungeonhub.connection

import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.discord_role_group.DiscordRoleGroupModel
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection
import java.util.*

@OptIn(ExperimentalStdlibApi::class)
class DiscordRoleGroupConnection(server: Long, override val client: AuthenticatedClient) : AuthenticatedModuleConnection(client) {
    override val moduleApiPrefix = "server/$server/role-group"

    suspend fun getAll(): List<DiscordRoleGroupModel>? = dhApiRequest("all") {}

    companion object {
        private val instances: MutableMap<Long, ClientlessDiscordRoleGroupConnection> = HashMap()

        operator fun get(server: Long): ClientlessDiscordRoleGroupConnection {
            return instances.computeIfAbsent(server) { ClientlessDiscordRoleGroupConnection(it) }
        }

        class ClientlessDiscordRoleGroupConnection(val server: Long) : ClientlessConnection<DiscordRoleGroupConnection> {
            override fun authenticated(authenticationProvider: AuthenticationProvider): DiscordRoleGroupConnection {
                return DiscordRoleGroupConnection(server, AuthenticatedClient(authenticationProvider))
            }
        }
    }
}