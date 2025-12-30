package net.dungeonhub.connection

import com.squareup.moshi.adapter
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.carry_difficulty.CarryDifficultyCreationModel
import net.dungeonhub.model.carry_difficulty.CarryDifficultyModel
import net.dungeonhub.model.carry_difficulty.CarryDifficultyUpdateModel
import net.dungeonhub.model.carry_tier.CarryTierModel
import net.dungeonhub.service.MoshiService.moshi
import net.dungeonhub.structure.ClientlessConnection
import net.dungeonhub.structure.Connection.Companion.jsonMediaType
import net.dungeonhub.structure.ModuleConnection
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@OptIn(ExperimentalStdlibApi::class)
class CarryDifficultyConnection(carryTierModel: CarryTierModel, override val client: AuthenticatedClient) :
    ModuleConnection {
    override val moduleApiPrefix = ("server/"
            + carryTierModel.carryType.server.id
            + "/carry-type/"
            + carryTierModel.carryType.id
            + "/carry-tier/"
            + carryTierModel.id
            + "/carry-difficulty")

    fun getCarryDifficulty(id: Long): CarryDifficultyModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val request: Request = getApiRequest(url).get().build()

        return executeRequest(request) { json: String -> CarryDifficultyModel.fromJson(json) }
    }

    val allCarryDifficulties: List<CarryDifficultyModel>?
        get() {
            val url: HttpUrl = getApiUrl("all").build()

            val request: Request = getApiRequest(url).get().build()

            return executeRequest(request, function = moshi.adapter<List<CarryDifficultyModel>>()::fromJson)
        }

    fun getByIdentifier(identifier: String?): CarryDifficultyModel? {
        return allCarryDifficulties?.firstOrNull {
            it.identifier.equals(
                identifier,
                ignoreCase = true
            )
        }
    }

    fun createCarryDifficulty(creationModel: CarryDifficultyCreationModel): CarryDifficultyModel? {
        val url: HttpUrl = getApiUrl().build()

        val requestBody = creationModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url).post(requestBody).build()

        return executeRequest(request) { CarryDifficultyModel.fromJson(it) }
    }

    fun updateCarryDifficulty(id: Long, updateModel: CarryDifficultyUpdateModel): CarryDifficultyModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val requestBody: RequestBody = updateModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url).put(requestBody).build()

        return executeRequest(request) { json: String -> CarryDifficultyModel.fromJson(json) }
    }

    fun deleteCarryDifficulty(id: Long): CarryDifficultyModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val request: Request = getApiRequest(url).delete().build()

        return executeRequest(request) { CarryDifficultyModel.fromJson(it) }
    }

    companion object {
        private val instances: MutableMap<CarryTierModel, ClientlessCarryDifficultyConnection> = HashMap()

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