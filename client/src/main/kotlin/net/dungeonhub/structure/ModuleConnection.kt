package net.dungeonhub.structure

import io.ktor.http.Url
import net.dungeonhub.client.DungeonHubClient.Companion.apiUrl

interface ModuleConnection : Connection {
    val moduleApiPrefix: String?

    fun getApiUrl(): Url = getApiUrl("")

    fun getApiUrl(id: Long): Url {
        return getApiUrl(id.toString())
    }

    fun getApiUrl(uri: String?): Url {
        val prefix = if ((moduleApiPrefix == null || moduleApiPrefix!!.isBlank()))
            ""
        else
            moduleApiPrefix + (if (uri.isNullOrBlank()) "" else "/")

        return Url(apiUrl + apiPrefix + prefix + uri)
    }

    val apiPrefix: String
        get() = "api/v1/"
}