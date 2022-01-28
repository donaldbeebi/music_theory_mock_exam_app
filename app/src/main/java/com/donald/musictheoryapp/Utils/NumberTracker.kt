package com.donald.musictheoryapp.Utils

class NumberTracker(
    private val target: Int,
    private val onIncrement: () -> Unit,
    private val onTarget: () -> Unit,
) {
    private var count = 0
    private var counting = true

    init {
        if (target == 0) onTarget()
        require(target >= 0)
    }

    fun count(): Int {
        return count
    }

    fun target(): Int {
        return target
    }

    fun increment() {
        if(counting)
        {
            count++
            onIncrement()
            if (target == count) onTarget()
        }
    }

    fun abort() {
        counting = false
    }
}