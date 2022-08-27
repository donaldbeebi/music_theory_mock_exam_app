package com.donald.musictheoryapp.music.musicxml

enum class Accidental(
    val xmlValue: String,
    val jsonValue: String
) {

    FlatFlat("flat-flat", "flat-flat"),
    Flat("flat", "flat"),
    Natural("natural", "natural"),
    Sharp("sharp", "sharp"),
    SharpSharp("sharp-sharp", "sharp-sharp");

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