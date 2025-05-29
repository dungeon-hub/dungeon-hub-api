package net.dungeonhub.connection

import com.squareup.moshi.adapter
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.warning.AddedWarningModel
import net.dungeonhub.model.warning.DetailedWarningModel
import net.dungeonhub.model.warning.WarningCreationModel
import net.dungeonhub.model.warning.WarningEvidenceCreationModel
import net.dungeonhub.service.MoshiService.moshi
import net.dungeonhub.structure.ClientlessConnection
import net.dungeonhub.structure.ModuleConnection
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@OptIn(ExperimentalStdlibApi::class)
class WarningConnection(private val serverId: Long, override val client: AuthenticatedClient) : ModuleConnection {
    override val moduleApiPrefix = "server/$serverId/warns"

    fun getAllWarns(userId: Long): List<DetailedWarningModel>? {
        val url: HttpUrl = getApiUrl("all")
            .addQueryParameter("user", userId.toString())
            .build()

        val request: Request = getApiRequest(url).get().build()

        return executeRequest(request, function = moshi.adapter<List<DetailedWarningModel>>()::fromJson)
    }

    fun getActiveWarns(userId: Long): List<DetailedWarningModel>? {
        val url: HttpUrl = getApiUrl("active")
            .addQueryParameter("user", userId.toString())
            .build()

        val request: Request = getApiRequest(url).get().build()

        return executeRequest(request, function = moshi.adapter<List<DetailedWarningModel>>()::fromJson)
    }

    fun addWarning(creationModel: WarningCreationModel): AddedWarningModel? {
        val url: HttpUrl = getApiUrl().build()

        val requestBody = creationModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url).post(requestBody).build()

        return executeRequest(request) { json: String -> AddedWarningModel.fromJson(json) }
    }

    fun deactivateWarning(id: Long): DetailedWarningModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val request: Request = getApiRequest(url).delete().build()

        return executeRequest(request) { json: String -> DetailedWarningModel.fromJson(json) }
    }

    fun addEvidence(
        warningId: Long,
        evidenceCreationModel: WarningEvidenceCreationModel
    ): DetailedWarningModel? {
        val url: HttpUrl = getApiUrl("$warningId/evidence").build()

        val requestBody = evidenceCreationModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url).put(requestBody).build()

        return executeRequest(request) { json: String -> DetailedWarningModel.fromJson(json) }
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