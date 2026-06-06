package net.dungeonhub.structure

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import net.dungeonhub.client.DungeonHubClient

interface Connection {
    val client: DungeonHubClient

    suspend fun executeRequest(request: HttpRequestBuilder.() -> Unit): HttpResponse? {
        return client.executeRawRequest(request)
    }
}