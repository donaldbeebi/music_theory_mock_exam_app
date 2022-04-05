package com.donald.musictheoryapp.util

class Promise<T: Any> {

    private var value: T? = null
    private var completed = false
    private var onValueSet: ((T?) -> Unit)? = null

    fun complete(value: T?) {
        if (completed) throw IllegalStateException("'complete' function called more than once")
        completed = true
        val onValueSet = this.onValueSet
        if (onValueSet == null) {
            // when the result is ready but the 'then' function is not set yet
            // store it first so that it can be retrieved later when 'then' is called
            this.value = value
        } else {
            // when the result is ready and the 'then' function is set already
            onValueSet(value)
        }
    }

    fun then(callback: (T?) -> Unit) {
        if (onValueSet != null) throw IllegalStateException("'then' function called more than once")
        val completed = this.completed
        if (completed) {
            // when the result is ready, call it
            callback(value)
        } else {
            // when the result is not ready
            // store it first so that when the result is ready, it can be called
            onValueSet = callback
        }
    }

    fun <V: Any> then(callback: (T?) -> V): Promise<V> {
        if (onValueSet != null) throw IllegalStateException("'then' function called more than once")
        val completed = this.completed
        val thisValue = this.value
        return if (completed) {
            val promise = Promise<V>()
            promise.complete(callback(thisValue))
            promise
        } else {
            val promise = Promise<V>()
            onValueSet = { value -> promise.complete(callback(value)) }
            promise
        }
    }

}