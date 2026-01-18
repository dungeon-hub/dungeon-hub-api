package net.dungeonhub.connection

import com.squareup.moshi.adapter
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.carry_type.CarryTypeModel
import net.dungeonhub.model.purge_type.PurgeTypeModel
import net.dungeonhub.service.MoshiService.moshi
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection
import okhttp3.HttpUrl
import okhttp3.Request

@OptIn(ExperimentalStdlibApi::class)
class PurgeTypeConnection(carryTypeModel: CarryTypeModel, override val client: AuthenticatedClient) : AuthenticatedModuleConnection(client) {
    override val moduleApiPrefix = "server/${carryTypeModel.server.id}/carry-type/${carryTypeModel.id}/purge-type"

    //TODO own endpoint
    fun getByIdentifier(identifier: String?): PurgeTypeModel? {
        return allPurgeTypes?.firstOrNull { carryTypeModel: PurgeTypeModel ->
            carryTypeModel.identifier.equals(
                identifier,
                ignoreCase = true
            )
        }
    }

    val allPurgeTypes: List<PurgeTypeModel>?
        get() {
            val url: HttpUrl = getApiUrl("all").build()

            val request: Request = getApiRequest(url)
                .get()
                .build()

            return executeRequest(request, function = moshi.adapter<List<PurgeTypeModel>>()::fromJson)
        }

    companion object {
        private val instances: MutableMap<CarryTypeModel, ClientlessPurgeTypeConnection> = HashMap()

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