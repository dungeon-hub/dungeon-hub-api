package net.dungeonhub.connection

import com.squareup.moshi.adapter
import net.dungeonhub.model.discord_role_group.DiscordRoleGroupModel
import net.dungeonhub.service.MoshiService.moshi
import net.dungeonhub.structure.ModuleConnection
import okhttp3.HttpUrl
import okhttp3.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

@OptIn(ExperimentalStdlibApi::class)
class DiscordRoleGroupConnection(server: Long) : ModuleConnection {
    override val moduleApiPrefix = "server/$server/role-group"

    val all: List<DiscordRoleGroupModel>?
        get() {
            val url: HttpUrl = getApiUrl("all").build()

            val request: Request = getApiRequest(url).get().build()

            return executeRequest(request, moshi.adapter<List<DiscordRoleGroupModel>>()::fromJson)
        }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DiscordRoleGroupConnection::class.java)
        private val instances: MutableMap<Long, DiscordRoleGroupConnection> = HashMap()

        operator fun get(server: Long): DiscordRoleGroupConnection {
            return instances.computeIfAbsent(server) { DiscordRoleGroupConnection(it) }
        }
    }
}