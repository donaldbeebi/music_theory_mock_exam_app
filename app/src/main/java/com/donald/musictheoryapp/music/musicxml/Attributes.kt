package com.donald.musictheoryapp.music.musicxml

import org.dom4j.Element

class Attributes(
    val divisions: Int,
    val key: Key,
    val time: Time?,
    val staves: Int,
    val clefs: Array<Clef>
) {

    fun clone(): Attributes {
        val otherClefs = Array(clefs.size) { index ->
            clefs[index].clone()
        }
        return Attributes(
            divisions,
            key.clone(),
            time?.clone(),
            staves,
            otherClefs
        )
    }

    fun addToXml(measure: Element) {
        measure.addElement("attributes").apply {
            addElement("divisions").addText(divisions.toString())
            key.addToXml(this)
            time?.addToXml(this)
            addElement("staves").addText(staves.toString())
            for (clef in clefs) {
                clef.addToXml(this)
            }
        }
    }

}