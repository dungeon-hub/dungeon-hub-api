package net.dungeonhub.connection

import com.squareup.moshi.adapter
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.client.DungeonHubClient
import net.dungeonhub.enums.ScoreType
import net.dungeonhub.model.carry_difficulty.CarryDifficultyModel
import net.dungeonhub.model.carry_tier.CarryTierModel
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.model.reputation.ReputationLeaderboardModel
import net.dungeonhub.model.score.ScoreLeaderboardModel
import net.dungeonhub.model.score.ScoreModel
import net.dungeonhub.service.MoshiService.moshi
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection
import okhttp3.HttpUrl
import okhttp3.Request
import org.jetbrains.annotations.Range
import java.time.Instant

@OptIn(ExperimentalStdlibApi::class)
class DiscordServerConnection(override val client: DungeonHubClient) : AuthenticatedModuleConnection() {
    override val moduleApiPrefix = "server"

    fun findServerById(id: Long): DiscordServerModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request) { json: String -> DiscordServerModel.fromJson(json) }
    }

    fun getAllCarryTiers(serverId: Long): List<CarryTierModel>? {
        val url: HttpUrl = getApiUrl("$serverId/carry-tiers").build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request, function = moshi.adapter<List<CarryTierModel>>()::fromJson)
    }

    fun getAllCarryDifficulties(serverId: Long): List<CarryDifficultyModel>? {
        val url: HttpUrl = getApiUrl("$serverId/carry-difficulties").build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request) { s -> moshi.adapter<List<CarryDifficultyModel>>().fromJson(s) }
    }

    fun loadAllServers(): List<DiscordServerModel>? {
        val url: HttpUrl = getApiUrl("all").build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request, function = moshi.adapter<List<DiscordServerModel>>()::fromJson)
    }

    fun getCarryTierFromCategory(serverId: Long, categoryId: Long): CarryTierModel? {
        val url: HttpUrl = getApiUrl("$serverId/category/$categoryId/carry-tier").build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request) { json: String -> CarryTierModel.fromJson(json) }
    }

    fun getScores(serverModel: DiscordServerModel, id: Long): List<ScoreModel>? {
        val url: HttpUrl = getApiUrl(serverModel.id.toString() + "/score/" + id).build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request, function = moshi.adapter<List<ScoreModel>>()::fromJson)
    }

    @JvmOverloads
    fun loadTotalLeaderboard(
        serverId: Long,
        scoreType: ScoreType = ScoreType.Default,
        page: @Range(from = 0, to = Integer.MAX_VALUE.toLong()) Int = 0,
        userId: Long? = null
    ): ScoreLeaderboardModel? {
        val urlBuilder = getApiUrl("$serverId/total-leaderboard")
            .addQueryParameter("score-type", scoreType.name)
            .addQueryParameter("page", page.toString())

        if (userId != null) {
            urlBuilder.addQueryParameter("user", userId.toString())
        }

        val request: Request = getApiRequest(urlBuilder.build())
            .get()
            .build()

        return executeRequest(request) { json: String -> ScoreLeaderboardModel.fromJson(json) }
    }

    fun loadReputationLeaderboard(
        serverId: Long,
        page: @Range(from = 0, to = Integer.MAX_VALUE.toLong()) Int = 0,
        userId: Long? = null
    ): ReputationLeaderboardModel? {
        val urlBuilder = getApiUrl("$serverId/reputation-leaderboard")
            .addQueryParameter("page", page.toString())

        if (userId != null) {
            urlBuilder.addQueryParameter("user", userId.toString())
        }

        val request: Request = getApiRequest(urlBuilder.build())
            .get()
            .build()

        return executeRequest(request) { json: String -> ReputationLeaderboardModel.fromJson(json) }
    }

    fun getTotalAmountOfMoneySpent(
        serverId: Long,
        userId: Long? = null,
        carrierId: Long? = null,
        carryTypeId: Long? = null,
        carryTierId: Long? = null,
        since: Instant? = null
    ): Long? {
        val url = getApiUrl("$serverId/total-money-spent")

        if (userId != null) {
            url.addQueryParameter("user", userId.toString())
        }

        if (carrierId != null) {
            url.addQueryParameter("carrier", carrierId.toString())
        }

        if (carryTypeId != null) {
            url.addQueryParameter("carry-type", carryTypeId.toString())
        }

        if (carryTierId != null) {
            url.addQueryParameter("carry-tier", carryTierId.toString())
        }

        if (since != null) {
            url.addQueryParameter("since", since.toEpochMilli().toString())
        }

        val request: Request = getApiRequest(url.build()).get().build()

        return executeRequest(request, function = java.lang.Long::parseLong)
    }

    fun getCarryAmount(serverId: Long, since: Instant? = null): Long? {
        val url = getApiUrl("$serverId/count-carries")

        if (since != null) {
            url.addQueryParameter("since", since.toEpochMilli().toString())
        }

        val request: Request = getApiRequest(url.build()).get().build()

        return executeRequest(request, function = java.lang.Long::parseLong)
    }

    companion object : ClientlessConnection<DiscordServerConnection> {
        override fun authenticated(authenticationProvider: AuthenticationProvider): DiscordServerConnection {
            return DiscordServerConnection(AuthenticatedClient(authenticationProvider))
        }
    }
}