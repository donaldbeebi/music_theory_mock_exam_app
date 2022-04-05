package com.donald.csci3310

import com.donald.musictheoryapp.util.Result
import com.donald.musictheoryapp.util.Val
import org.junit.Test

class ResultTest {

    @Test
    fun test() {

        val result: Result<Int, Exception>
        result = Val(5)

        println(result.isError)

    }

}