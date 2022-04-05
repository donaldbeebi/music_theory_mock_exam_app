package com.donald.musictheoryapp.music.musicxml

import android.graphics.Color
import org.dom4j.Element
import org.json.JSONObject

class Note(
    var printObject: Boolean,
    val pitch: Pitch?,
    val duration: Int,
    val type: Type?,
    var accidental: Accidental?,
    val chord: Boolean,
    val staff: Int,
    val notations: Notations?,
    var color: Int = Color.BLACK
) {

    fun equals(that: Note?): Boolean {
        return that != null &&
                (pitch?.equals(that.pitch) ?: (that.pitch == null)) &&
                duration == that.duration &&
                type == that.type &&
                accidental == that.accidental &&
                chord == that.chord &&
                staff == that.staff &&
                notations?.equals(that.notations) ?: (that.notations == null)
    }

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("print-object", printObject)
            pitch ?.let { put("pitch", it.toJson()) }
            put("duration", duration)
            type?.let { put("type", it.jsonValue) }
            accidental?.let { put("accidental", it.jsonValue) }
            put("staff", staff)
            notations?.let { put("notations", it.toJson()) }
        }
    }

    fun addToXml(measure: Element) {
        val note = measure.addElement("note")
        pitch?.addToXml(note) ?: type?.let { note.addElement("rest") }
        //if (pitch != null) pitch.addToXml(note) else if (type != Type.NULL) note.addElement("rest")
        note.addElement("duration").addText(duration.toString())
        type?.let { note.addElement("type").addText(it.xmlValue) }
        if (chord) note.addElement("chord")
        accidental?.let { note.addElement("accidental").addText(it.xmlValue) }
        note.addElement("staff").addText(staff.toString())
        note.addAttribute("print-object", if (printObject) "yes" else "no")
        notations?.addToXml(note)
    }

    fun clone(): Note {
        return Note(printObject, pitch?.clone(), duration, type, accidental, chord, staff, notations?.clone())
    }

    companion object {

        fun fromJson(jsonObject: JSONObject): Note {
            var accidental: Accidental? = null
            if (jsonObject.has("accidental")) {
                accidental = Accidental.fromJsonValue(jsonObject.getString("accidental"))
            }
            var notations: Notations? = null
            if (jsonObject.has("notations")) {
                notations = Notations.fromJson(jsonObject.getJSONObject("notations"))
            }
            return Note( // removed printObject = ...
                printObject = jsonObject.isNull("print-object") || jsonObject.getBoolean("print-object"),
                pitch = Pitch.fromJson(jsonObject.getJSONObject("pitch")),
                duration = jsonObject.getInt("duration"),
                type = Type.fromJsonValue(jsonObject.getString("type")),
                accidental = accidental,
                chord = false,  // chord not implemented
                staff = jsonObject.getInt("staff"),
                notations = notations
            )
        }

    }

}