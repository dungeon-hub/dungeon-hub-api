package net.dungeonhub.structure

import net.dungeonhub.connection.DungeonHubConnection
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request

interface Connection {
    val jsonMediaType: MediaType
        get() = "application/json; charset=utf-8".toMediaType()

    fun <T> executeRequest(request: Request, function: MappingFunction<String, T>): T? {
        return DungeonHubConnection.executeRequest(request, function)
    }

    fun executeRequest(request: Request): String? {
        return DungeonHubConnection.executeRequest(request)
    }
}