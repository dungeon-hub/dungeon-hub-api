package net.dungeonhub.connection

import com.squareup.moshi.adapter
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.static_message.StaticMessageModel
import net.dungeonhub.enums.StaticMessageType
import net.dungeonhub.model.static_message.StaticMessageUpdateModel
import net.dungeonhub.service.MoshiService.moshi
import net.dungeonhub.structure.ClientlessConnection
import net.dungeonhub.structure.ModuleConnection
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@OptIn(ExperimentalStdlibApi::class)
class StaticMessageConnection(private val server: Long, override val client: AuthenticatedClient) : ModuleConnection {
    override val moduleApiPrefix: String = "server/$server/static-message"

    fun getById(id: Long): StaticMessageModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val request: Request = getApiRequest(url).get().build()

        return executeRequest(request, function = moshi.adapter<StaticMessageModel>()::fromJson)
    }

    fun findAll(): List<StaticMessageModel>? = findStaticMessages(null, null)

    fun findStaticMessages(staticMessageType: StaticMessageType?, channelId: Long?): List<StaticMessageModel>? {
        var url = getApiUrl("find")

        if (staticMessageType != null) {
            url = url.addQueryParameter("staticMessageType", staticMessageType.name)
        }

        if (channelId != null) {
            url = url.addQueryParameter("channelId", channelId.toString())
        }

        val request: Request = getApiRequest(url.build()).get().build()

        return executeRequest(request, function = moshi.adapter<List<StaticMessageModel>>()::fromJson)
    }

    fun createStaticMessage(creationModel: net.dungeonhub.model.static_message.StaticMessageCreationModel): StaticMessageModel? {
        val url: HttpUrl = getApiUrl().build()

        val request = getApiRequest(url)
            .post(creationModel.toJson().toRequestBody(jsonMediaType))
            .build()

        return executeRequest(request, function = moshi.adapter<StaticMessageModel>()::fromJson)
    }

    fun updateStaticMessage(id: Long, updateModel: StaticMessageUpdateModel): StaticMessageModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val request = getApiRequest(url)
            .put(updateModel.toJson().toRequestBody(jsonMediaType))
            .build()

        return executeRequest(request, function = moshi.adapter<StaticMessageModel>()::fromJson)
    }

    fun deleteStaticMessage(id: Long): StaticMessageModel? {
        val url: HttpUrl = getApiUrl("$id").build()

        val request: Request = getApiRequest(url).delete().build()

        return executeRequest(request, function = moshi.adapter<StaticMessageModel>()::fromJson)
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
