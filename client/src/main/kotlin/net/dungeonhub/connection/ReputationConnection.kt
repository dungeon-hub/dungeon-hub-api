package net.dungeonhub.connection

import dev.kord.core.entity.Member
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.model.discord_user.DiscordUserModel
import net.dungeonhub.model.reputation.ReputationCreationModel
import net.dungeonhub.model.reputation.ReputationModel
import net.dungeonhub.structure.ClientlessConnection
import net.dungeonhub.structure.ModuleConnection
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class ReputationConnection(server: Long, discordUser: Long, override val client: AuthenticatedClient) :
    ModuleConnection {
    override val moduleApiPrefix = "server/$server/discord-user/$discordUser/reputation"

    fun calculateReputation(): Long? {
        val url = getApiUrl("calculate").build()

        val request = getApiRequest(url).get().build()

        return executeRequest(request, function = java.lang.Long::parseLong)
    }

    fun addReputation(creationModel: ReputationCreationModel): ReputationModel? {
        val url: HttpUrl = getApiUrl().build()

        val requestBody = creationModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .post(requestBody)
            .build()

        return executeRequest(request) { json: String -> ReputationModel.fromJson(json) }
    }

    companion object {
        private val instances: MutableMap<Long, MutableMap<Long, ClientlessReputationConnection>> = HashMap()

        operator fun get(server: Long, discordUser: Long): ClientlessReputationConnection {
            return instances.computeIfAbsent(server) {
                mutableMapOf(discordUser to ClientlessReputationConnection(it, discordUser))
            }.computeIfAbsent(discordUser) { ClientlessReputationConnection(server, it) }
        }

        operator fun get(server: DiscordServerModel, discordUser: DiscordUserModel): ClientlessReputationConnection {
            return get(server.id, discordUser.id)
        }

        operator fun get(member: Member): ClientlessReputationConnection {
            return get(member.guild.id.value.toLong(), member.id.value.toLong())
        }

        class ClientlessReputationConnection(val server: Long, val discordUser: Long) :
            ClientlessConnection<ReputationConnection> {
            override fun authenticated(authenticationProvider: AuthenticationProvider): ReputationConnection {
                return ReputationConnection(server, discordUser, AuthenticatedClient(authenticationProvider))
            }
        }
    }
}