package net.dungeonhub.service

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import dev.kord.common.entity.Permissions
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import java.awt.Color
import java.io.IOException
import java.time.Instant

/**
 * This class exposes a gson instance.
 * This should only be used for compatibility purposes with services that can't easily implement Moshi (e.g. Spring Boot).
 * For new services, prefer using Moshi.
 */
object GsonService {
    @Deprecated(
        "Use MoshiService.moshi instead",
        replaceWith = ReplaceWith("net.dungeonhub.service.MoshiService.moshi")
    )
    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Instant::class.java, InstantTypeAdapter())
        .registerTypeAdapter(Color::class.java, ColorTypeAdapter())
        .registerTypeAdapter(Permissions::class.java, PermissionsTypeAdapter())
        .create()

    private class InstantTypeAdapter : TypeAdapter<Instant>() {
        @Throws(IOException::class)
        override fun write(jsonWriter: JsonWriter, instant: Instant?) {
            if (instant == null) {
                jsonWriter.nullValue()
                return
            }

            jsonWriter.value(instant.toEpochMilli())
        }

        @Throws(IOException::class)
        override fun read(jsonReader: JsonReader): Instant? {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull()
                return null
            }

            return Instant.ofEpochMilli(jsonReader.nextLong())
        }
    }

    private class ColorTypeAdapter : TypeAdapter<Color>() {
        @Throws(IOException::class)
        override fun write(jsonWriter: JsonWriter, color: Color?) {
            if (color == null) {
                jsonWriter.nullValue()
                return
            }

            jsonWriter.value(color.red * 256 * 256L + color.green * 256L + color.blue)
        }

        @Throws(IOException::class)
        override fun read(jsonReader: JsonReader): Color? {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull()
                return null
            }

            return Color.decode(jsonReader.nextString())
        }
    }

    private class PermissionsTypeAdapter : TypeAdapter<Permissions>() {
        @Throws(IOException::class)
        override fun write(jsonWriter: JsonWriter, permissions: Permissions?) {
            if (permissions == null) {
                jsonWriter.nullValue()
                return
            }

            val jsonElement = MoshiService.toJsonElement(permissions.code).jsonPrimitive
            jsonWriter.value(jsonElement.long)
        }

        @Throws(IOException::class)
        override fun read(jsonReader: JsonReader): Permissions? {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull()
                return null
            }

            val content = kotlinx.serialization.json.JsonPrimitive(jsonReader.nextLong())
            val result = MoshiService.discordBitSetFromJsonElement(content)
            return Permissions.Builder(result).build()
        }
    }
}