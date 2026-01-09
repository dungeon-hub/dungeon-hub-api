package net.dungeonhub.connection

import com.squareup.moshi.adapter
import dev.kord.core.behavior.UserBehavior
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.client.DungeonHubClient
import net.dungeonhub.model.discord_user.DiscordUserModel
import net.dungeonhub.model.discord_user.DiscordUserUpdateModel
import net.dungeonhub.service.MoshiService.moshi
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection
import net.dungeonhub.structure.Connection.Companion.jsonMediaType
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

@OptIn(ExperimentalStdlibApi::class)
class DiscordUserConnection(override val client: DungeonHubClient) : AuthenticatedModuleConnection() {
    override val moduleApiPrefix = "discord-users"

    fun countLinkedUsers(): Long? {
        val url: HttpUrl = getApiUrl("count-linked").build()

        val request: Request = Request.Builder().url(url).build()

        return executeRequest(request, function = java.lang.Long::parseLong)
    }

    fun getById(id: Long): DiscordUserModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request) { json: String -> DiscordUserModel.fromJson(json) }
    }

    fun getByIdOrCreate(id: Long) : DiscordUserModel? {
        return getById(id) ?: updateUser(id, DiscordUserUpdateModel(null))
    }

    fun getLinkedById(id: Long): DiscordUserModel? {
        return getById(id)?.takeIf { discordUserModel -> discordUserModel.minecraftId != null }
    }

    val all: List<DiscordUserModel>?
        get() {
            val url: HttpUrl = getApiUrl("all").build()

            val request: Request = getApiRequest(url)
                .get()
                .build()

            return executeRequest(request, function = moshi.adapter<List<DiscordUserModel>>()::fromJson)
        }

    fun updateUser(id: Long, updateModel: DiscordUserUpdateModel): DiscordUserModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val requestBody = updateModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .put(requestBody)
            .build()

        return executeRequest(request) { json: String -> DiscordUserModel.fromJson(json) }
    }

    fun getCarryCount(id: Long, guildId: Long): Int? {
        val url: HttpUrl = getApiUrl("$id/carries/$guildId").build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request, function = Integer::parseInt)
    }

    fun findUserByUuid(uuid: UUID): DiscordUserModel? {
        val url: HttpUrl = getApiUrl("find")
            .addQueryParameter("uuid", uuid.toString())
            .build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request) { json: String -> DiscordUserModel.fromJson(json) }
    }

    companion object : ClientlessConnection<DiscordUserConnection> {
        override fun authenticated(authenticationProvider: AuthenticationProvider): DiscordUserConnection {
            return DiscordUserConnection(AuthenticatedClient(authenticationProvider))
        }

        //TODO move to connection?
        fun UserBehavior.getUUIDOrNull(): UUID? {
            return authenticated().getById(id.value.toLong())?.minecraftId
        }
    }
}