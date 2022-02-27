package com.donald.musictheoryapp.Utils

import java.util.*

data class Time(var millis: Long) {

    var seconds: Int
        get() = (millis / 1000).toInt()
        set(seconds) { millis = seconds.toLong() * 1000 }
    var minutes: Int
        get() = (millis / (60 * 1000)).toInt()
        set(minutes) { millis = minutes.toLong() * 60 * 1000 }
    var hours: Int
        get() = (millis / (60 * 60 * 1000)).toInt()
        set(hours) { millis = hours.toLong() * 60 * 60 * 1000 }

    override fun toString(): String {
        return String.format("%d:%02d:%02d", hours, minutes % 60, seconds % 60)
    }

    companion object {

        val Long.ms get() = Time(this)

        val Int.sec get() = Time(this.toLong() * 1000)

        val Int.min get() = Time(this.toLong() * 60 * 1000)

        val Int.hr get() = Time(this.toLong() * 60 * 60 * 1000)

    }

}