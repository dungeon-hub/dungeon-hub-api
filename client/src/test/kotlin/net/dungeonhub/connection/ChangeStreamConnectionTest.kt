package net.dungeonhub.connection

import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.DungeonHubClient
import net.dungeonhub.connection.ChangeStreamConnection.ChangeStreamEvent
import net.dungeonhub.enums.ChangeAction
import net.dungeonhub.enums.ChangeEventType
import net.dungeonhub.model.score.ScoreModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.InetSocketAddress
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.seconds

class ChangeStreamConnectionTest {
    private lateinit var server: HttpServer
    private val connections = AtomicInteger()
    private val requests = CopyOnWriteArrayList<Request>()

    private data class Request(val authorization: String?, val query: String?, val lastEventId: String?)

    @BeforeEach
    fun startServer() {
        server = HttpServer.create(InetSocketAddress("localhost", 0), 0)
        server.createContext("/api/v1/changes") { exchange ->
            val lastEventId = exchange.requestHeaders.getFirst("Last-Event-ID")
            requests.add(Request(exchange.requestHeaders.getFirst("Authorization"), exchange.requestURI.query, lastEventId))

            exchange.responseHeaders.add("Content-Type", "text/event-stream")
            exchange.sendResponseHeaders(200, 0)

            exchange.responseBody.use { body ->
                val connection = connections.incrementAndGet()

                body.write("event: Connected\ndata: {\"resumed\":${lastEventId != null}}\n\n".toByteArray())
                body.write(": heartbeat\n\n".toByteArray())
                body.write("id: stream:${connection * 2 - 1}\nevent: Score\ndata: ${scoreChange(connection)}\n\n".toByteArray())
                body.write("id: stream:${connection * 2}\nevent: SomeFutureType\ndata: {}\n\n".toByteArray())
                body.flush()
                // closing the body ends the stream, which the client should handle by reconnecting
            }
        }
        server.start()

        DungeonHubClient.apiUrl = "http://localhost:${server.address.port}/"
    }

    @AfterEach
    fun stopServer() {
        server.stop(0)
    }

    @Test
    fun testChangesAreReceivedAndTheStreamResumesAfterReconnecting() = runBlocking {
        val connection = ChangeStreamConnection.authenticated(object : AuthenticationProvider {
            override suspend fun getApiToken() = "test-token"
        })

        val events = withTimeout(20.seconds) {
            connection.changes(setOf(ChangeEventType.Score, ChangeEventType.Ticket)).take(4).toList()
        }

        assertEquals(ChangeStreamEvent.Connected(resumed = false), events[0])
        assertEquals(1L, assertIs<ScoreModel>(assertIs<ChangeStreamEvent.Change>(events[1]).change.data).scoreAmount)
        assertEquals(ChangeStreamEvent.Connected(resumed = true), events[2])

        val secondChange = assertIs<ChangeStreamEvent.Change>(events[3]).change
        assertEquals(ChangeAction.Updated, secondChange.action)
        assertEquals(2L, assertIs<ScoreModel>(secondChange.data).scoreAmount)

        assertEquals(Request("Bearer test-token", "types=Score,Ticket", null), requests[0])
        // the id of the last event is sent on reconnect, also if the event had an unknown type
        assertEquals("stream:2", requests[1].lastEventId)
    }

    private fun scoreChange(scoreAmount: Int): String {
        return """{"type":"Score","action":"Updated","serverId":1,"data":{"carrier":{"id":42},"carryType":null,"scoreType":"Default","scoreAmount":$scoreAmount}}"""
    }
}
