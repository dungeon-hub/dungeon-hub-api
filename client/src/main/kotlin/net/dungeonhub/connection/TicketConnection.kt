package net.dungeonhub.connection

import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.model.ticket.TicketCreationModel
import net.dungeonhub.model.ticket.TicketModel
import net.dungeonhub.model.ticket.TicketUpdateModel
import net.dungeonhub.model.ticket_panel.TicketPanelModel
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection
import java.util.HashMap

@OptIn(ExperimentalStdlibApi::class)
class TicketConnection(private val server: Long, private val ticketPanel: Long, override val client: AuthenticatedClient) : AuthenticatedModuleConnection(client) {
    override val moduleApiPrefix = "server/$server/ticket-panel/$ticketPanel/ticket"

    suspend fun getById(id: Long): TicketModel? = dhApiRequest(id) {}

    suspend fun addNewTicket(creationModel: TicketCreationModel): TicketModel? = dhApiRequest {
        method = HttpMethod.Post
        setBody(creationModel)
    }

    suspend fun updateTicket(id: Long, updateModel: TicketUpdateModel): TicketModel? = dhApiRequest(id) {
        method = HttpMethod.Put
        setBody(updateModel)
    }

    suspend fun getAllTickets(): List<TicketModel>? = dhApiRequest("all") {  }

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