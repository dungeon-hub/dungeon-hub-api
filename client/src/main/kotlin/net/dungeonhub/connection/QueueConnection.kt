package net.dungeonhub.connection

import com.squareup.moshi.adapter
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.client.DungeonHubClient
import net.dungeonhub.enums.QueueStep
import net.dungeonhub.model.carry_difficulty.CarryDifficultyModel
import net.dungeonhub.model.carry_queue.CarryQueueCreationModel
import net.dungeonhub.model.carry_queue.CarryQueueModel
import net.dungeonhub.model.carry_queue.CarryQueueUpdateModel
import net.dungeonhub.model.score.LoggedCarryModel
import net.dungeonhub.service.MoshiService.moshi
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection
import net.dungeonhub.structure.Connection.Companion.jsonMediaType
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@OptIn(ExperimentalStdlibApi::class)
class QueueConnection(override val client: DungeonHubClient) : AuthenticatedModuleConnection() {
    override val moduleApiPrefix = "queue"

    fun addNewQueue(
        carryDifficultyModel: CarryDifficultyModel,
        creationModel: CarryQueueCreationModel
    ): CarryQueueModel? {
        val url: HttpUrl = getApiUrl("carry-difficulty/" + carryDifficultyModel.id)
            .build()

        val requestBody = creationModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .post(requestBody)
            .build()

        return executeRequest(request) { json: String -> CarryQueueModel.fromJson(json) }
    }

    fun getCarryQueueByRelatedIdAndQueueStep(
        relatedId: Long,
        queueStep: QueueStep
    ): Set<CarryQueueModel>? {
        val url: HttpUrl = getApiUrl("all")
            .addQueryParameter("related-id", relatedId.toString())
            .addQueryParameter("queue-step", queueStep.name)
            .build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request, function = moshi.adapter<Set<CarryQueueModel>>()::fromJson)
    }

    fun getCarryQueueByRelatedId(id: Long): Set<CarryQueueModel>? {
        val url: HttpUrl = getApiUrl("all")
            .addQueryParameter("related-id", id.toString())
            .build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request, function = moshi.adapter<Set<CarryQueueModel>>()::fromJson)
    }

    fun getCarryQueuesByQueueStep(step: QueueStep): Set<CarryQueueModel>? {
        val url: HttpUrl = getApiUrl("all")
            .addQueryParameter("queue-step", step.name)
            .build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request, function = moshi.adapter<Set<CarryQueueModel>>()::fromJson)
    }

    fun updateQueue(id: Long, updateModel: CarryQueueUpdateModel): CarryQueueModel? {
        val url: HttpUrl = getApiUrl(id)
            .build()

        val requestBody = updateModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .put(requestBody)
            .build()

        return executeRequest(request) { json: String -> CarryQueueModel.fromJson(json) }
    }

    fun deleteQueue(id: Long): Boolean {
        val url: HttpUrl = getApiUrl(id)
            .build()

        val request: Request = getApiRequest(url)
            .delete()
            .build()

        return executeRequest(request) != null
    }

    fun logQueue(id: Long, updateModel: CarryQueueUpdateModel): LoggedCarryModel? {
        val url: HttpUrl = getApiUrl("log/$id")
            .build()

        val requestBody = updateModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .post(requestBody)
            .build()

        return executeRequest(request) { json: String -> LoggedCarryModel.fromJson(json) }
    }

    companion object : ClientlessConnection<QueueConnection> {
        override fun authenticated(authenticationProvider: AuthenticationProvider): QueueConnection {
            return QueueConnection(AuthenticatedClient(authenticationProvider))
        }
    }
}