package com.donald.musictheoryapp.music.musicxml

// fifths in order: F -> C -> G -> D -> A -> E -> B

enum class Step(
    val fifths: Int,
    val jsonValue: String,
    val xmlValue: String,
) {

    C(1, "C", "C"),
    D(3, "D", "D"),
    E(5, "E", "E"),
    F(0, "F", "F"),
    G(2, "G", "G"),
    A(4, "A", "A"),
    B(6, "B", "B");

    companion object {

        val valuesInFifths = arrayOf(F, C, G, D, A, E, B)

        fun fromXmlValue(xmlValue: String): Step {
            values().forEach {
                if (it.xmlValue == xmlValue) return it
            }
            throw IllegalArgumentException()
        }

        fun fromJsonValue(jsonValue: String): Step {
            values().forEach {
                if (it.jsonValue == jsonValue) return it
            }
            throw IllegalArgumentException()
        }

    }

}