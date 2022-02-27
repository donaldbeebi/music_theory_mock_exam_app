package com.donald.musictheoryapp.music.MusicXML

import org.dom4j.Element

class Part(val id: String, val measures: Array<Measure>) {

    fun measures(): Array<Measure> {
        return measures
    }

    fun clone(): Part {
        val otherMeasures = Array(measures.size) { index ->
            measures[index].clone()
        }
        return Part(id, otherMeasures)
    }

    fun addToXml(scorePartwise: Element) {
        scorePartwise.addElement("part").apply {
            addAttribute("id", id)
            for (measure in measures) {
                measure.addToXml(this)
            }
        }
    }

}