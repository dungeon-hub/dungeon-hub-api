package net.dungeonhub.connection

import com.squareup.moshi.adapter
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.cnt_request.CntRequestCreationModel
import net.dungeonhub.model.cnt_request.CntRequestModel
import net.dungeonhub.model.cnt_request.CntRequestUpdateModel
import net.dungeonhub.service.MoshiService.moshi
import net.dungeonhub.structure.ClientlessConnection
import net.dungeonhub.structure.ModuleConnection
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@OptIn(ExperimentalStdlibApi::class)
class CntRequestConnection(private val server: Long, override val client: AuthenticatedClient) : ModuleConnection {
    override val moduleApiPrefix = "server/$server/cnt-request"

    fun findCntRequests(messageId: Long): List<CntRequestModel>? {
        val url: HttpUrl = getApiUrl("find")
            .addQueryParameter("message-id", messageId.toString())
            .build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request, function = moshi.adapter<List<CntRequestModel>>()::fromJson)
    }

    fun findCntRequestsByUser(userId: Long): List<CntRequestModel>? {
        val url: HttpUrl = getApiUrl("find")
            .addQueryParameter("user", userId.toString())
            .build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request, function = moshi.adapter<List<CntRequestModel>>()::fromJson)
    }

    fun createCntRequest(creationModel: CntRequestCreationModel): CntRequestModel? {
        val url: HttpUrl = getApiUrl().build()

        val requestBody = creationModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .post(requestBody)
            .build()

        return executeRequest(request) { json: String -> CntRequestModel.fromJson(json) }
    }

    fun updateCntRequest(id: Long, updateModel: CntRequestUpdateModel): CntRequestModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val requestBody: RequestBody = updateModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .put(requestBody)
            .build()

        return executeRequest(request) { json: String -> CntRequestModel.fromJson(json) }
    }

    companion object {
        private val instances: MutableMap<Long, ClientlessCntRequestConnection> = HashMap()

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