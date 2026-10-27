package net.dungeonhub.connection

import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.runBlocking
import net.dungeonhub.client.DungeonHubClient
import java.net.InetSocketAddress
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DiscordUserConnectionTest {
    private lateinit var server: HttpServer
    private var previousApiUrl: String? = null

    @BeforeTest
    fun setUp() {
        server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/api/v1/discord-users/1/carries/2/by-type") { exchange ->
            val body = """{"10":33,"11":5,"9007199254740993":4294967296}""".toByteArray()
            exchange.responseHeaders.add("Content-Type", "application/json")
            exchange.sendResponseHeaders(200, body.size.toLong())
            exchange.responseBody.use { it.write(body) }
        }
        server.start()

        previousApiUrl = DungeonHubClient.apiUrl
        DungeonHubClient.apiUrl = "http://127.0.0.1:${server.address.port}/"
    }

    @AfterTest
    fun tearDown() {
        DungeonHubClient.apiUrl = previousApiUrl
        server.stop(0)
    }

    @Test
    fun testGetCarryCountsByType() = runBlocking {
        val connection = DiscordUserConnection(DungeonHubClient())

        assertEquals(
            mapOf(10L to 33L, 11L to 5L, 9007199254740993L to 4294967296L),
            connection.getCarryCountsByType(1, 2)
        )
    }
}
