package net.dungeonhub.connection

import com.squareup.moshi.adapter
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.model.ticket_panel.TicketPanelCreationModel
import net.dungeonhub.model.ticket_panel.TicketPanelModel
import net.dungeonhub.model.ticket_panel.TicketPanelUpdateModel
import net.dungeonhub.service.MoshiService.moshi
import net.dungeonhub.structure.ClientlessConnection
import net.dungeonhub.structure.Connection.Companion.jsonMediaType
import net.dungeonhub.structure.ModuleConnection
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.HashMap

@OptIn(ExperimentalStdlibApi::class)
class TicketPanelConnection(private val server: Long, override val client: AuthenticatedClient) : ModuleConnection {
    override val moduleApiPrefix = "server/$server/ticket-panel"

    fun getById(id: Long): TicketPanelModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request) { json: String -> TicketPanelModel.fromJson(json) }
    }

    fun addNewTicketPanel(creationModel: TicketPanelCreationModel): TicketPanelModel? {
        val url: HttpUrl = getApiUrl().build()

        val requestBody: RequestBody = creationModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .post(requestBody)
            .build()

        return executeRequest(request) { json: String -> TicketPanelModel.fromJson(json) }
    }

    fun updateTicketPanel(id: Long, updateModel: TicketPanelUpdateModel): TicketPanelModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val requestBody = updateModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .put(requestBody)
            .build()

        return executeRequest(request) { json: String -> TicketPanelModel.fromJson(json) }
    }

    val allTicketPanels: List<TicketPanelModel>?
        get() {
            val url: HttpUrl = getApiUrl("all").build()

            val request: Request = getApiRequest(url).get().build()

            return executeRequest(request, function = moshi.adapter<List<TicketPanelModel>>()::fromJson)
        }

    companion object {
        private val instances: MutableMap<Long, ClientlessTicketPanelConnection> = HashMap()

        operator fun get(server: Long): ClientlessTicketPanelConnection {
            return instances.computeIfAbsent(server) { ClientlessTicketPanelConnection(it) }
        }

        operator fun get(server: DiscordServerModel): ClientlessTicketPanelConnection {
            return get(server.id)
        }

        class ClientlessTicketPanelConnection(val server: Long) : ClientlessConnection<TicketPanelConnection> {
            override fun authenticated(authenticationProvider: AuthenticationProvider): TicketPanelConnection {
                return TicketPanelConnection(server, AuthenticatedClient(authenticationProvider))
            }
        }
    }
}