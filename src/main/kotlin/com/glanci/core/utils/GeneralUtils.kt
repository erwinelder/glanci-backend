package com.glanci.core.utils

import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlin.enums.enumEntries


suspend inline fun <reified T : Any> RoutingCall.receiveOrNull(): T? {
    return runCatching { receive<T>() }.getOrNull()
}


inline fun <reified T : Enum<T>> enumValueOrNull(name: String): T? {
    return enumEntries<T>().find { it.name == name }
}


fun <T, V> List<T>.excludeItems(items: List<T>, keySelector: (T) -> V): List<T> {
    return this.filter { item ->
        items.none { keySelector(item) == keySelector(it) }
    }
}

fun <T1, T2> List<T1>.excludeItems(items: List<T2>, keyComparator: (T1, T2) -> Boolean): List<T1> {
    return this.filter { item ->
        items.none { keyComparator(item, it) }
    }
}