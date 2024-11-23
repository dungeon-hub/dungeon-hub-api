package net.dungeonhub.structure

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import net.dungeonhub.service.MoshiService
import org.slf4j.Logger
import java.lang.reflect.Type

interface ExternalConnection : Connection {
    val logger: Logger

    fun getMoshi(): Moshi {
        return MoshiService.moshi
    }

    fun <T> fromJson(json: String, clazz: Class<T>): T {
        return getMoshi().adapter(clazz).fromJson(json)!!
    }

    fun <T> fromJson(json: String, typeOfT: Type): T {
        return getMoshi().adapter<T>(typeOfT).fromJson(json)!!
    }
}

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T> ExternalConnection.toJson(entity: T): String {
    return getMoshi().adapter<T>().toJson(entity)
}