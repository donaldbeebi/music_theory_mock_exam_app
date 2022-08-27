package com.donald.musictheoryapp.util

sealed class Result<out V, out E> {
    class Value<out V>(val value: V) : Result<V, Nothing>()
    class Error<out E>(val error: E) : Result<Nothing, E>()
}

@Deprecated("Use the regular when expression instead")
inline fun <V, E> Result<V, E>.get(onValue: (V) -> Unit, onError: (E) -> Unit) {
    when (this) {
        is Result.Value -> onValue(value)
        is Result.Error -> onError(error)
    }
}

inline infix fun <V, E> Result<V, E>.otherwise(block: (E) -> Nothing): V {
    when (this) {
        is Result.Value -> return value
        is Result.Error -> block(error)
    }
}

infix fun <V> Result<V, *>.otherwise(defaultValue: V): V {
    return when (this) {
        is Result.Value -> value
        else -> defaultValue
    }
}

fun <V> Result<V, *>.valueOrNull(): V? {
    return when (this) {
        is Result.Value -> value
        else -> null
    }
}

fun <V> Result<V, *>.valueOrThrow(errorMessage: String = "Result is error"): V {
    return when (this) {
        is Result.Value -> value
        else -> throw IllegalStateException(errorMessage)
    }
}

fun <E> Result<*, E>.errorOrNull(): E? {
    return when (this) {
        is Result.Error -> error
        else -> null
    }
}

inline fun <V, E> Result<V, E>.onValue(block: (V) -> Unit): Result<V, E> {
    if (this is Result.Value) { block(value) }
    return this
}

inline fun <V, E> Result<V, E>.onError(block: (E) -> Unit): Result<V, E> {
    if (this is Result.Error) { block(error) }
    return this
}