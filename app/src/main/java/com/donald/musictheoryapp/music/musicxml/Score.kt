package com.donald.musictheoryapp.music.musicxml

import kotlin.Throws
import org.xmlpull.v1.XmlPullParserException
import com.donald.musictheoryapp.customview.scoreview.MusicXmlParser
import org.dom4j.Document
import org.dom4j.DocumentHelper
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream

class Score(val parts: Array<Part>) {

    fun parts(): Array<Part> {
        return parts
    }

    fun clone(): Score {
        val otherParts = Array(parts.size) { index ->
            parts[index].clone()
        }
        return Score(otherParts)
    }

    fun toDocument(): Document {
        val score = DocumentHelper.createDocument()
        score.addElement("score-partwise").apply {
            addAttribute("version", "4.0")
            for (part in parts) {
                part.addToXml(this)
            }
        }
        return score
    }

    companion object {
        @Throws(IOException::class, XmlPullParserException::class)
        fun fromXml(inputStream: InputStream): Score {
            return Score(MusicXmlParser.parse(inputStream))
        }

        @JvmStatic
		@Throws(IOException::class, XmlPullParserException::class)
        fun fromXml(xml: String): Score {
            return Score(MusicXmlParser.parse(ByteArrayInputStream(xml.toByteArray())))
        }
    }

}