package com.donald.musictheoryapp.music.musicxml

import org.dom4j.Element

class Barline(var barStyle: Int) {

    object BarStyle {
        const val REGULAR = 0
        const val LIGHT_HEAVY = 1
        const val LIGHT_LIGHT = 2
        val STRINGS = arrayOf(
            "regular",
            "light-heavy",
            "light-light"
        )

        @JvmStatic
        fun fromString(barlineString: String): Int {
            for (i in STRINGS.indices) {
                if (barlineString == STRINGS[i]) return i
            }
            return -1
        }
    }

    fun addToXml(measure: Element) {
        measure.addElement("barline").apply {
            addElement("bar-style").addText(BarStyle.STRINGS[barStyle])
        }
    }

    fun clone(): Barline {
        return Barline(barStyle)
    }

}