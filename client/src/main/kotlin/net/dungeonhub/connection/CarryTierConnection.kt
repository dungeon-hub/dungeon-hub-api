package net.dungeonhub.connection

import com.squareup.moshi.adapter
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.carry_tier.CarryTierCreationModel
import net.dungeonhub.model.carry_tier.CarryTierModel
import net.dungeonhub.model.carry_tier.CarryTierUpdateModel
import net.dungeonhub.model.carry_type.CarryTypeModel
import net.dungeonhub.service.MoshiService.moshi
import net.dungeonhub.structure.ClientlessConnection
import net.dungeonhub.structure.ModuleConnection
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

@OptIn(ExperimentalStdlibApi::class)
class CarryTierConnection(carryTypeModel: CarryTypeModel, override val client: AuthenticatedClient) : ModuleConnection {
    override val moduleApiPrefix = "server/${carryTypeModel.server.id}/carry-type/${carryTypeModel.id}/carry-tier"

    fun getByIdentifier(identifier: String?): CarryTierModel? {
        return allCarryTiers?.firstOrNull { carryTierModel: CarryTierModel ->
            carryTierModel.identifier.equals(
                identifier,
                ignoreCase = true
            )

        }
    }

    val allCarryTiers: List<CarryTierModel>?
        /**
         * Loads all available carry tiers for the given carry type.
         * This represents the tiers of carry, so for example floor 1, master mode floor 1, tier 4, kuudra, ...
         *
         * @return The list of carry tiers that were loaded from the database.
         */
        get() {
            val url: HttpUrl = getApiUrl("all").build()

            val request: Request = getApiRequest(url)
                .get()
                .build()

            return executeRequest(request, function = moshi.adapter<List<CarryTierModel>>()::fromJson)
        }

    fun createCarryTier(creationModel: CarryTierCreationModel): CarryTierModel? {
        val url: HttpUrl = getApiUrl().build()

        val requestBody: RequestBody = creationModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .post(requestBody)
            .build()

        return executeRequest(request) { json: String -> CarryTierModel.fromJson(json) }
    }

    fun updateCarryTier(id: Long, updateModel: CarryTierUpdateModel): CarryTierModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val requestBody: RequestBody = updateModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .put(requestBody)
            .build()

        return executeRequest(request) { json: String -> CarryTierModel.fromJson(json) }
    }

    fun deleteCarryTier(id: Long): CarryTierModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val request: Request = getApiRequest(url)
            .delete()
            .build()

        return executeRequest(request) { json: String -> CarryTierModel.fromJson(json) }
    }

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