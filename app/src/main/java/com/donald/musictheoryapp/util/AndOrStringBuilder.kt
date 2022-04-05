package com.donald.musictheoryapp.util

class AndOrStringBuilder(private val word: Word) {

    enum class Word(val string: String) { OR("or"), AND("and") }

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
                    .append(" ").append(word.string).append(" ")
                    .append(stringsBuffer[1]).toString()
            }
            else -> {
                stringsBuffer.forEachIndexed { index, string ->
                    stringBuilder.append(string)
                    if (index != stringsBuffer.lastIndex) stringBuilder.append(", ")
                    if (index == stringsBuffer.lastIndex - 1) stringBuilder.append(word.string).append(" ")
                }
                stringBuilder.toString()
            }
        }
        stringsBuffer.clear()
        stringBuilder.clear()
        return string
    }

}