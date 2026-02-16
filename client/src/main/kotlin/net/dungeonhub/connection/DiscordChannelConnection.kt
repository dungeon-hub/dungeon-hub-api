package net.dungeonhub.connection

import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.discord_channel.DiscordChannelCreationModel
import net.dungeonhub.model.discord_channel.DiscordChannelModel
import net.dungeonhub.model.discord_channel.DiscordChannelUpdateModel
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection
import java.util.concurrent.ConcurrentHashMap

@OptIn(ExperimentalStdlibApi::class)
class DiscordChannelConnection(private val server: Long, override val client: AuthenticatedClient) : AuthenticatedModuleConnection(client) {
    override val moduleApiPrefix = "server/$server/channel"

    suspend fun getById(id: Long): DiscordChannelModel? = dhApiRequest(id) {}

    suspend fun getByIdOrCreate(id: Long) : DiscordChannelModel? {
        return getById(id) ?: addNewChannel(DiscordChannelCreationModel(id, null))
    }

    suspend fun addNewChannel(creationModel: DiscordChannelCreationModel): DiscordChannelModel? {
        return dhApiRequest {
            method = HttpMethod.Post
            setBody(creationModel)
        }
    }

    suspend fun updateChannel(id: Long, updateModel: DiscordChannelUpdateModel): DiscordChannelModel? {
        return dhApiRequest(id) {
            method = HttpMethod.Put
            setBody(updateModel)
        }
    }

    suspend fun getAllChannels(): List<DiscordChannelModel>? = dhApiRequest("all") {}

    companion object {
        private val instances: MutableMap<Long, ClientlessDiscordChannelConnection> = ConcurrentHashMap()

        operator fun get(server: Long): ClientlessDiscordChannelConnection {
            return instances.computeIfAbsent(server) { ClientlessDiscordChannelConnection(it) }
        }

        class ClientlessDiscordChannelConnection(val server: Long) : ClientlessConnection<DiscordChannelConnection> {
            override fun authenticated(authenticationProvider: AuthenticationProvider): DiscordChannelConnection {
                return DiscordChannelConnection(server, AuthenticatedClient(authenticationProvider))
            }
        }
    }
}