package net.dungeonhub.structure

import java.nio.charset.StandardCharsets

class RequestResult(val code: Int, val result: ByteArray?) {
    val successResult = if (code in 200..299) result else null

    val stringResult = StringResult(
        code,
        result?.let { String(it, StandardCharsets.UTF_8) }
    )
}