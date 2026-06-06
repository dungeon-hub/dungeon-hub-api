package net.dungeonhub.connection

import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.carry_difficulty.CarryDifficultyCreationModel
import net.dungeonhub.model.carry_difficulty.CarryDifficultyModel
import net.dungeonhub.model.carry_difficulty.CarryDifficultyUpdateModel
import net.dungeonhub.model.carry_tier.CarryTierModel
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection
import java.util.concurrent.ConcurrentHashMap

class CarryDifficultyConnection(carryTierModel: CarryTierModel, override val client: AuthenticatedClient) :
    AuthenticatedModuleConnection(client) {
    override val moduleApiPrefix = ("server/"
            + carryTierModel.carryType.server.id
            + "/carry-type/"
            + carryTierModel.carryType.id
            + "/carry-tier/"
            + carryTierModel.id
            + "/carry-difficulty")

    suspend fun getCarryDifficulty(id: Long): CarryDifficultyModel? {
        return dhApiRequest(id) {}
    }

    suspend fun getAllCarryDifficulties(): List<CarryDifficultyModel>? = dhApiRequest("all") {}

    suspend fun getByIdentifier(identifier: String?): CarryDifficultyModel? {
        return getAllCarryDifficulties()?.firstOrNull {
            it.identifier.equals(
                identifier,
                ignoreCase = true
            )
        }
    }

    suspend fun findCarryDifficultyByString(input: String): CarryDifficultyModel? {
        val allCarryDifficulties = getAllCarryDifficulties() ?: return null

        return allCarryDifficulties.singleOrNull { it.displayName.equals(input, true) }
            ?: allCarryDifficulties.singleOrNull { it.identifier.equals(input, true) }
            ?: allCarryDifficulties.singleOrNull { it.displayName.startsWith(input, true) }
            ?: allCarryDifficulties.singleOrNull { it.identifier.startsWith(input, true) }
    }

    suspend fun createCarryDifficulty(creationModel: CarryDifficultyCreationModel): CarryDifficultyModel? {
        return dhApiRequest {
            method = HttpMethod.Post
            setBody(creationModel)
        }
    }

    suspend fun updateCarryDifficulty(id: Long, updateModel: CarryDifficultyUpdateModel): CarryDifficultyModel? {
        return dhApiRequest(id) {
            method = HttpMethod.Put
            setBody(updateModel)
        }
    }

    suspend fun deleteCarryDifficulty(id: Long): CarryDifficultyModel? {
        return dhApiRequest(id) {
            method = HttpMethod.Delete
        }
    }

    companion object {
        private val instances: MutableMap<CarryTierModel, ClientlessCarryDifficultyConnection> = ConcurrentHashMap()

        operator fun get(carryTierModel: CarryTierModel): ClientlessCarryDifficultyConnection {
            return instances.computeIfAbsent(carryTierModel) { ClientlessCarryDifficultyConnection(it) }
        }

        class ClientlessCarryDifficultyConnection(val carryTierModel: CarryTierModel) :
            ClientlessConnection<CarryDifficultyConnection> {
            override fun authenticated(authenticationProvider: AuthenticationProvider): CarryDifficultyConnection {
                return CarryDifficultyConnection(carryTierModel, AuthenticatedClient(authenticationProvider))
            }
        }
    }
}