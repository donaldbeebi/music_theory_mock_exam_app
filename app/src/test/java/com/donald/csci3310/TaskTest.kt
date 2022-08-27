package com.donald.csci3310

import com.donald.musictheoryapp.util.Task
import org.junit.Assert
import org.junit.Test
import java.util.*

class TaskTest {

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

        val task: Task<Int> = doSomeWork()
        task.then<Int> { value ->
            value ?: throw IllegalStateException()
            println("value: $value; initialValue: $initialValue")
            Assert.assertTrue(value == initialValue)
            value + 1
        }.then<String> { value ->
            value ?: throw IllegalStateException()
            Assert.assertTrue(value == initialValue + 1)
            (value + 2).toString()
        }.finally { value ->
            value ?: throw IllegalStateException()
            Assert.assertTrue(value == (initialValue + 3).toString())
        }

        Thread.sleep(2000)
        task.complete(initialValue)
    }

    private fun doSomeWork(): Task<Int> {
        val promise = Task<Int>()
        return promise
    }

}