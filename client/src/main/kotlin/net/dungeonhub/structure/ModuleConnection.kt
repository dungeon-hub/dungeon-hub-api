package net.dungeonhub.structure

import net.dungeonhub.connection.AuthorizationConnection
import net.dungeonhub.connection.DungeonHubConnection.apiUrl
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request

interface ModuleConnection : Connection {
    val moduleApiPrefix: String?

    fun getApiRequest(uri: String?): Request.Builder {
        return getApiRequest(getApiUrl(uri).build())
    }

    fun getApiRequest(httpUrl: HttpUrl): Request.Builder {
        val mediaType: MediaType = "multipart/form-data; boundary=---011000010111000001101001".toMediaType()

        return Request.Builder()
            .url(httpUrl)
            .addHeader("Content-Type", mediaType.toString())
            .addHeader("Authorization", "Bearer " + AuthorizationConnection.apiToken)
    }

    fun getApiUrl(): HttpUrl.Builder = getApiUrl("")

    fun getApiUrl(id: Long): HttpUrl.Builder {
        return getApiUrl(id.toString())
    }

    fun getApiUrl(uri: String?): HttpUrl.Builder {
        val prefix = if ((moduleApiPrefix == null || moduleApiPrefix!!.isBlank()))
            ""
        else
            moduleApiPrefix + (if (uri == null || uri.isBlank()) "" else "/")

        return (apiUrl + apiPrefix + prefix + uri).toHttpUrl()
            .newBuilder()
    }

    val apiPrefix: String
        get() = "api/v1/"
}