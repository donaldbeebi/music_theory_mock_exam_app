package com.donald.musictheoryapp.music.MusicXML

import org.dom4j.Element
import kotlin.Throws
import org.json.JSONException
import org.json.JSONObject

class Clef(val sign: Int, val line: Int, val printObject: Boolean) {

    object Sign {
        const val F = 0
        const val C = 1
        const val G = 2
        val STRINGS = arrayOf(
            "F",
            "C",
            "G"
        )
        @JvmField
		val BASE_NOTE_POS_BY_SIGN = intArrayOf(
            Pitch.Step.F + 3 * Pitch.Step.NO_OF_STEPS,
            Pitch.Step.C + 4 * Pitch.Step.NO_OF_STEPS,
            Pitch.Step.G + 4 * Pitch.Step.NO_OF_STEPS
        )

        @JvmStatic
		fun fromString(signString: String): Int {
            for (i in STRINGS.indices) {
                if (signString == STRINGS[i]) return i
            }
            return -1
        }
    }

    fun clone(): Clef {
        return Clef(sign, line, printObject)
    }

    fun addToXml(attributes: Element) {
        attributes.addElement("clef").apply {
            addAttribute("print-object", if (printObject) "yes" else "no")
            addElement("sign").addText(Sign.STRINGS[sign])
            addElement("line").addText(line.toString())
        }
    }

    companion object {
        @Throws(JSONException::class)
        fun fromJson(clef: JSONObject): Clef {
            return Clef(
                Sign.fromString(clef.getString("sign")),
                clef.getInt("line"),
                clef.getBoolean("print_object")
            )
        }

        @JvmStatic
		fun noteStaffPosition(note: Note, clef: Clef): Int {
            return (Pitch.absStep(note.pitch) - Sign.BASE_NOTE_POS_BY_SIGN[clef.sign]
                    + (clef.line - 1) * 2)
        }
    }

}