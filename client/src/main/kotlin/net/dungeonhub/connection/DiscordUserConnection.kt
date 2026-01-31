package net.dungeonhub.connection

import dev.kord.core.behavior.UserBehavior
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.client.DungeonHubClient
import net.dungeonhub.model.discord_user.DiscordUserModel
import net.dungeonhub.model.discord_user.DiscordUserUpdateModel
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection
import java.util.*

@OptIn(ExperimentalStdlibApi::class)
class DiscordUserConnection(override val client: DungeonHubClient) : AuthenticatedModuleConnection(client) {
    override val moduleApiPrefix = "discord-users"

    suspend fun countLinkedUsers(): Long? = dhApiRequest("count-linked") {}

    suspend fun getById(id: Long): DiscordUserModel? = dhApiRequest(id) {}

    suspend fun getByIdOrCreate(id: Long) : DiscordUserModel? {
        return getById(id) ?: updateUser(id, DiscordUserUpdateModel(null, null))
    }

    suspend fun getLinkedById(id: Long): DiscordUserModel? {
        return getById(id)?.takeIf { discordUserModel -> discordUserModel.minecraftId != null }
    }

    suspend fun getAll(): List<DiscordUserModel>? = dhApiRequest("all") {}

    suspend fun updateUser(id: Long, updateModel: DiscordUserUpdateModel): DiscordUserModel? = dhApiRequest(id) {
        method = HttpMethod.Put
        setBody(updateModel)
    }

    suspend fun getCarryCount(id: Long, guildId: Long): Int? = dhApiRequest("$id/carries/$guildId") {}

    suspend fun findUserByUuid(uuid: UUID): DiscordUserModel? = dhApiRequest("find") {
        parameter("uuid", uuid)
    }

    companion object : ClientlessConnection<DiscordUserConnection> {
        override fun authenticated(authenticationProvider: AuthenticationProvider): DiscordUserConnection {
            return DiscordUserConnection(AuthenticatedClient(authenticationProvider))
        }

        //TODO move to connection?
        suspend fun UserBehavior.getUUIDOrNull(): UUID? {
            return authenticated().getById(id.value.toLong())?.minecraftId
        }
    }
}