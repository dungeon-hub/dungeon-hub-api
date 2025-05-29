package net.dungeonhub.connection

import com.squareup.moshi.adapter
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.discord_role_group.DiscordRoleGroupModel
import net.dungeonhub.service.MoshiService.moshi
import net.dungeonhub.structure.ClientlessConnection
import net.dungeonhub.structure.ModuleConnection
import okhttp3.HttpUrl
import okhttp3.Request
import java.util.*

@OptIn(ExperimentalStdlibApi::class)
class DiscordRoleGroupConnection(server: Long, override val client: AuthenticatedClient) : ModuleConnection {
    override val moduleApiPrefix = "server/$server/role-group"

    val all: List<DiscordRoleGroupModel>?
        get() {
            val url: HttpUrl = getApiUrl("all").build()

            val request: Request = getApiRequest(url).get().build()

            return executeRequest(request, function = moshi.adapter<List<DiscordRoleGroupModel>>()::fromJson)
        }

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