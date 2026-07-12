package net.dungeonhub.connection

 import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.model.discord_user.DiscordUserModel
import net.dungeonhub.model.reputation.ReputationCreationModel
import net.dungeonhub.model.reputation.ReputationModel
import net.dungeonhub.model.reputation.ReputationUpdateModel
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection
import java.util.concurrent.ConcurrentHashMap

class ReputationConnection(server: Long, discordUser: Long, override val client: AuthenticatedClient) :
    AuthenticatedModuleConnection(client) {
    override val moduleApiPrefix = "server/$server/discord-user/$discordUser/reputation"

    suspend fun calculateReputation(): Long? = dhApiRequest("calculate") {}

    suspend fun addReputation(creationModel: ReputationCreationModel): ReputationModel? = dhApiRequest {
        method = HttpMethod.Post
        setBody(creationModel)
    }

    suspend fun updateReputation(id: Long, updateModel: ReputationUpdateModel): ReputationModel? = dhApiRequest(id) {
        method = HttpMethod.Put
        setBody(updateModel)
    }

    suspend fun getReputations(): List<ReputationModel>? = dhApiRequest("all") {}

    companion object {
        private val instances: MutableMap<Long, MutableMap<Long, ClientlessReputationConnection>> = ConcurrentHashMap()

        operator fun get(server: Long, discordUser: Long): ClientlessReputationConnection {
            return instances.computeIfAbsent(server) {
                mutableMapOf(discordUser to ClientlessReputationConnection(it, discordUser))
            }.computeIfAbsent(discordUser) { ClientlessReputationConnection(server, it) }
        }

        operator fun get(server: DiscordServerModel, discordUser: DiscordUserModel): ClientlessReputationConnection {
            return get(server.id, discordUser.id)
        }

        class ClientlessReputationConnection(val server: Long, val discordUser: Long) :
            ClientlessConnection<ReputationConnection> {
            override fun authenticated(authenticationProvider: AuthenticationProvider): ReputationConnection {
                return ReputationConnection(server, discordUser, AuthenticatedClient(authenticationProvider))
            }
        }
    }
}