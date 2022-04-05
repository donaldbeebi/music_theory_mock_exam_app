package com.donald.csci3310

import android.os.Handler
import android.os.Looper
import com.donald.musictheoryapp.util.Promise
import org.junit.Assert
import org.junit.Test
import java.util.*
import kotlin.concurrent.thread

class PromiseTest {

    private val random = Random()

    fun testMain() {
        repeat(100) {
            test()
        }
        Assert.assertTrue(false)
    }

    @Test
     fun test() {
        val initialValue = random.nextInt(1000)

        val promise: Promise<Int> = doSomeWork()
        promise.then<Int> { value ->
            value ?: throw IllegalStateException()
            println("value: $value; initialValue: $initialValue")
            Assert.assertTrue(value == initialValue)
            value + 1
        }.then<String> { value ->
            value ?: throw IllegalStateException()
            Assert.assertTrue(value == initialValue + 1)
            (value + 2).toString()
        }.then { value ->
            value ?: throw IllegalStateException()
            Assert.assertTrue(value == (initialValue + 3).toString())
        }

        Thread.sleep(2000)
        promise.complete(initialValue)
    }

    private fun doSomeWork(): Promise<Int> {
        val promise = Promise<Int>()
        return promise
    }

}