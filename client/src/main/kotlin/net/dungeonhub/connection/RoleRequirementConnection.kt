package net.dungeonhub.connection

import com.squareup.moshi.adapter
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.model.role_requirement.RoleRequirementModel
import net.dungeonhub.service.MoshiService.moshi
import net.dungeonhub.structure.ModuleConnection
import okhttp3.HttpUrl
import okhttp3.Request

@OptIn(ExperimentalStdlibApi::class)
class RoleRequirementConnection(server: Long) : ModuleConnection {
    override val moduleApiPrefix = "server/$server/role-requirement"

    fun getById(id: Long): RoleRequirementModel? {
        val url = getApiUrl(id).build()

        val request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request) { json: String -> RoleRequirementModel.Companion.fromJson(json) }
    }

    val allRoleRequirements: List<RoleRequirementModel>?
        get() {
            val url: HttpUrl = getApiUrl("all").build()

            val request: Request = getApiRequest(url).get().build()

            return executeRequest(request, function = moshi.adapter<List<RoleRequirementModel>>()::fromJson)
        }

    companion object {
        private val instances: MutableMap<Long, RoleRequirementConnection> = HashMap()

        operator fun get(server: Long): RoleRequirementConnection {
            return instances.computeIfAbsent(server) { RoleRequirementConnection(it) }
        }

        operator fun get(server: DiscordServerModel): RoleRequirementConnection {
            return get(server.id)
        }
    }
}