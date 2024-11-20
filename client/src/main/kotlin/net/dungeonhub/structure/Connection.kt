package net.dungeonhub.structure

import net.dungeonhub.connection.DungeonHubConnection
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request

interface Connection {
    val jsonMediaType: MediaType
        get() = "application/json; charset=utf-8".toMediaType()

    fun <T> executeRequest(request: Request, notFoundFallback: T? = null, function: MappingFunction<String, T>): T? {
        val result = DungeonHubConnection.executeRawRequest(request)?.stringResult

        if(result?.code == 404) {
            return notFoundFallback
        }

        return result?.result?.let { function.apply(it) }
    }

    fun executeRequest(request: Request): String? {
        return DungeonHubConnection.executeRequest(request)
    }
}