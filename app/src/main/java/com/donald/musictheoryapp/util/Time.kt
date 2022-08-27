package com.donald.musictheoryapp.util

@JvmInline
value class Time private constructor(val millis: Long) : Comparable<Time> {

    val seconds: Int
        get() = (millis / 1000).toInt()
        //set(seconds) { millis = seconds.toLong() * 1000 }
    val minutes: Int
        get() = (millis / (60 * 1000)).toInt()
        //set(minutes) { millis = minutes.toLong() * 60 * 1000 }
    val hours: Int
        get() = (millis / (60 * 60 * 1000)).toInt()
        //set(hours) { millis = hours.toLong() * 60 * 60 * 1000 }

    override fun toString(): String {
        return String.format("%d:%02d:%02d", hours, minutes % 60, seconds % 60)
    }

    fun toJsonValue(): Long = millis

    companion object {

        val Long.ms get() = Time(this)
        val Int.ms get() = Time(this.toLong())

        val Int.sec get() = Time(this.toLong() * 1000)

        val Int.min get() = Time(this.toLong() * 60 * 1000)

        val Int.hr get() = Time(this.toLong() * 60 * 60 * 1000)

    }

    override fun compareTo(other: Time): Int {
        return (millis - other.millis).toInt()
    }

}