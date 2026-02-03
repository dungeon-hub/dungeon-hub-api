package net.dungeonhub.connection

import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.enums.ScoreResetType
import net.dungeonhub.enums.ScoreType
import net.dungeonhub.model.carry_type.CarryTypeModel
import net.dungeonhub.model.score.ScoreLeaderboardModel
import net.dungeonhub.model.score.ScoreModel
import net.dungeonhub.model.score.ScoreResetModel
import net.dungeonhub.model.score.ScoreUpdateModel
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection
import org.jetbrains.annotations.Range

@OptIn(ExperimentalStdlibApi::class)
class ScoreConnection(carryTypeModel: CarryTypeModel, override val client: AuthenticatedClient) : AuthenticatedModuleConnection(client) {
    override val moduleApiPrefix = "server/${carryTypeModel.server.id}/carry-type/${carryTypeModel.id}/score"
    private val SCORE_TYPE = "score-type"

    suspend fun getScore(id: Long, scoreType: ScoreType): ScoreModel? = dhApiRequest(id) {
        parameter(SCORE_TYPE, scoreType.name)
    }

    suspend fun getScore(id: Long): ScoreModel? {
        return getScore(id, ScoreType.Default)
    }

    suspend fun getScores(): List<ScoreModel>? = dhApiRequest("all") {}

    suspend fun getScores(id: Long): List<ScoreModel>? = dhApiRequest("all") {
        parameter("id", id)
    }

    suspend fun updateScores(scoreUpdateModel: ScoreUpdateModel): List<ScoreModel>? = dhApiRequest {
        method = HttpMethod.Put
        setBody(scoreUpdateModel)
    }

    @JvmOverloads
    suspend fun loadLeaderboard(
        scoreType: ScoreType = ScoreType.Default,
        page: @Range(from = 0, to = Integer.MAX_VALUE.toLong()) Int = 0,
        userId: Long? = null
    ): ScoreLeaderboardModel? = dhApiRequest("leaderboard") {
        parameter(SCORE_TYPE, scoreType.name)
        parameter("page", page)
        if (userId != null) {
            parameter("user", userId)
        }
    }

    suspend fun resetScore(scoreResetType: ScoreResetType): ScoreResetModel? = dhApiRequest {
        method = HttpMethod.Delete
        parameter(SCORE_TYPE, scoreResetType.name)
    }

    companion object {
        private val instances: MutableMap<CarryTypeModel, ClientlessScoreConnection> = HashMap()

        operator fun get(carryTypeModel: CarryTypeModel): ClientlessScoreConnection {
            return instances.computeIfAbsent(carryTypeModel) { ClientlessScoreConnection(it) }
        }

        class ClientlessScoreConnection(val carryTypeModel: CarryTypeModel) : ClientlessConnection<ScoreConnection> {
            override fun authenticated(authenticationProvider: AuthenticationProvider): ScoreConnection {
                return ScoreConnection(carryTypeModel, AuthenticatedClient(authenticationProvider))
            }
        }
    }
}