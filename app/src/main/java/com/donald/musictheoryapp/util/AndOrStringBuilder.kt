package com.donald.musictheoryapp.util

class AndOrStringBuilder(private val word: String) {

    private val stringsBuffer = ArrayList<String>()
    private val stringBuilder = StringBuilder()

    fun append(value: Any): AndOrStringBuilder {
        stringsBuffer += value.toString()
        return this
    }

    fun build(): String {
        val string = when (stringsBuffer.size) {
            0 -> {
                ""
            }
            1 -> {
                stringsBuffer[0]
            }
            2 -> {
                stringBuilder
                    .append(stringsBuffer[0])
                    .append(" ").append(word).append(" ")
                    .append(stringsBuffer[1]).toString()
            }
            else -> {
                stringsBuffer.forEachIndexed { index, string ->
                    stringBuilder.append(string)
                    if (index != stringsBuffer.lastIndex) stringBuilder.append(", ")
                    if (index == stringsBuffer.lastIndex - 1) stringBuilder.append(word).append(" ")
                }
                stringBuilder.toString()
            }
        }
        stringsBuffer.clear()
        stringBuilder.clear()
        return string
    }

}

fun buildString(items: List<Any>, word: String): String {
    val stringBuilder = StringBuilder()
    val string = when (items.size) {
        0 -> {
            ""
        }
        1 -> {
            items[0].toString()
        }
        2 -> {
            stringBuilder
                .append(items[0])
                .append(" ").append(word).append(" ")
                .append(items[1]).toString()
        }
        else -> {
            items.forEachIndexed { index, string ->
                stringBuilder.append(string)
                if (index != items.lastIndex) stringBuilder.append(", ")
                if (index == items.lastIndex - 1) stringBuilder.append(word).append(" ")
            }
            stringBuilder.toString()
        }
    }
    return string

}