package com.donald.musictheoryapp.music.MusicXML

import org.dom4j.Element

class Measure(val attributes: Attributes?, val notes: ArrayList<Note>, val barline: Barline?) {

    fun notes(): ArrayList<Note> {
        return notes
    }

    fun clone(): Measure {
        val otherNotes = ArrayList<Note>()
        notes.forEach {
            otherNotes.add(it.clone())
        }

        return Measure(
            attributes?.clone(),
            otherNotes,
            barline?.clone()
        )
    }

    fun addToXml(part: Element) {
        part.addElement("measure").apply {
            attributes?.addToXml(this)
            notes.forEach { it.addToXml(this) }
            barline?.addToXml(this)
        }
    }
}