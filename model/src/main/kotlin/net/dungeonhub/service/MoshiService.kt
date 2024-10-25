package net.dungeonhub.service

import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.awt.Color
import java.time.Instant

object MoshiService {
    //TODO add type adapter for kord embeds ?
    val moshi: Moshi = Moshi.Builder()
        .add(Instant::class.java, InstantAdapter())
        .add(Color::class.java, ColorAdapter())
        .addLast(KotlinJsonAdapterFactory())
        .build()

    class InstantAdapter : JsonAdapter<Instant>() {
        override fun toJson(writer: JsonWriter, instant: Instant?) {
            if(instant == null) {
                writer.nullValue()
                return
            }

            writer.value(instant.toEpochMilli())
        }

        override fun fromJson(reader: JsonReader): Instant? {
            if(reader.peek() == JsonReader.Token.NULL) {
                reader.nextNull<Instant>()
                return null
            }

            return Instant.ofEpochMilli(reader.nextLong())
        }
    }

    class ColorAdapter : JsonAdapter<Color>() {
        override fun fromJson(reader: JsonReader): Color? {
            if(reader.peek() == JsonReader.Token.NULL) {
                reader.nextNull<Color>()
                return null
            }

            val hexString = reader.nextString()

            //If the String doesn't start with a # this wasn't encoded with this adapter
            // --> This might have come from the old library and is just not hex encoded
            if(!hexString.startsWith("#")) {
                return Color.decode(hexString)
            }

            val colorValue = Integer.parseUnsignedInt(hexString.substring(1), 16)

            return Color.decode(colorValue.toString())
        }

        override fun toJson(writer: JsonWriter, color: Color?) {
            if(color == null) {
                writer.nullValue()
                return
            }

            writer.value("#${Integer.toHexString(
                color.red * 256 * 256 + color.green * 256 + color.blue
            ).uppercase()}")
        }
    }
}