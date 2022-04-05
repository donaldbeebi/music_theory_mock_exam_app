package com.donald.musictheoryapp.util

class NumberTracker {

    var target: Int? = null
        set(target) {
            if (active) throw IllegalStateException("Tracker is active")
            field = target
        }

    var onIncrement: (() -> Unit)? = null
        set(callback) {
            if (active) throw IllegalStateException("Tracker is active")
            field = callback
        }

    var onTarget: (() -> Unit)? = null
        set(callback) {
            if (active) throw IllegalStateException("Tracker is active")
            field = callback
        }

    var count = 0
        private set

    var active = false
        private set


    fun activate() {
        val target = target ?: throw IllegalStateException("Target is null")
        require(target >= 0)
        if (target == 0) onTarget?.invoke()
        count = 0
        active = true
    }

    fun increment() {
        if (!active) throw IllegalStateException("Tracker is not active")
        count++
        onIncrement?.invoke()
        if (target == count) onTarget?.invoke()
    }

    fun abort() {
        active = false
    }

}