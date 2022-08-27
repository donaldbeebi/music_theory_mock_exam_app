package com.donald.musictheoryapp.music.musicxml

import kotlin.Throws
import org.json.JSONException
import org.json.JSONObject
import org.dom4j.Element

data class Notations(
    val noteArrow: NoteArrow?
) {

    data class NoteArrow(
        val label: String
    ) {
        constructor(that: NoteArrow) : this(that.label)

        fun addToXml(notations: Element) {
            notations.addElement("other-notation").apply {
                addAttribute("notation-name", "note-arrow")
                addText(label)
            }
        }

        fun equals(that: NoteArrow?): Boolean {
            return that != null && label == that.label
        }

        @Throws(JSONException::class)
        fun toJson(): JSONObject {
            val `object` = JSONObject()
            `object`.put("label", label)
            return `object`
        }

        companion object {
            @Throws(JSONException::class)
            fun fromJson(`object`: JSONObject): NoteArrow {
                return NoteArrow(
                    `object`.getString("label")
                )
            }
        }

    }

    constructor(that: Notations) : this(that.noteArrow!!)

    fun equals(that: Notations?): Boolean {
        return that != null &&
            if (noteArrow == null) that.noteArrow == null else noteArrow.equals(that.noteArrow)
    }

    @Throws(JSONException::class)
    fun toJson(): JSONObject {
        val `object` = JSONObject()
        if (noteArrow != null) `object`.put("note_arrow", noteArrow.toJson())
        return `object`
    }

    fun addToXml(note: Element) {
        note.addElement("notations").apply {
            noteArrow?.addToXml(this)
        }
    }

    fun clone(): Notations {
        return Notations(noteArrow)
    }

    companion object {
        @JvmStatic
		@Throws(JSONException::class)
        fun fromJson(`object`: JSONObject): Notations {
            return Notations(
                NoteArrow.fromJson(`object`)
            )
        }
    }

}