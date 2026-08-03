package net.dungeonhub.connection

import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.stats.DiscordServerStatsModel
import net.dungeonhub.model.stats.GlobalStatsModel
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection

class StatsConnection(override val client: AuthenticatedClient) : AuthenticatedModuleConnection(client) {
    override val moduleApiPrefix = "stats"

    suspend fun getGlobalStats(): GlobalStatsModel? = dhApiRequest("global") {}

    suspend fun getServerStats(server: Long): DiscordServerStatsModel? = dhApiRequest("server/$server/stats") {}

    companion object : ClientlessConnection<StatsConnection> {
        override fun authenticated(authenticationProvider: AuthenticationProvider): StatsConnection {
            return StatsConnection(AuthenticatedClient(authenticationProvider))
        }
    }
}