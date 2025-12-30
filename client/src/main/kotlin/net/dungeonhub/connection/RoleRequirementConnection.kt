package net.dungeonhub.connection

import com.squareup.moshi.adapter
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.model.discord_server.DiscordServerModel
import net.dungeonhub.model.role_requirement.RoleRequirementCreationModel
import net.dungeonhub.model.role_requirement.RoleRequirementModel
import net.dungeonhub.model.role_requirement.RoleRequirementUpdateModel
import net.dungeonhub.service.MoshiService.moshi
import net.dungeonhub.structure.ClientlessConnection
import net.dungeonhub.structure.Connection.Companion.jsonMediaType
import net.dungeonhub.structure.ModuleConnection
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@OptIn(ExperimentalStdlibApi::class)
class RoleRequirementConnection(server: Long, override val client: AuthenticatedClient) : ModuleConnection {
    override val moduleApiPrefix = "server/$server/role-requirement"

    fun getById(id: Long): RoleRequirementModel? {
        val url = getApiUrl(id).build()

        val request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request) { json: String -> RoleRequirementModel.fromJson(json) }
    }

    val allRoleRequirements: List<RoleRequirementModel>?
        get() {
            val url: HttpUrl = getApiUrl("all").build()

            val request: Request = getApiRequest(url).get().build()

            return executeRequest(request, function = moshi.adapter<List<RoleRequirementModel>>()::fromJson)
        }

    //TODO dedicated endpoint?
    fun getByRoleId(id: Long): RoleRequirementModel? {
        return allRoleRequirements?.firstOrNull { roleRequirement: RoleRequirementModel ->
            roleRequirement.discordRole.id == id
        }
    }

    fun addNewRoleRequirement(creationModel: RoleRequirementCreationModel): RoleRequirementModel? {
        val url = getApiUrl().build()

        val requestBody = creationModel.toJson().toRequestBody(jsonMediaType)

        val request = getApiRequest(url).post(requestBody).build()

        return executeRequest(request) { json: String -> RoleRequirementModel.fromJson(json) }
    }

    fun updateRoleRequirement(id: Long, updateModel: RoleRequirementUpdateModel): RoleRequirementModel? {
        val url = getApiUrl(id).build()

        val requestBody = updateModel.toJson().toRequestBody(jsonMediaType)

        val request = getApiRequest(url).put(requestBody).build()

        return executeRequest(request) { json: String -> RoleRequirementModel.fromJson(json) }
    }

    fun deleteRoleRequirement(roleRequirement: RoleRequirementModel): RoleRequirementModel? {
        val url = getApiUrl(roleRequirement.id).build()

        val request = getApiRequest(url).delete().build()

        return executeRequest(request) { json: String -> RoleRequirementModel.fromJson(json) }
    }

    companion object {
        private val instances: MutableMap<Long, ClientlessRoleRequirementConnection> = HashMap()

        operator fun get(server: Long): ClientlessRoleRequirementConnection {
            return instances.computeIfAbsent(server) { ClientlessRoleRequirementConnection(it) }
        }

        operator fun get(server: DiscordServerModel): ClientlessRoleRequirementConnection {
            return get(server.id)
        }

        class ClientlessRoleRequirementConnection(val server: Long) : ClientlessConnection<RoleRequirementConnection> {
            override fun authenticated(authenticationProvider: AuthenticationProvider): RoleRequirementConnection {
                return RoleRequirementConnection(server, AuthenticatedClient(authenticationProvider))
            }
        }
    }
}