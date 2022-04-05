package com.donald.musictheoryapp.music.musicxml

import org.dom4j.Element
import org.json.JSONObject

class Pitch(
    var step: Step,
    var alter: Int,
    var octave: Int
) {

    val absStep: Int
        get() = step.ordinal + octave * Step.values().size

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("step", step.jsonValue)
            put("alter", alter)
            put("octave", octave)
        }
    }

    fun addToXml(note: Element) {
        val pitch = note.addElement("pitch")
        pitch.addElement("step").addText(step.jsonValue)
        if (alter != 0) pitch.addElement("alter").addText(alter.toString())
        pitch.addElement("octave").addText(octave.toString())
    }

    fun equals(that: Pitch?): Boolean {
        return that != null && step == that.step && alter == that.alter && octave == that.octave
    }

    fun clone(): Pitch {
        return Pitch(step, alter, octave)
    }

    companion object {

        fun fromJson(correctPitch: JSONObject): Pitch {
            return Pitch(
                Step.fromJsonValue(correctPitch.getString("step")),
                correctPitch.getInt("alter"),
                correctPitch.getInt("octave")
            )
        }
    }

}