package net.dungeonhub.connection

import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.carry_tier.CarryTierCreationModel
import net.dungeonhub.model.carry_tier.CarryTierModel
import net.dungeonhub.model.carry_tier.CarryTierUpdateModel
import net.dungeonhub.model.carry_type.CarryTypeModel
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection
import java.util.*

@OptIn(ExperimentalStdlibApi::class)
class CarryTierConnection(carryTypeModel: CarryTypeModel, override val client: AuthenticatedClient) : AuthenticatedModuleConnection(client) {
    override val moduleApiPrefix = "server/${carryTypeModel.server.id}/carry-type/${carryTypeModel.id}/carry-tier"

    suspend fun getByIdentifier(identifier: String?): CarryTierModel? {
        return getAllCarryTiers()?.firstOrNull { carryTierModel: CarryTierModel ->
            carryTierModel.identifier.equals(
                identifier,
                ignoreCase = true
            )
        }
    }

    suspend fun findCarryTierByString(input: String): CarryTierModel? {
        val allCarryTiers = getAllCarryTiers() ?: return null

        return allCarryTiers.singleOrNull { it.displayName.equals(input, true) }
            ?: allCarryTiers.singleOrNull { it.identifier.equals(input, true) }
            ?: allCarryTiers.singleOrNull { it.displayName.startsWith(input, true) }
            ?: allCarryTiers.singleOrNull { it.identifier.startsWith(input, true) }
    }

    /**
     * Loads all available carry tiers for the given carry type.
     * This represents the tiers of carry, so for example; floor 1, master mode floor 1, tier 4, kuudra, ...
     *
     * @return The list of carry tiers that were loaded from the database.
     */
    suspend fun getAllCarryTiers(): List<CarryTierModel>? {
        return dhApiRequest("all") {}
    }

    suspend fun createCarryTier(creationModel: CarryTierCreationModel): CarryTierModel? {
        return dhApiRequest {
            method = HttpMethod.Post
            setBody(creationModel)
        }
    }

    suspend fun updateCarryTier(id: Long, updateModel: CarryTierUpdateModel): CarryTierModel? {
        return dhApiRequest(id) {
            method = HttpMethod.Put
            setBody(updateModel)
        }
    }

    suspend fun deleteCarryTier(id: Long): CarryTierModel? = dhApiRequest(id) { method = HttpMethod.Delete }

    companion object {
        private val instances: MutableMap<CarryTypeModel, ClientlessCarryTierConnection> = HashMap()

        operator fun get(carryTypeModel: CarryTypeModel): ClientlessCarryTierConnection {
            return instances.computeIfAbsent(carryTypeModel) { ClientlessCarryTierConnection(it) }
        }

        class ClientlessCarryTierConnection(val carryTypeModel: CarryTypeModel) : ClientlessConnection<CarryTierConnection> {
            override fun authenticated(authenticationProvider: AuthenticationProvider): CarryTierConnection {
                return CarryTierConnection(carryTypeModel, AuthenticatedClient(authenticationProvider))
            }
        }
    }
}