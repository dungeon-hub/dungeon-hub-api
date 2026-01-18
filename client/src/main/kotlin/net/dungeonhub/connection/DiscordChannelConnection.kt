package net.dungeonhub.connection

import com.squareup.moshi.adapter
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.discord_channel.DiscordChannelCreationModel
import net.dungeonhub.model.discord_channel.DiscordChannelModel
import net.dungeonhub.model.discord_channel.DiscordChannelUpdateModel
import net.dungeonhub.service.MoshiService.moshi
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection
import net.dungeonhub.structure.Connection.Companion.jsonMediaType
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.HashMap

@OptIn(ExperimentalStdlibApi::class)
class DiscordChannelConnection(private val server: Long, override val client: AuthenticatedClient) : AuthenticatedModuleConnection(client) {
    override val moduleApiPrefix = "server/$server/channel"

    fun getById(id: Long): DiscordChannelModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request) { json: String -> DiscordChannelModel.fromJson(json) }
    }

    fun getByIdOrCreate(id: Long) : DiscordChannelModel? {
        return getById(id) ?: addNewChannel(DiscordChannelCreationModel(id, null))
    }

    fun addNewChannel(creationModel: DiscordChannelCreationModel): DiscordChannelModel? {
        val url: HttpUrl = getApiUrl().build()

        val requestBody: RequestBody = creationModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .post(requestBody)
            .build()

        return executeRequest(request) { json: String -> DiscordChannelModel.fromJson(json) }
    }

    fun updateChannel(id: Long, updateModel: DiscordChannelUpdateModel): DiscordChannelModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val requestBody = updateModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .put(requestBody)
            .build()

        return executeRequest(request) { json: String -> DiscordChannelModel.fromJson(json) }
    }

    val allChannels: List<DiscordChannelModel>?
        get() {
            val url: HttpUrl = getApiUrl("all").build()

            val request: Request = getApiRequest(url).get().build()

            return executeRequest(request, function = moshi.adapter<List<DiscordChannelModel>>()::fromJson)
        }

    companion object {
        private val instances: MutableMap<Long, ClientlessDiscordChannelConnection> = HashMap()

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