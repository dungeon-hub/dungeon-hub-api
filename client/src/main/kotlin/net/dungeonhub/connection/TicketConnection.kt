package net.dungeonhub.connection

import com.squareup.moshi.adapter
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.model.ticket.TicketCreationModel
import net.dungeonhub.model.ticket.TicketModel
import net.dungeonhub.model.ticket.TicketUpdateModel
import net.dungeonhub.model.ticket_panel.TicketPanelModel
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
class TicketConnection(private val server: Long, private val ticketPanel: Long, override val client: AuthenticatedClient) : ModuleConnection {
    override val moduleApiPrefix = "server/$server/ticket-panel/$ticketPanel/ticket"

    fun getById(id: Long): TicketModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request) { json: String -> TicketModel.fromJson(json) }
    }

    fun addNewTicket(creationModel: TicketCreationModel): TicketModel? {
        val url: HttpUrl = getApiUrl().build()

        val requestBody: RequestBody = creationModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .post(requestBody)
            .build()

        return executeRequest(request) { json: String -> TicketModel.fromJson(json) }
    }

    fun updateTicket(id: Long, updateModel: TicketUpdateModel): TicketModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val requestBody = updateModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .put(requestBody)
            .build()

        return executeRequest(request) { json: String -> TicketModel.fromJson(json) }
    }

    val allTickets: List<TicketModel>?
        get() {
            val url: HttpUrl = getApiUrl("all").build()

            val request: Request = getApiRequest(url).get().build()

            return executeRequest(request, function = moshi.adapter<List<TicketModel>>()::fromJson)
        }

    companion object {
        private val instances: MutableMap<Long, MutableMap<Long, ClientlessTicketConnection>> = HashMap()

        operator fun get(server: Long, ticketPanel: TicketPanelModel): ClientlessTicketConnection {
            return instances.computeIfAbsent(server) {
                mutableMapOf(ticketPanel.id to ClientlessTicketConnection(it, ticketPanel.id))
            }.computeIfAbsent(ticketPanel.id) { ClientlessTicketConnection(server, it) }
        }

        operator fun get(server: DiscordServerModel, ticketPanel: TicketPanelModel): ClientlessTicketConnection {
            return get(server.id, ticketPanel)
        }

        class ClientlessTicketConnection(val server: Long, val ticketPanel: Long) : ClientlessConnection<TicketConnection> {
            override fun authenticated(authenticationProvider: AuthenticationProvider): TicketConnection {
                return TicketConnection(server, ticketPanel, AuthenticatedClient(authenticationProvider))
            }
        }
    }
}