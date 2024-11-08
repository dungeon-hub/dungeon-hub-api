package net.dungeonhub.structure

import java.io.IOException

fun interface MappingFunction<T, R> {
    @Throws(IOException::class)
    fun apply(t: T): R
}