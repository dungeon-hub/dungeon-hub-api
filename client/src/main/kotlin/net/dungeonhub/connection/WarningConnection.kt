package net.dungeonhub.connection

import com.squareup.moshi.adapter
import net.dungeonhub.model.warning.AddedWarningModel
import net.dungeonhub.model.warning.DetailedWarningModel
import net.dungeonhub.model.warning.WarningCreationModel
import net.dungeonhub.model.warning.WarningEvidenceCreationModel
import net.dungeonhub.service.MoshiService.moshi
import net.dungeonhub.structure.ModuleConnection
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@OptIn(ExperimentalStdlibApi::class)
class WarningConnection(private val serverId: Long) : ModuleConnection {
    override val moduleApiPrefix = "server/$serverId/warns"

    fun getAllWarns(userId: Long): List<DetailedWarningModel>? {
        val url: HttpUrl = getApiUrl("all")
            .addQueryParameter("user", userId.toString())
            .build()

        val request: Request = getApiRequest(url).get().build()

        return executeRequest(request, function = moshi.adapter<List<DetailedWarningModel>>()::fromJson)
    }

    fun getActiveWarns(userId: Long): List<DetailedWarningModel>? {
        val url: HttpUrl = getApiUrl("active")
            .addQueryParameter("user", userId.toString())
            .build()

        val request: Request = getApiRequest(url).get().build()

        return executeRequest(request, function = moshi.adapter<List<DetailedWarningModel>>()::fromJson)
    }

    fun addWarning(creationModel: WarningCreationModel): AddedWarningModel? {
        val url: HttpUrl = getApiUrl().build()

        val requestBody = creationModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url).post(requestBody).build()

        return executeRequest(request) { json: String -> AddedWarningModel.Companion.fromJson(json) }
    }

    fun deactivateWarning(id: Long): DetailedWarningModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val request: Request = getApiRequest(url).delete().build()

        return executeRequest(request) { json: String -> DetailedWarningModel.Companion.fromJson(json) }
    }

    fun addEvidence(
        warningId: Long,
        evidenceCreationModel: WarningEvidenceCreationModel
    ): DetailedWarningModel? {
        val url: HttpUrl = getApiUrl("$warningId/evidence").build()

        val requestBody = evidenceCreationModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url).put(requestBody).build()

        return executeRequest(request) { json: String -> DetailedWarningModel.Companion.fromJson(json) }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(WarningConnection::class.java)
        private val instances: MutableMap<Long, WarningConnection> = HashMap()

        operator fun get(serverId: Long): WarningConnection {
            return instances.computeIfAbsent(serverId) { WarningConnection(it) }
        }
    }
}