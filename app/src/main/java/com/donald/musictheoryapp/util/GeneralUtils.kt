package com.donald.musictheoryapp.util

import java.util.*

/*
inline fun <reified E: Exception, reified R: Any> tryOrNull(block: () -> R): R? {
    return try {
        block()
    } catch (e: E) {

    }
}

 */

fun <T> toggle(currentValue: T, firstValue: T, secondValue: T) = when (currentValue) {
    firstValue -> secondValue
    secondValue -> firstValue
    else -> throw IllegalStateException()
}

fun toggle(currentValue: Boolean): Boolean = !currentValue