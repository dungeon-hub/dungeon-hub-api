package net.dungeonhub.connection

import io.ktor.client.plugins.HttpTimeoutConfig
import io.ktor.client.plugins.sse.sse
import io.ktor.client.plugins.timeout
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
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
import net.dungeonhub.model.change.ChangeStreamConnectedModel
import net.dungeonhub.providers.HttpClientProvider.streamingHttpClient
import net.dungeonhub.structure.AuthenticatedModuleConnection
import net.dungeonhub.structure.ClientlessConnection
import net.dungeonhub.structure.model.Model
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.random.Random
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
     * When reconnecting, the API sends the changes that were missed in the meantime again, as long as they happened
     * recently. If it couldn't do that (or on the first connect), [ChangeStreamEvent.Connected.resumed] is false and
     * clients should (re)load the state they need.
     *
     * The returned flow is cold: every collection opens its own connection, and cancelling the collecting coroutine
     * (or completing the collection, e.g. with `take`) closes it.
     *
     * @param types the types of changes to listen to, all types if empty
     */
    fun changes(types: Set<ChangeEventType> = emptySet()): Flow<ChangeStreamEvent> = channelFlow {
        var reconnectDelay = MIN_RECONNECT_DELAY
        var lastEventId: String? = null

        while (true) {
            try {
                listen(types, lastEventId, this, onConnected = { reconnectDelay = MIN_RECONNECT_DELAY }) {
                    lastEventId = it
                }
                logger.debug("The change stream was closed, reconnecting...")
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                logger.warn("The change stream failed, reconnecting in up to {}.", reconnectDelay, exception)
            }

            // randomized, so that many clients don't all reconnect at the same time, e.g. after the API restarted
            delay(reconnectDelay * Random.nextDouble(0.5, 1.0))
            reconnectDelay = (reconnectDelay * 2).coerceAtMost(MAX_RECONNECT_DELAY)
        }
    }

    private suspend fun listen(
        types: Set<ChangeEventType>,
        lastEventId: String?,
        channel: SendChannel<ChangeStreamEvent>,
        onConnected: () -> Unit,
        onEventId: (String) -> Unit
    ) {
        val request = HttpRequestBuilder()
        request.url(getApiUrl())
        client.setupRequest(request)

        streamingHttpClient.sse({
            takeFrom(request)

            if (types.isNotEmpty()) {
                parameter("types", types.joinToString(",") { it.name })
            }

            // lets the API send the changes that were missed while reconnecting
            lastEventId?.let { header(LAST_EVENT_ID_HEADER, it) }

            timeout {
                requestTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
                // the server sends a heartbeat regularly, so a silent connection is considered dead
                socketTimeoutMillis = SOCKET_TIMEOUT.inWholeMilliseconds
            }
        }) {
            incoming.collect { event ->
                if (event.event == ChangeStreamConnectedModel.EVENT_NAME) {
                    onConnected()
                    channel.send(ChangeStreamEvent.Connected(parseConnectedEvent(event)?.resumed ?: false))
                    return@collect
                }

                parseChangeEvent(event)?.let { channel.send(ChangeStreamEvent.Change(it)) }

                // also changes of unknown types have an id, which marks how far the stream was received
                event.id?.let(onEventId)
            }
        }
    }

    private fun parseConnectedEvent(event: ServerSentEvent): ChangeStreamConnectedModel? {
        return try {
            event.data?.let { ChangeStreamConnectedModel.fromJson(it) }
        } catch (exception: Exception) {
            logger.error("Couldn't parse the connected event: {}", event.data, exception)
            null
        }
    }

    private fun parseChangeEvent(event: ServerSentEvent): ChangeEventModel<out Model>? {
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
         * The stream was (re)connected.
         *
         * @param resumed true if all changes that happened while the stream was disconnected were sent again,
         * false if changes might have been missed, e.g. on the first connect
         */
        data class Connected(val resumed: Boolean) : ChangeStreamEvent

        class Change(val change: ChangeEventModel<out Model>) : ChangeStreamEvent
    }

    companion object : ClientlessConnection<ChangeStreamConnection> {
        private val logger: Logger = LoggerFactory.getLogger(ChangeStreamConnection::class.java)

        private val MIN_RECONNECT_DELAY: Duration = 1.seconds
        private val MAX_RECONNECT_DELAY: Duration = 1.minutes
        private val SOCKET_TIMEOUT: Duration = 1.minutes
        private const val LAST_EVENT_ID_HEADER = "Last-Event-ID"

        override fun authenticated(authenticationProvider: AuthenticationProvider): ChangeStreamConnection {
            return ChangeStreamConnection(AuthenticatedClient(authenticationProvider))
        }
    }
}
