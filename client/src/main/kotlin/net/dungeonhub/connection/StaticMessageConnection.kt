package net.dungeonhub.connection

import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.static_message.StaticMessageModel
import net.dungeonhub.enums.StaticMessageType
import net.dungeonhub.model.static_message.StaticMessageCreationModel
import net.dungeonhub.model.static_message.StaticMessageUpdateModel
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection

@OptIn(ExperimentalStdlibApi::class)
class StaticMessageConnection(private val server: Long, override val client: AuthenticatedClient) : AuthenticatedModuleConnection(client) {
    override val moduleApiPrefix: String = "server/$server/static-message"

    suspend fun getById(id: Long): StaticMessageModel? = dhApiRequest(id) {}

    suspend fun findStaticMessages(staticMessageType: StaticMessageType? = null, channelId: Long? = null, messageId: Long? = null): List<StaticMessageModel>? =
        dhApiRequest("find") {
            if (staticMessageType != null) {
                parameter("staticMessageType", staticMessageType.name)
            }
            if (channelId != null) {
                parameter("channelId", channelId)
            }
            if (messageId != null) {
                parameter("messageId", messageId)
            }
        }

    suspend fun createStaticMessage(creationModel: StaticMessageCreationModel): StaticMessageModel? = dhApiRequest {
        method = HttpMethod.Post
        setBody(creationModel)
    }

    suspend fun updateStaticMessage(id: Long, updateModel: StaticMessageUpdateModel): StaticMessageModel? = dhApiRequest(id) {
        method = HttpMethod.Put
        setBody(updateModel)
    }

    suspend fun deleteStaticMessage(id: Long): StaticMessageModel? = dhApiRequest(id) {
        method = HttpMethod.Delete
    }

    companion object {
        private val instances: MutableMap<Long, ClientlessStaticMessageConnection> = HashMap()

        operator fun get(server: Long): ClientlessStaticMessageConnection {
            return instances.computeIfAbsent(server) { ClientlessStaticMessageConnection(it) }
        }

        class ClientlessStaticMessageConnection(private val server: Long) : ClientlessConnection<StaticMessageConnection> {
            override fun authenticated(authenticationProvider: AuthenticationProvider): StaticMessageConnection {
                return StaticMessageConnection(server, AuthenticatedClient(authenticationProvider))
            }
        }
    }
}
