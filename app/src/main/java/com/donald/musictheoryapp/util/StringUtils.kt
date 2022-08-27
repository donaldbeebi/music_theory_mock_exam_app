package com.donald.musictheoryapp.util

import java.lang.StringBuilder

fun String.capitalize(): String {
    val builder = StringBuilder()
    for (i in this.indices) {
        val currentChar = this[i]
        if (i == 0 || this[i - 1] == ' ' && Character.isAlphabetic(currentChar.code)) {
            builder.append(Character.toUpperCase(currentChar))
        } else {
            builder.append(currentChar)
        }
    }
    return builder.toString()
}

inline fun String.forEachArg(block: (startIndex: Int, endIndex: Int, content: String) -> Unit) {
    var argStart = false
    var startIndex = 0
    this.forEachIndexed { index, char ->
        when {
            !argStart && char == '{' -> {
                argStart = true
                startIndex = index
            }
            argStart && char == '}' -> {
                val endIndex = index + 1
                block(startIndex, endIndex, this.substring(startIndex + 1, endIndex - 1))
                argStart = false
            }
        }
    }
    require(argStart != true)
}

fun String.getArgs(): List<String> {
    var argStart = false
    val argCount = this.count { it == '{' }
    val args = ArrayList<String>(argCount)
    var startIndex = 0
    this.forEachIndexed { index, char ->
        when {
            !argStart && char == '{' -> {
                argStart = true
                startIndex = index
            }
            argStart && char == '}' -> {
                val endIndex = index + 1
                args += this.substring(startIndex + 1, endIndex - 1)
                argStart = false
            }
        }
    }
    require(argStart != true)
    return args
}