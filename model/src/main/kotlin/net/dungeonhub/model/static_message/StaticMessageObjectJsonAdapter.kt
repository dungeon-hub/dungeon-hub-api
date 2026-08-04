package net.dungeonhub.model.static_message

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

/**
 * Selects the concrete [StaticMessageObject] variant from the `type` discriminator.
 *
 * Moshi's reflection adapter handles concrete Kotlin classes, but cannot instantiate the sealed interface used as the
 * declared list element type. Keeping this adapter also preserves the compact `type`/`value` wire format.
 */
class StaticMessageObjectJsonAdapter : JsonAdapter<StaticMessageObject>() {
    override fun fromJson(reader: JsonReader): StaticMessageObject {
        var type: String? = null
        var value: Long? = null

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "type" -> type = reader.nextString()
                "value" -> value = reader.nextLong()
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        if (type == "Separator") {
            return StaticMessageObject.Separator
        }

        val objectType = try {
            StaticMessageObjectType.valueOf(type ?: throw JsonDataException("Static message object type is required"))
        } catch (exception: IllegalArgumentException) {
            throw JsonDataException("Unknown static message object type: $type", exception)
        }
        return StaticMessageObject.LinkedObject(
            objectType,
            value ?: throw JsonDataException("Static message object value is required for type $objectType")
        )
    }

    override fun toJson(writer: JsonWriter, value: StaticMessageObject?) {
        if (value == null) {
            writer.nullValue()
            return
        }

        writer.beginObject()
        when (value) {
            is StaticMessageObject.LinkedObject -> {
                writer.name("type").value(value.type.name)
                writer.name("value").value(value.value)
            }
            StaticMessageObject.Separator -> writer.name("type").value("Separator")
        }
        writer.endObject()
    }
}
