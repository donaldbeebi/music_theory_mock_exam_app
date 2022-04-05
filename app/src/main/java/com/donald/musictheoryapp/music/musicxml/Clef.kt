package com.donald.musictheoryapp.music.musicxml

import org.dom4j.Element
import kotlin.Throws
import org.json.JSONException
import org.json.JSONObject

class Clef(
    val sign: Sign,
    val line: Int,
    val printObject: Boolean
) {

    fun clone(): Clef {
        return Clef(sign, line, printObject)
    }

    fun addToXml(attributes: Element) {
        attributes.addElement("clef").apply {
            addAttribute("print-object", if (printObject) "yes" else "no")
            addElement("sign").addText(sign.xmlValue)
            addElement("line").addText(line.toString())
        }
    }

    companion object {
        @Throws(JSONException::class)
        fun fromJson(clef: JSONObject): Clef {
            return Clef(
                Sign.fromJsonValue(clef.getString("sign")),
                clef.getInt("line"),
                clef.getBoolean("print_object")
            )
        }

        @JvmStatic
		fun noteStaffPosition(note: Note, clef: Clef): Int {
            return note.pitch?.let {
                it.absStep - clef.sign.baseAbsStep + (clef.line - 1) * 2
            } ?: 0
        }
    }

}