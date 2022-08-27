package com.donald.musictheoryapp.music.musicxml

enum class Type(val xmlValue: String, val jsonValue: String) {

    Breve("breve", "breve"),
    Whole("whole", "whole"),
    Half("half", "half"),
    Quarter("quarter", "quarter"),
    Eighth("eighth", "eighth"),
    Sixteenth("16th", "16th"),
    ThirtySecond("32nd", "32nd"),
    SixtyFourth("64th", "64th");

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