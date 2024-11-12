package net.dungeonhub.connection

import com.squareup.moshi.adapter
import net.dungeonhub.model.discord_role.DiscordRoleCreationModel
import net.dungeonhub.model.discord_role.DiscordRoleModel
import net.dungeonhub.model.discord_role.DiscordRoleUpdateModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.service.MoshiService.moshi
import net.dungeonhub.structure.ModuleConnection
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

@OptIn(ExperimentalStdlibApi::class)
class DiscordRoleConnection(private val server: Long) : ModuleConnection {
    override val moduleApiPrefix = "server/$server/roles"

    fun getById(id: Long): DiscordRoleModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request) { json: String -> DiscordRoleModel.Companion.fromJson(json) }
    }

    fun addNewRole(creationModel: DiscordRoleCreationModel): DiscordRoleModel? {
        val url: HttpUrl = getApiUrl().build()

        val requestBody: RequestBody = creationModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .post(requestBody)
            .build()

        return executeRequest(request) { json: String -> DiscordRoleModel.Companion.fromJson(json) }
    }

    fun updateRole(id: Long, updateModel: DiscordRoleUpdateModel): DiscordRoleModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val requestBody = updateModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .put(requestBody)
            .build()

        return executeRequest(request) { json: String -> DiscordRoleModel.Companion.fromJson(json) }
    }

    val allRoles: List<DiscordRoleModel>?
        get() {
            val url: HttpUrl = getApiUrl("all").build()

            val request: Request = getApiRequest(url).get().build()

            return executeRequest(request, moshi.adapter<List<DiscordRoleModel>>()::fromJson)
        }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DiscordRoleConnection::class.java)
        private val instances: MutableMap<Long, DiscordRoleConnection> = HashMap()

        operator fun get(server: Long): DiscordRoleConnection {
            return instances.computeIfAbsent(server) { DiscordRoleConnection(it) }
        }
    }
}