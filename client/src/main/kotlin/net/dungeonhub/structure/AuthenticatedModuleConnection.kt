package net.dungeonhub.structure

import net.dungeonhub.client.DungeonHubClient
import okhttp3.HttpUrl

abstract class AuthenticatedModuleConnection(client: DungeonHubClient) : AuthenticatedConnection(client), ModuleConnection {
    override fun getApiRequest(httpUrl: HttpUrl) = client.getApiRequest(httpUrl)
}