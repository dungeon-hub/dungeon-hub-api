package net.dungeonhub.connection

import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.model.ticket_panel.TicketPanelCreationModel
import net.dungeonhub.model.ticket_panel.TicketPanelModel
import net.dungeonhub.model.ticket_panel.TicketPanelUpdateModel
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection
import java.util.HashMap

@OptIn(ExperimentalStdlibApi::class)
class TicketPanelConnection(private val server: Long, override val client: AuthenticatedClient) : AuthenticatedModuleConnection(client) {
    override val moduleApiPrefix = "server/$server/ticket-panel"

    suspend fun getById(id: Long): TicketPanelModel? = dhApiRequest(id) {}

    suspend fun addNewTicketPanel(creationModel: TicketPanelCreationModel): TicketPanelModel? = dhApiRequest {
        method = HttpMethod.Post
        setBody(creationModel)
    }

    suspend fun updateTicketPanel(id: Long, updateModel: TicketPanelUpdateModel): TicketPanelModel? = dhApiRequest(id) {
        method = HttpMethod.Put
        setBody(updateModel)
    }

    suspend fun getAllTicketPanels(): List<TicketPanelModel>? = dhApiRequest("all") {}

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