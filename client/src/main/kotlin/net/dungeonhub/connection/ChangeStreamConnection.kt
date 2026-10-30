package net.dungeonhub.connection

import io.ktor.client.plugins.HttpTimeoutConfig
import io.ktor.client.plugins.sse.sse
import io.ktor.client.plugins.timeout
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.parameter
import io.ktor.client.request.takeFrom
import io.ktor.client.request.url
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.enums.ChangeEventType
import net.dungeonhub.model.change.ChangeEventModel
import net.dungeonhub.providers.HttpClientProvider.httpClient
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection
import net.dungeonhub.structure.model.Model
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Connection to the change stream of the API, which pushes changes of entities to all clients that are allowed to see them.
 */
class ChangeStreamConnection(override val client: AuthenticatedClient) : AuthenticatedModuleConnection(client) {
    override val moduleApiPrefix = "changes"

    /**
     * Listens to the change stream and emits every change the authenticated client is allowed to see.
     *
     * The stream is re-established automatically whenever it is closed, e.g. due to network issues or because the
     * token used for the connection expired. Each (re)connect is signaled with [ChangeStreamEvent.Connected].
     * Changes that happened while the stream was disconnected are not replayed, so clients should (re)load the state
     * they need whenever they receive [ChangeStreamEvent.Connected].
     *
     * @param types the types of changes to listen to, all types if empty
     */
    fun changes(types: Set<ChangeEventType> = emptySet()): Flow<ChangeStreamEvent> = channelFlow {
        var reconnectDelay = MIN_RECONNECT_DELAY

        while (true) {
            try {
                listen(types, this) {
                    reconnectDelay = MIN_RECONNECT_DELAY
                }
                logger.debug("The change stream was closed, reconnecting...")
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                logger.warn("The change stream failed, reconnecting in {}.", reconnectDelay, exception)
            }

            delay(reconnectDelay)
            reconnectDelay = (reconnectDelay * 2).coerceAtMost(MAX_RECONNECT_DELAY)
        }
    }

    private suspend fun listen(types: Set<ChangeEventType>, channel: SendChannel<ChangeStreamEvent>, onConnected: () -> Unit) {
        val request = HttpRequestBuilder()
        request.url(getApiUrl())
        client.setupRequest(request)

        httpClient.sse({
            takeFrom(request)

            if (types.isNotEmpty()) {
                parameter("types", types.joinToString(",") { it.name })
            }

            timeout {
                requestTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
                // the server sends a heartbeat regularly, so a silent connection is considered dead
                socketTimeoutMillis = SOCKET_TIMEOUT.inWholeMilliseconds
            }
        }) {
            onConnected()
            channel.send(ChangeStreamEvent.Connected)

            incoming.collect { event ->
                parseEvent(event)?.let { channel.send(ChangeStreamEvent.Change(it)) }
            }
        }
    }

    private fun parseEvent(event: ServerSentEvent): ChangeEventModel<out Model>? {
        val type = ChangeEventType.fromEventName(event.event)
        val data = event.data

        if (type == null || data == null) {
            logger.debug("Ignoring unknown change event '{}'.", event.event)
            return null
        }

        return try {
            ChangeEventModel.fromJson(type, data)
        } catch (exception: Exception) {
            logger.error("Couldn't parse change event '{}': {}", event.event, data, exception)
            null
        }
    }

    sealed interface ChangeStreamEvent {
        /**
         * The stream was (re)connected, changes that happened before this event were possibly missed.
         */
        data object Connected : ChangeStreamEvent

        class Change(val change: ChangeEventModel<out Model>) : ChangeStreamEvent
    }

    companion object : ClientlessConnection<ChangeStreamConnection> {
        private val logger: Logger = LoggerFactory.getLogger(ChangeStreamConnection::class.java)

        private val MIN_RECONNECT_DELAY: Duration = 1.seconds
        private val MAX_RECONNECT_DELAY: Duration = 1.minutes
        private val SOCKET_TIMEOUT: Duration = 1.minutes

        override fun authenticated(authenticationProvider: AuthenticationProvider): ChangeStreamConnection {
            return ChangeStreamConnection(AuthenticatedClient(authenticationProvider))
        }
    }
}
