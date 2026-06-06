package net.dungeonhub.connection

import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.carry_type.CarryTypeModel
import net.dungeonhub.model.purge_type.PurgeTypeModel
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection
import java.util.concurrent.ConcurrentHashMap

class PurgeTypeConnection(carryTypeModel: CarryTypeModel, override val client: AuthenticatedClient) : AuthenticatedModuleConnection(client) {
    override val moduleApiPrefix = "server/${carryTypeModel.server.id}/carry-type/${carryTypeModel.id}/purge-type"

    //TODO own endpoint
    suspend fun getByIdentifier(identifier: String?): PurgeTypeModel? {
        return getAllPurgeTypes()?.firstOrNull { purgeTypeModel: PurgeTypeModel ->
            purgeTypeModel.identifier.equals(
                identifier,
                ignoreCase = true
            )
        }
    }

    suspend fun getAllPurgeTypes(): List<PurgeTypeModel>? = dhApiRequest("all") {}

    companion object {
        private val instances: MutableMap<CarryTypeModel, ClientlessPurgeTypeConnection> = ConcurrentHashMap()

        operator fun get(carryTypeModel: CarryTypeModel): ClientlessPurgeTypeConnection {
            return instances.computeIfAbsent(carryTypeModel) { ClientlessPurgeTypeConnection(it) }
        }

        class ClientlessPurgeTypeConnection(val carryTypeModel: CarryTypeModel) :
            ClientlessConnection<PurgeTypeConnection> {
            override fun authenticated(authenticationProvider: AuthenticationProvider): PurgeTypeConnection {
                return PurgeTypeConnection(carryTypeModel, AuthenticatedClient(authenticationProvider))
            }
        }
    }
}