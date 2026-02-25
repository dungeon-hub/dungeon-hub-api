package net.dungeonhub.connection

import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import io.ktor.http.isSuccess
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.client.DungeonHubClient
import net.dungeonhub.enums.QueueStep
import net.dungeonhub.model.carry_difficulty.CarryDifficultyModel
import net.dungeonhub.model.carry_queue.CarryQueueCreationModel
import net.dungeonhub.model.carry_queue.CarryQueueModel
import net.dungeonhub.model.carry_queue.CarryQueueUpdateModel
import net.dungeonhub.model.score.LoggedCarryModel
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection

@OptIn(ExperimentalStdlibApi::class)
class QueueConnection(override val client: DungeonHubClient) : AuthenticatedModuleConnection(client) {
    override val moduleApiPrefix = "queue"

    suspend fun addNewQueue(
        carryDifficultyModel: CarryDifficultyModel,
        creationModel: CarryQueueCreationModel
    ): CarryQueueModel? = dhApiRequest("carry-difficulty/${carryDifficultyModel.id}") {
        method = HttpMethod.Post
        setBody(creationModel)
    }

    // TODO merge this with the two methods below?
    suspend fun getCarryQueueByRelatedIdAndQueueStep(
        relatedId: Long,
        queueStep: QueueStep
    ): Set<CarryQueueModel>? = dhApiRequest("all") {
        parameter("related-id", relatedId)
        parameter("queue-step", queueStep.name)
    }

    suspend fun getCarryQueueByRelatedId(id: Long): Set<CarryQueueModel>? = dhApiRequest("all") {
        parameter("related-id", id)
    }

    suspend fun getCarryQueuesByQueueStep(queueStep: QueueStep): Set<CarryQueueModel>? = dhApiRequest("all") {
        parameter("queue-step", queueStep.name)
    }

    suspend fun updateQueue(id: Long, updateModel: CarryQueueUpdateModel): CarryQueueModel? = dhApiRequest(id) {
        method = HttpMethod.Put
        setBody(updateModel)
    }

    suspend fun deleteQueue(id: Long): Boolean = executeModuleRequest(id.toString()) {
        method = HttpMethod.Delete
    }?.takeIf { it.status.isSuccess() } != null

    suspend fun logQueue(id: Long, updateModel: CarryQueueUpdateModel): LoggedCarryModel? = dhApiRequest("log/$id") {
        method = HttpMethod.Post
        setBody(updateModel)
    }

    companion object : ClientlessConnection<QueueConnection> {
        override fun authenticated(authenticationProvider: AuthenticationProvider): QueueConnection {
            return QueueConnection(AuthenticatedClient(authenticationProvider))
        }
    }
}