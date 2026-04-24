package net.dungeonhub.connection

import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.cnt_request.CntRequestCreationModel
import net.dungeonhub.model.cnt_request.CntRequestPageModel
import net.dungeonhub.model.cnt_request.CntRequestModel
import net.dungeonhub.model.cnt_request.CntRequestUpdateModel
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection
import java.util.concurrent.ConcurrentHashMap

@OptIn(ExperimentalStdlibApi::class)
class CntRequestConnection(private val server: Long, override val client: AuthenticatedClient) : AuthenticatedModuleConnection(client) {
    override val moduleApiPrefix = "server/$server/cnt-request"

    suspend fun findCntRequests(messageId: Long): List<CntRequestModel>? {
        return dhApiRequest("find") {
            parameter("message-id", messageId)
        }
    }

    suspend fun findCntRequestsByUser(userId: Long): List<CntRequestModel>? {
        return dhApiRequest("find") {
            parameter("user", userId)
        }
    }

    suspend fun getCntRequests(page: Int = 0): CntRequestPageModel? {
        return dhApiRequest("all") {
            parameter("page", page.toString())
        }
    }

    suspend fun createCntRequest(creationModel: CntRequestCreationModel): CntRequestModel? {
        return dhApiRequest {
            method = HttpMethod.Post
            setBody(creationModel)
        }
    }

    suspend fun updateCntRequest(id: Long, updateModel: CntRequestUpdateModel): CntRequestModel? {
        return dhApiRequest(id) {
            method = HttpMethod.Put
            setBody(updateModel)
        }
    }

    companion object {
        private val instances: MutableMap<Long, ClientlessCntRequestConnection> = ConcurrentHashMap()

        operator fun get(server: Long): ClientlessCntRequestConnection {
            return instances.computeIfAbsent(server) { ClientlessCntRequestConnection(it) }
        }

        class ClientlessCntRequestConnection(val server: Long) : ClientlessConnection<CntRequestConnection> {
            override fun authenticated(authenticationProvider: AuthenticationProvider): CntRequestConnection {
                return CntRequestConnection(server, AuthenticatedClient(authenticationProvider))
            }
        }
    }
}