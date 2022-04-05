package com.donald.musictheoryapp.music.musicxml

enum class Accidental(
    val xmlValue: String,
    val jsonValue: String
) {

    FLAT_FLAT("flat-flat", "flat-flat"),
    FLAT("flat", "flat"),
    NATURAL("natural", "natural"),
    SHARP("sharp", "sharp"),
    SHARP_SHARP("sharp-sharp", "sharp-sharp");

    val alter: Int
        get() = ordinal - values().size / 2

    companion object {

        fun fromXmlValue(xmlValue: String): Accidental {
            values().forEach { if (it.xmlValue == xmlValue) return it }
            throw IllegalArgumentException()
        }

        fun fromJsonValue(jsonValue: String): Accidental {
            values().forEach { if (it.jsonValue == jsonValue) return it }
            throw IllegalArgumentException()
        }
    }

}