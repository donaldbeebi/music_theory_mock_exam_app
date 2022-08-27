package com.donald.csci3310

import com.donald.musictheoryapp.util.AndOrStringBuilder
import org.junit.Test

class AndOrStringBuilderTest {

    class Clazz {
        override fun toString() = "clazz"
    }

    @Test
    fun test() {
        val builder = AndOrStringBuilder("")
        builder.append(1)
        builder.append(2.5F)
        builder.append(Clazz())
        val string = builder.build()
        assert(string == "1, 2.5, or clazz")
        println(string)
    }

}