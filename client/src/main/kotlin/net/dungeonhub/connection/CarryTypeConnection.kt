package net.dungeonhub.connection

import com.squareup.moshi.adapter
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.carry_type.CarryTypeCreationModel
import net.dungeonhub.model.carry_type.CarryTypeModel
import net.dungeonhub.model.carry_type.CarryTypeUpdateModel
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.service.MoshiService.moshi
import net.dungeonhub.structure.ClientlessConnection
import net.dungeonhub.structure.Connection.Companion.jsonMediaType
import net.dungeonhub.structure.ModuleConnection
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

@OptIn(ExperimentalStdlibApi::class)
class CarryTypeConnection(server: Long, override val client: AuthenticatedClient) : ModuleConnection {
    override val moduleApiPrefix = "server/$server/carry-type"

    fun getById(id: Long): CarryTypeModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request) { json: String -> CarryTypeModel.fromJson(json) }
    }

    //TODO dedicated endpoint?
    fun getByIdentifier(identifier: String?): CarryTypeModel? {
        return allCarryTypes?.firstOrNull { carryTypeModel: CarryTypeModel ->
            carryTypeModel.identifier.equals(
                identifier,
                ignoreCase = true
            )
        }
    }

    fun addNewCarryType(creationModel: CarryTypeCreationModel): CarryTypeModel? {
        val url: HttpUrl = getApiUrl().build()

        val requestBody = creationModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .post(requestBody)
            .build()

        return executeRequest(request) { json: String -> CarryTypeModel.fromJson(json) }
    }

    fun updateCarryType(id: Long, updateModel: CarryTypeUpdateModel): CarryTypeModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val requestBody = updateModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .put(requestBody)
            .build()

        return executeRequest(request) { json: String -> CarryTypeModel.fromJson(json) }
    }

    fun deleteCarryType(carryTypeModel: CarryTypeModel): CarryTypeModel? {
        val url: HttpUrl = getApiUrl(carryTypeModel.id).build()

        val request: Request = getApiRequest(url)
            .delete()
            .build()

        return executeRequest(request) { json: String -> CarryTypeModel.fromJson(json) }
    }

    val allCarryTypes: List<CarryTypeModel>?
        get() {
            val url: HttpUrl = getApiUrl("all").build()

            val request: Request = getApiRequest(url).get().build()

            return executeRequest(request, function = moshi.adapter<List<CarryTypeModel>>()::fromJson)
        }

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