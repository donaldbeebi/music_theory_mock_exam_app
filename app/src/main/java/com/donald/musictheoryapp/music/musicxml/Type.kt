package com.donald.musictheoryapp.music.musicxml

enum class Type(val xmlValue: String, val jsonValue: String) {

    BREVE("breve", "breve"),
    WHOLE("whole", "whole"),
    HALF("half", "half"),
    QUARTER("quarter", "quarter"),
    EIGHTH("eighth", "eighth"),
    SIXTEENTH("16th", "16th"),
    THIRTY_SECOND("32nd", "32nd"),
    SIXTY_FOURTH("64th", "64th");

    companion object {

        fun fromXmlValue(xmlValue: String): Type {
            values().forEach { if (it.xmlValue == xmlValue) return it }
            throw IllegalArgumentException()
        }

        fun fromJsonValue(jsonValue: String): Type {
            values().forEach { if (it.jsonValue == jsonValue) return it }
            throw IllegalArgumentException()
        }

    }

}