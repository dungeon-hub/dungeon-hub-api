package net.dungeonhub.service

import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.awt.Color
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class MoshiServiceTest {
    @Test
    fun testColorAdapter() {
        val whiteColor = Color(255, 255, 255)
        val dungeonHubColor = Color(165, 23, 112)

        val whiteHex = "#FFFFFF"
        val dungeonHubHex = "#A51770"

        val whiteInt = Integer.parseUnsignedInt(whiteHex.substring(1), 16)
        val dungeonHubInt = Integer.parseUnsignedInt(dungeonHubHex.substring(1), 16)

        assertEquals(whiteHex, MoshiService.moshi.adapter(Color::class.java).toJsonValue(whiteColor))
        assertEquals(dungeonHubHex, MoshiService.moshi.adapter(Color::class.java).toJsonValue(dungeonHubColor))

        assertEquals(whiteColor, MoshiService.moshi.adapter(Color::class.java).fromJsonValue(whiteHex))
        assertEquals(dungeonHubColor, MoshiService.moshi.adapter(Color::class.java).fromJsonValue(dungeonHubHex))

        assertEquals(whiteColor, MoshiService.moshi.adapter(Color::class.java).fromJsonValue(whiteInt.toString()))
        assertEquals(
            dungeonHubColor,
            MoshiService.moshi.adapter(Color::class.java).fromJsonValue(dungeonHubInt.toString())
        )
    }

    @Test
    fun testInstantAdapter() {
        val currentMillis = System.currentTimeMillis()
        val currentInstant = Instant.ofEpochMilli(currentMillis)

        val epoch = 0L
        val epochInstant = Instant.ofEpochMilli(epoch)

        assertEquals(currentMillis, MoshiService.moshi.adapter(Instant::class.java).toJsonValue(currentInstant))
        assertEquals(currentInstant, MoshiService.moshi.adapter(Instant::class.java).fromJsonValue(currentMillis))

        assertEquals(epoch, MoshiService.moshi.adapter(Instant::class.java).toJsonValue(epochInstant))
        assertEquals(epochInstant, MoshiService.moshi.adapter(Instant::class.java).fromJsonValue(epoch))
    }

    @Test
    fun testbasicTypes() {
        val numberString = "-42"
        val booleanString = "true"
        val string = "Hello, World!"

        assertEquals(-42, MoshiService.moshi.adapter(Int::class.java).fromJson(numberString))
        assertEquals(true, MoshiService.moshi.adapter(Boolean::class.java).fromJson(booleanString))
        assertEquals(string, MoshiService.moshi.adapter(String::class.java).fromJsonValue(string))

        assertThrows<JsonDataException> {
            MoshiService.moshi.adapter(Int::class.java).fromJsonValue(string)
        }
    }

    @Test
    fun testObjects() {
        val firstJson = "{\"name\":\"Taubsie\"}"
        val incorrectJson = "{name:\"SomeoneElse\"}"

        assertEquals(TestObject("Taubsie"), MoshiService.moshi.adapter(TestObject::class.java).fromJson(firstJson))
        assertNotEquals(
            TestObject("SomeoneElse"),
            MoshiService.moshi.adapter(TestObject::class.java).fromJson(firstJson)
        )

        assertThrows<JsonEncodingException> {
            MoshiService.moshi.adapter(TestObject::class.java).fromJson(incorrectJson)
        }

    }

    @JsonClass(generateAdapter = true)
    data class TestObject(val name: String)
}