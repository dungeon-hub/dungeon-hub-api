package net.dungeonhub.connection

import io.ktor.client.request.parameter
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.client.DungeonHubClient
import net.dungeonhub.enums.ScoreType
import net.dungeonhub.model.carry_difficulty.CarryDifficultyModel
import net.dungeonhub.model.carry_tier.CarryTierModel
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.model.reputation.ReputationLeaderboardModel
import net.dungeonhub.model.reputation.ReputationModel
import net.dungeonhub.model.score.ScoreLeaderboardModel
import net.dungeonhub.model.score.ScoreModel
import net.dungeonhub.model.static_message.StaticMessageModel
import net.dungeonhub.model.ticket.TicketModel
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection
import org.jetbrains.annotations.Range
import java.time.Instant

@OptIn(ExperimentalStdlibApi::class)
class DiscordServerConnection(override val client: DungeonHubClient) : AuthenticatedModuleConnection(client) {
    override val moduleApiPrefix = "server"

    suspend fun findServerById(id: Long): DiscordServerModel? = dhApiRequest(id) {}

    suspend fun getAllCarryTiers(serverId: Long): List<CarryTierModel>? = dhApiRequest("$serverId/carry-tiers") {}

    suspend fun findCarryTierByString(serverId: Long, input: String): CarryTierModel? {
        val allCarryTiers = getAllCarryTiers(serverId) ?: return null

        return allCarryTiers.singleOrNull { it.displayName.equals(input, true) }
            ?: allCarryTiers.singleOrNull { it.identifier.equals(input, true) }
            ?: allCarryTiers.singleOrNull { it.displayName.startsWith(input) }
            ?: allCarryTiers.singleOrNull { it.identifier.startsWith(input) }
    }

    suspend fun getAllCarryDifficulties(serverId: Long): List<CarryDifficultyModel>? = dhApiRequest("$serverId/carry-difficulties") {}

    suspend fun loadAllServers(): List<DiscordServerModel>? = dhApiRequest("all") {}

    suspend fun getCarryTierFromCategory(serverId: Long, categoryId: Long): CarryTierModel? = dhApiRequest("$serverId/category/$categoryId/carry-tier") {}

    suspend fun getReputation(serverId: Long, reputationId: Long): ReputationModel? = dhApiRequest("$serverId/reputation/$reputationId") {}

    suspend fun getScores(serverModel: DiscordServerModel, id: Long): List<ScoreModel>? = dhApiRequest("${serverModel.id}/score/$id") {}

    @JvmOverloads
    suspend fun loadTotalLeaderboard(
        serverId: Long,
        scoreType: ScoreType = ScoreType.Default,
        page: @Range(from = 0, to = Integer.MAX_VALUE.toLong()) Int = 0,
        userId: Long? = null
    ): ScoreLeaderboardModel? = dhApiRequest("$serverId/total-leaderboard") {
        parameter("score-type", scoreType.name)
        parameter("page", page)
        if (userId != null) {
            parameter("user", userId)
        }
    }

    suspend fun loadReputationLeaderboard(
        serverId: Long,
        page: @Range(from = 0, to = Integer.MAX_VALUE.toLong()) Int = 0,
        userId: Long? = null
    ): ReputationLeaderboardModel? = dhApiRequest("$serverId/reputation-leaderboard") {
        parameter("page", page)
        if (userId != null) {
            parameter("user", userId)
        }
    }

    suspend fun getTotalAmountOfMoneySpent(
        serverId: Long,
        userId: Long? = null,
        carrierId: Long? = null,
        carryTypeId: Long? = null,
        carryTierId: Long? = null,
        since: Instant? = null
    ): Long? = dhApiRequest("$serverId/total-money-spent") {
        if (userId != null) {
            parameter("user", userId.toString())
        }

        if (carrierId != null) {
            parameter("carrier", carrierId.toString())
        }

        if (carryTypeId != null) {
            parameter("carry-type", carryTypeId.toString())
        }

        if (carryTierId != null) {
            parameter("carry-tier", carryTierId.toString())
        }

        if (since != null) {
            parameter("since", since.toEpochMilli().toString())
        }
    }

    suspend fun getCarryAmount(serverId: Long, since: Instant? = null): Long? = dhApiRequest("$serverId/count-carries") {
        if (since != null) {
            parameter("since", since.toEpochMilli().toString())
        }
    }

    suspend fun findGlobalStaticMessages(): List<StaticMessageModel>? = dhApiRequest("static-messages") {}

    suspend fun findTickets(serverId: Long, channelId: Long? = null): List<TicketModel>? = dhApiRequest("$serverId/ticket/find") {
        if (channelId != null) {
            parameter("channel", channelId.toString())
        }
    }

    companion object : ClientlessConnection<DiscordServerConnection> {
        override fun authenticated(authenticationProvider: AuthenticationProvider): DiscordServerConnection {
            return DiscordServerConnection(AuthenticatedClient(authenticationProvider))
        }
    }
}