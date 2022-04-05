package com.donald.musictheoryapp.music.musicxml

/**
 * @param [baseAbsStep] This is the absolute step of the note at position 0 (on the first line of the staff)
 */

enum class Sign(
    val baseAbsStep: Int,
    val xmlValue: String,
    val jsonValue: String
) {

    F(Step.F.ordinal + 3 * Step.values().size, "F", "F"),
    C(Step.C.ordinal + 4 * Step.values().size, "C", "C"),
    G(Step.G.ordinal + 4 * Step.values().size, "G", "G");

    companion object {

        fun fromXmlValue(xmlValue: String): Sign {
            values().forEach {
                if (it.xmlValue == xmlValue) return it
            }
            throw IllegalArgumentException()
        }

        fun fromJsonValue(jsonValue: String): Sign {
            values().forEach {
                if (it.jsonValue == jsonValue) return it
            }
            throw IllegalArgumentException()
        }

    }

}