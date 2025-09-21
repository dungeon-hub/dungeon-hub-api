package net.dungeonhub.structure

import net.dungeonhub.client.DungeonHubClient
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request

interface Connection {
    val client: DungeonHubClient

    fun <T> executeRequest(request: Request, notFoundFallback: T? = null, function: MappingFunction<String, T>): T? {
        val result = client.executeRawRequest(request)?.stringResult

        if (result?.code == 404) {
            return notFoundFallback
        }

        return result?.successResult?.let { function.apply(it) }
    }

    fun executeRequest(request: Request): String? {
        return client.executeRequest(request)
    }

    companion object {
        val jsonMediaType: MediaType
            get() = "application/json; charset=utf-8".toMediaType()
    }
}