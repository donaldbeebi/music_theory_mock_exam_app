package com.donald.musictheoryapp.music.MusicXML

import org.dom4j.Element

class Time(val beats: Int, val beatType: Int) {

    fun addToXml(attributes: Element) {
        attributes.addElement("time").apply {
            addElement("beats").addText(beats.toString())
            addElement("beat-type").addText(beatType.toString())
        }
    }

    fun clone(): Time {
        return Time(beats, beatType)
    }

}