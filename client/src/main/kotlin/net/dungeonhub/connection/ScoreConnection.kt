package net.dungeonhub.connection

import com.squareup.moshi.adapter
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.enums.ScoreResetType
import net.dungeonhub.enums.ScoreType
import net.dungeonhub.model.carry_type.CarryTypeModel
import net.dungeonhub.model.score.LeaderboardModel
import net.dungeonhub.model.score.ScoreModel
import net.dungeonhub.model.score.ScoreResetModel
import net.dungeonhub.model.score.ScoreUpdateModel
import net.dungeonhub.service.MoshiService.moshi
import net.dungeonhub.structure.ClientlessConnection
import net.dungeonhub.structure.ModuleConnection
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.jetbrains.annotations.Range

@OptIn(ExperimentalStdlibApi::class)
class ScoreConnection(carryTypeModel: CarryTypeModel, override val client: AuthenticatedClient) : ModuleConnection {
    override val moduleApiPrefix = "server/${carryTypeModel.server.id}/carry-type/${carryTypeModel.id}/score"
    private val SCORE_TYPE = "score-type"

    fun getScore(id: Long, scoreType: ScoreType): ScoreModel? {
        val url: HttpUrl = getApiUrl(id)
            .addQueryParameter(SCORE_TYPE, scoreType.name)
            .build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request) { json: String -> ScoreModel.fromJson(json) }
    }

    val scores: List<ScoreModel>?
        get() {
            val url: HttpUrl = getApiUrl("all").build()

            val request: Request = getApiRequest(url)
                .get()
                .build()

            return executeRequest(request, function = moshi.adapter<List<ScoreModel>>()::fromJson)
        }

    fun getScores(id: Long): List<ScoreModel>? {
        val url: HttpUrl = getApiUrl("all")
            .addQueryParameter("id", id.toString())
            .build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request, function = moshi.adapter<List<ScoreModel>>()::fromJson)
    }

    fun getScore(id: Long): ScoreModel? {
        return getScore(id, ScoreType.Default)
    }

    fun updateScores(scoreUpdateModel: ScoreUpdateModel): List<ScoreModel>? {
        val url: HttpUrl = getApiUrl().build()

        val requestBody = scoreUpdateModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .put(requestBody)
            .build()

        return executeRequest(request, function = moshi.adapter<List<ScoreModel>>()::fromJson)
    }

    @JvmOverloads
    fun loadLeaderboard(
        scoreType: ScoreType = ScoreType.Default,
        page: @Range(from = 0, to = Integer.MAX_VALUE.toLong()) Int = 0,
        userId: Long? = null
    ): LeaderboardModel? {
        val urlBuilder = getApiUrl("leaderboard")
            .addQueryParameter(SCORE_TYPE, scoreType.name)
            .addQueryParameter("page", page.toString())

        if (userId != null) {
            urlBuilder.addQueryParameter("user", userId.toString())
        }

        val request: Request = getApiRequest(urlBuilder.build())
            .get()
            .build()

        return executeRequest(request) { json: String -> LeaderboardModel.fromJson(json) }
    }

    fun resetScore(scoreResetType: ScoreResetType): ScoreResetModel? {
        val url: HttpUrl = getApiUrl().addQueryParameter(SCORE_TYPE, scoreResetType.name).build()

        val request: Request = getApiRequest(url).delete().build()

        return executeRequest(request) { json: String -> ScoreResetModel.fromJson(json) }
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