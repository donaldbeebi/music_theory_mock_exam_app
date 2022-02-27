package com.donald.musictheoryapp.music.scoreview

import org.xmlpull.v1.XmlPullParser
import android.util.Xml
import com.donald.musictheoryapp.music.MusicXML.*
import com.donald.musictheoryapp.music.MusicXML.Notations.NoteArrow
import java.io.InputStream

object MusicXmlParser {

    fun parse(inputStream: InputStream): Array<Part> {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(inputStream, null)
        parser.nextTag()
        val parts = ArrayList<Part>()
        parser.require(XmlPullParser.START_TAG, null, "score-partwise")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                val name = parser.name
                if (name == "part") {
                    parts.add(readPart(parser))
                } else {
                    skip(parser)
                }
            }
        }
        return parts.toTypedArray()
    }

    private fun readPart(parser: XmlPullParser): Part {
        parser.require(XmlPullParser.START_TAG, null, "part")
        val id = parser.getAttributeValue(null, "id")
        val measures = ArrayList<Measure>()
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                val name = parser.name
                if (name == "measure") {
                    measures.add(readMeasure(parser))
                } else {
                    skip(parser)
                }
            }
        }
        return Part(id, measures.toTypedArray())
    }

    private fun readMeasure(parser: XmlPullParser): Measure {
        parser.require(XmlPullParser.START_TAG, null, "measure")
        var attributes: Attributes? = null
        val notes = ArrayList<Note>()
        var barline: Barline? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                val name = parser.name
                if (name == "attributes") {
                    attributes = readAttributes(parser)
                } else if (name == "note") {
                    notes.add(readNote(parser))
                } else if (name == "barline") {
                    barline = readBarline(parser)
                } else {
                    skip(parser)
                }
            }
        }
        return Measure(attributes, notes, barline)
    }

    private fun readAttributes(parser: XmlPullParser): Attributes {
        parser.require(XmlPullParser.START_TAG, null, "attributes")
        var divisions = 0
        var key: Key? = null
        var time: Time? = null
        var staves = 0
        val clefs = ArrayList<Clef>()
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                val name = parser.name
                if (name == "divisions") {
                    parser.require(XmlPullParser.START_TAG, null, "divisions")
                    divisions = readText(parser).toInt()
                    parser.require(XmlPullParser.END_TAG, null, "divisions")
                } else if (name == "key") {
                    key = readKey(parser)
                } else if (name == "time") {
                    time = readTime(parser)
                } else if (name == "staves") {
                    parser.require(XmlPullParser.START_TAG, null, "staves")
                    staves = readText(parser).toInt()
                    parser.require(XmlPullParser.END_TAG, null, "staves")
                } else if (name == "clef") {
                    clefs.add(readClef(parser))
                } else {
                    skip(parser)
                }
            }
        }
        return Attributes(divisions, key!!, time, staves, clefs.toTypedArray())
    }

    private fun readNote(parser: XmlPullParser): Note {
        parser.require(XmlPullParser.START_TAG, null, "note")
        val printObject = "yes" == parser.getAttributeValue(null, "print-object")
        var pitch: Pitch? = null
        var duration = 0
        var type = Note.Type.NULL
        var accidental = Note.Accidental.NULL
        var chord = false
        var staff = 1
        var notations: Notations? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                val name = parser.name
                if (name == "chord") {
                    parser.require(XmlPullParser.START_TAG, null, "chord")
                    chord = true
                    parser.nextTag()
                    parser.require(XmlPullParser.END_TAG, null, "chord")
                } else if (name == "pitch") {
                    pitch = readPitch(parser)
                } else if (name == "rest") {
                    pitch = null
                } else if (name == "duration") {
                    parser.require(XmlPullParser.START_TAG, null, "duration")
                    duration = readText(parser).toInt()
                    parser.require(XmlPullParser.END_TAG, null, "duration")
                } else if (name == "type") {
                    parser.require(XmlPullParser.START_TAG, null, "type")
                    type = Note.Type.fromString(readText(parser))
                    parser.require(XmlPullParser.END_TAG, null, "type")
                } else if (name == "accidental") {
                    parser.require(XmlPullParser.START_TAG, null, "accidental")
                    accidental = Note.Accidental.fromString(readText(parser))
                    parser.require(XmlPullParser.END_TAG, null, "accidental")
                } else if (name == "staff") {
                    parser.require(XmlPullParser.START_TAG, null, "staff")
                    staff = readText(parser).toInt()
                    parser.require(XmlPullParser.END_TAG, null, "staff")
                } else if (name == "notations") {
                    parser.require(XmlPullParser.START_TAG, null, "notations")
                    notations = readNotations(parser)
                    parser.require(XmlPullParser.END_TAG, null, "notations")
                } else {
                    skip(parser)
                }
            }
        }
        return Note(
            printObject, pitch, duration, type,
            accidental, chord, staff, notations
        )
    }

    private fun readBarline(parser: XmlPullParser): Barline {
        parser.require(XmlPullParser.START_TAG, null, "barline")
        var barStyle = Barline.BarStyle.REGULAR
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                val name = parser.name
                if (name == "bar-style") {
                    barStyle = Barline.BarStyle.fromString(readText(parser))
                } else {
                    skip(parser)
                }
            }
        }
        return Barline(barStyle)
    }

    private fun readKey(parser: XmlPullParser): Key {
        parser.require(XmlPullParser.START_TAG, null, "key")
        var fifths = 0
        var mode = 0
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                val name = parser.name
                if (name == "fifths") {
                    parser.require(XmlPullParser.START_TAG, null, "fifths")
                    fifths = readText(parser).toInt()
                    parser.require(XmlPullParser.END_TAG, null, "fifths")
                } else if (name == "mode") {
                    parser.require(XmlPullParser.START_TAG, null, "mode")
                    mode = Key.Mode.fromString(readText(parser))
                    parser.require(XmlPullParser.END_TAG, null, "mode")
                } else {
                    skip(parser)
                }
            }
        }
        return Key(fifths, mode)
    }

    private fun readTime(parser: XmlPullParser): Time {
        parser.require(XmlPullParser.START_TAG, null, "time")
        var beats = 0
        var beatType = 0
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                val name = parser.name
                if (name == "beats") {
                    parser.require(XmlPullParser.START_TAG, null, "beats")
                    beats = readText(parser).toInt()
                    parser.require(XmlPullParser.END_TAG, null, "beats")
                } else if (name == "beatType") {
                    parser.require(XmlPullParser.START_TAG, null, "beat-type")
                    beatType = readText(parser).toInt()
                    parser.require(XmlPullParser.END_TAG, null, "beat-type")
                } else {
                    skip(parser)
                }
            }
        }
        return Time(beats, beatType)
    }

    private fun readPitch(parser: XmlPullParser): Pitch {
        parser.require(XmlPullParser.START_TAG, null, "pitch")
        var step = 0
        var alter = 0
        var octave = 0
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                val name = parser.name
                if (name == "step") {
                    parser.require(XmlPullParser.START_TAG, null, "step")
                    step = Pitch.Step.fromString(readText(parser))
                    parser.require(XmlPullParser.END_TAG, null, "step")
                } else if (name == "alter") {
                    parser.require(XmlPullParser.START_TAG, null, "alter")
                    alter = readText(parser).toInt()
                    parser.require(XmlPullParser.END_TAG, null, "alter")
                } else if (name == "octave") {
                    parser.require(XmlPullParser.START_TAG, null, "octave")
                    octave = readText(parser).toInt()
                    parser.require(XmlPullParser.END_TAG, null, "octave")
                }
            }
        }
        return Pitch(step, alter, octave)
    }

    private fun readNotations(parser: XmlPullParser): Notations {
        parser.require(XmlPullParser.START_TAG, null, "notations")
        var noteArrow: NoteArrow? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                val name = parser.name
                if (name == "other-notation") {
                    val notationName = parser.getAttributeValue(null, "notation-name")
                    if ("note-arrow" == notationName) {
                        noteArrow = readNoteArrow(parser)
                    }
                } else {
                    skip(parser)
                }
            }
        }
        return Notations(noteArrow)
    }

    private fun readClef(parser: XmlPullParser): Clef {
        parser.require(XmlPullParser.START_TAG, null, "clef")
        var sign = 0
        var line = 0
        val printObject = "no" != parser.getAttributeValue(null, "print-object")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                val name = parser.name
                if (name == "sign") {
                    parser.require(XmlPullParser.START_TAG, null, "sign")
                    sign = Clef.Sign.fromString(readText(parser))
                    parser.require(XmlPullParser.END_TAG, null, "sign")
                } else if (name == "line") {
                    parser.require(XmlPullParser.START_TAG, null, "line")
                    line = readText(parser).toInt()
                    parser.require(XmlPullParser.END_TAG, null, "line")
                } else {
                    skip(parser)
                }
            }
        }
        return Clef(sign, line, printObject)
    }

    private fun readNoteArrow(parser: XmlPullParser): NoteArrow {
        parser.require(XmlPullParser.START_TAG, null, "other-notation")
        val label = readText(parser)
        return NoteArrow(label)
    }

    private fun readText(parser: XmlPullParser): String {
        if (parser.next() == XmlPullParser.TEXT) {
            val result = parser.text
            parser.nextTag()
            return result
        }
        return ""
    }

    private fun skip(parser: XmlPullParser) {
        check(parser.eventType == XmlPullParser.START_TAG)
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}