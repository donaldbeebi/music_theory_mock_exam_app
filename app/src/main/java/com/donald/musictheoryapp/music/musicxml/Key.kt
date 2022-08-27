package com.donald.musictheoryapp.music.musicxml

import org.dom4j.Element
import kotlin.Throws
import org.json.JSONException
import org.json.JSONObject
import java.lang.IllegalArgumentException

class Key(val fifths: Int, val mode: Int) {

    object Mode {
        const val MINOR = 0
        const val MAJOR = 1
        private val STRINGS = arrayOf(
            "minor",
            "major"
        )

        fun stringOf(mode: Int): String {
            if (mode < MINOR || mode > MAJOR) throw IllegalArgumentException()
            return STRINGS[mode]
        }

        @JvmStatic
		fun fromString(modeString: String): Int {
            for (i in STRINGS.indices) {
                if (STRINGS[i] == modeString) return i
            }
            return -1
        }
    }

    fun addToXml(attributes: Element) {
        attributes.addElement("key").apply {
            addElement("fifths").addText(fifths.toString())
            addElement("mode").addText(Mode.stringOf(mode))
        }
    }

    fun clone(): Key {
        return Key(fifths, mode)
    }

    companion object {
        @Throws(JSONException::class)
        fun fromJson(key: JSONObject): Key {
            return Key(
                key.getInt("fifths"),
                Mode.fromString(key.getString("mode"))
            )
        }
    }

}