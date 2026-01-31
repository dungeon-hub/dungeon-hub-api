package net.dungeonhub.structure

import io.ktor.client.request.HttpRequestBuilder
import net.dungeonhub.client.DungeonHubClient

abstract class AuthenticatedModuleConnection(client: DungeonHubClient) : AuthenticatedConnection(client), ModuleConnection {
    suspend inline fun <reified T> dhApiRequest(uri: String, noinline request: HttpRequestBuilder.() -> Unit) = client.dhApiRequest<T>(uri, request, this)
    suspend inline fun <reified T> dhApiRequest(uri: Long, noinline request: HttpRequestBuilder.() -> Unit) = client.dhApiRequest<T>(uri, request, this)
    suspend inline fun <reified T> dhApiRequest(noinline request: HttpRequestBuilder.() -> Unit) = client.dhApiRequest<T>(request, this)
}