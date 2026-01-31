package net.dungeonhub.connection

import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.warning.AddedWarningModel
import net.dungeonhub.model.warning.DetailedWarningModel
import net.dungeonhub.model.warning.WarningCreationModel
import net.dungeonhub.model.warning.WarningEvidenceCreationModel
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection

@OptIn(ExperimentalStdlibApi::class)
class WarningConnection(private val serverId: Long, override val client: AuthenticatedClient) : AuthenticatedModuleConnection(client) {
    override val moduleApiPrefix = "server/$serverId/warns"

    suspend fun getAllWarns(userId: Long): List<DetailedWarningModel>? {
        return dhApiRequest("all") {
            parameter("user", userId.toString())
        }
    }

    suspend fun getActiveWarns(userId: Long): List<DetailedWarningModel>? {
        return dhApiRequest("active") {
            parameter("user", userId.toString())
        }
    }

    suspend fun addWarning(creationModel: WarningCreationModel): AddedWarningModel? {
        return dhApiRequest {
            method = HttpMethod.Post
            setBody(creationModel)
        }
    }

    suspend fun deactivateWarning(id: Long): DetailedWarningModel? {
        return dhApiRequest(id) {
            method = HttpMethod.Delete
        }
    }

    suspend fun addEvidence(
        warningId: Long,
        evidenceCreationModel: WarningEvidenceCreationModel
    ): DetailedWarningModel? {
        return dhApiRequest("$warningId/evidence") {
            method = HttpMethod.Put
            setBody(evidenceCreationModel)
        }
    }

    companion object {
        private val instances: MutableMap<Long, ClientlessWarningConnection> = HashMap()

        operator fun get(serverId: Long): ClientlessWarningConnection {
            return instances.computeIfAbsent(serverId) { ClientlessWarningConnection(it) }
        }

        class ClientlessWarningConnection(val serverId: Long) : ClientlessConnection<WarningConnection> {
            override fun authenticated(authenticationProvider: AuthenticationProvider): WarningConnection {
                return WarningConnection(serverId, AuthenticatedClient(authenticationProvider))
            }
        }
    }
}