package net.dungeonhub.connection

import com.squareup.moshi.adapter
import net.dungeonhub.model.carry_type.CarryTypeModel
import net.dungeonhub.model.purge_type.PurgeTypeModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.service.MoshiService.moshi
import net.dungeonhub.structure.ModuleConnection
import okhttp3.HttpUrl
import okhttp3.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@OptIn(ExperimentalStdlibApi::class)
class PurgeTypeConnection(carryTypeModel: CarryTypeModel) : ModuleConnection {
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

            return executeRequest(request, moshi.adapter<List<PurgeTypeModel>>()::fromJson)
        }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PurgeTypeConnection::class.java)
        private val instances: MutableMap<CarryTypeModel, PurgeTypeConnection> = HashMap()

        operator fun get(carryTypeModel: CarryTypeModel): PurgeTypeConnection {
            return instances.computeIfAbsent(carryTypeModel) { PurgeTypeConnection(it) }
        }
    }
}