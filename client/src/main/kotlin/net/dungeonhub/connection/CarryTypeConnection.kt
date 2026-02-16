package net.dungeonhub.connection

import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.carry_type.CarryTypeCreationModel
import net.dungeonhub.model.carry_type.CarryTypeModel
import net.dungeonhub.model.carry_type.CarryTypeUpdateModel
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection
import java.util.*

@OptIn(ExperimentalStdlibApi::class)
class CarryTypeConnection(server: Long, override val client: AuthenticatedClient) : AuthenticatedModuleConnection(client) {
    override val moduleApiPrefix = "server/$server/carry-type"

    suspend fun getById(id: Long): CarryTypeModel? {
        return dhApiRequest(id) {}
    }

    //TODO dedicated endpoint?
    suspend fun getByIdentifier(identifier: String?): CarryTypeModel? {
        return getAllCarryTypes()?.firstOrNull { carryTypeModel: CarryTypeModel ->
            carryTypeModel.identifier.equals(
                identifier,
                ignoreCase = true
            )
        }
    }

    suspend fun findCarryTypeByString(input: String): CarryTypeModel? {
        val allCarryTypes = getAllCarryTypes() ?: return null

        return allCarryTypes.singleOrNull { it.displayName.equals(input, true) }
            ?: allCarryTypes.singleOrNull { it.identifier.equals(input, true) }
            ?: allCarryTypes.singleOrNull { it.displayName.startsWith(input, true) }
            ?: allCarryTypes.singleOrNull { it.identifier.startsWith(input, true) }
    }

    suspend fun addNewCarryType(creationModel: CarryTypeCreationModel): CarryTypeModel? {
        return dhApiRequest {
            method = HttpMethod.Post
            setBody(creationModel)
        }
    }

    suspend fun updateCarryType(id: Long, updateModel: CarryTypeUpdateModel): CarryTypeModel? {
        return dhApiRequest(id) {
            method = HttpMethod.Put
            setBody(updateModel)
        }
    }

    suspend fun deleteCarryType(carryTypeModel: CarryTypeModel): CarryTypeModel? {
        return dhApiRequest(carryTypeModel.id) {
            method = HttpMethod.Delete
        }
    }

    suspend fun getAllCarryTypes(): List<CarryTypeModel>? = dhApiRequest("all") {}

    companion object {
        private val instances: MutableMap<Long, ClientlessCarryTypeConnection> = HashMap()

        operator fun get(server: Long): ClientlessCarryTypeConnection {
            return instances.computeIfAbsent(server) { ClientlessCarryTypeConnection(it) }
        }

        operator fun get(server: DiscordServerModel): ClientlessCarryTypeConnection {
            return get(server.id)
        }

        class ClientlessCarryTypeConnection(val server: Long) : ClientlessConnection<CarryTypeConnection> {
            override fun authenticated(authenticationProvider: AuthenticationProvider): CarryTypeConnection {
                return CarryTypeConnection(server, AuthenticatedClient(authenticationProvider))
            }
        }
    }
}