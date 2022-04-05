package com.donald.musictheoryapp.util

sealed class Result<out V, out E> {

    abstract val value: V
    abstract val error: E
    abstract val isValue: Boolean
    abstract val isError: Boolean

}

class Val<V>(
    override val value: V
) : Result<V, Nothing>() {

    override val error: Nothing
        get() = throw IllegalAccessError("No error")
    override val isValue = true
    override val isError = false

}

class Err<E>(
    override val error: E
) : Result<Nothing, E>() {

    override val value: Nothing
        get() = throw IllegalAccessError("No value")
    override val isValue = false
    override val isError = true

}