package com.donald.musictheoryapp.util

class Task<T> {

    private var value: T? = null
    //private var completed = false
    private var onValueSet: ((T) -> Unit)? = null
    private var child: Task<*>? = null
    var isCancelled = false
        private set

    fun complete(value: T) {
        if (isCancelled) return
        //if (completed) throw IllegalStateException("'complete' function called more than once")
        //completed = true
        if (value != null) throw IllegalStateException("'complete' function called more than once")
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

    fun finally(callback: (T) -> Unit) {
        if (onValueSet != null) throw IllegalStateException("'then' function called more than once")
        //val completed = this.completed
        //if (completed) {
        val value = this.value
        if (value != null) {
            // when the result is ready, call it
            callback(value)
        } else {
            // when the result is not ready
            // store it first so that when the result is ready, it can be called
            onValueSet = callback
        }
    }

    fun <V> then(callback: (T) -> V): Task<V> {
        if (onValueSet != null) throw IllegalStateException("'then' function called more than once")
        //val completed = this.completed
        val thisValue = this.value
        //return if (completed) {
        return if (thisValue != null) {
            val task = Task<V>()
            task.complete(callback(thisValue))
            this.child = task
            task
        } else {
            val task = Task<V>()
            onValueSet = { value -> task.complete(callback(value)) }
            this.child = task
            task
        }
    }

    fun cancel() {
        isCancelled = true
        child?.isCancelled = true
    }

}