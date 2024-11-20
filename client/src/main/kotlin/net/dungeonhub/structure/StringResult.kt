package net.dungeonhub.structure

class StringResult(val code: Int, val result: String?) {
    val successResult = if (code in 200..299) result else null
}