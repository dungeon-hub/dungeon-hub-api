package net.dungeonhub.connection

import net.dungeonhub.model.cnt_request.CntRequestCreationModel
import net.dungeonhub.model.cnt_request.CntRequestModel
import net.dungeonhub.model.cnt_request.CntRequestUpdateModel
import net.dungeonhub.structure.ModuleConnection
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CntRequestConnection(private val server: Long) : ModuleConnection {
    override val moduleApiPrefix = "server/$server/cnt-request"

    fun findCntRequest(messageId: Long): CntRequestModel? {
        val url: HttpUrl = getApiUrl("find")
            .addQueryParameter("message-id", messageId.toString())
            .build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request) { json: String -> CntRequestModel.Companion.fromJson(json) }
    }

    fun createCntRequest(creationModel: CntRequestCreationModel): CntRequestModel? {
        val url: HttpUrl = getApiUrl().build()

        val requestBody = creationModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .post(requestBody)
            .build()

        return executeRequest(request) { json: String -> CntRequestModel.Companion.fromJson(json) }
    }

    fun updateCntRequest(id: Long, updateModel: CntRequestUpdateModel): CntRequestModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val requestBody: RequestBody = updateModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .put(requestBody)
            .build()

        return executeRequest(request) { json: String -> CntRequestModel.Companion.fromJson(json) }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(CntRequestConnection::class.java)
        private val instances: MutableMap<Long, CntRequestConnection> = HashMap()

        operator fun get(server: Long): CntRequestConnection {
            return instances.computeIfAbsent(server) { CntRequestConnection(it) }
        }
    }
}