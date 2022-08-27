package com.donald.musictheoryapp.question

import com.donald.musictheoryapp.util.getIntOrNull
import com.donald.musictheoryapp.util.getSections
import com.donald.musictheoryapp.util.getSectionsOrNull
import com.donald.musictheoryapp.util.getStringOrNull
import org.json.JSONArray
import org.json.JSONObject

class SectionGroup(
    val number: Int,
    val name: String,
    val sections: List<Section>
) : Iterable<Section> {

    val points: Int
        get() = sections.sumOf { it.points }

    val maxPoints: Int
        get() = sections.sumOf { it.maxPoints }

    fun registerImages(imageArray: JSONArray) {
        sections.forEach { section ->
            section.registerImages(imageArray)
        }
    }

    operator fun get(index: Int): Section = sections[index]

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("number", number)
            put("name", name)
            put(
                "sections",
                JSONArray().apply { sections.forEach { put(it.toJson()) } }
            )
        }
    }

    fun copyNew(): SectionGroup {
        return SectionGroup(
            number,
            name,
            List(sections.size) { i -> sections[i].copyNew() }
        )
    }

    override fun iterator(): Iterator<Section> = sections.iterator()

    companion object {
        fun fromJson(jsonObject: JSONObject): SectionGroup {
            return SectionGroup(
                number = jsonObject.getInt("number"),
                name = jsonObject.getString("name"),
                sections = jsonObject.getSections("sections")
            )
        }

        fun fromJsonOrNull(jsonObject: JSONObject): SectionGroup? {
            return SectionGroup(
                number = jsonObject.getIntOrNull("number") ?: return null,
                name = jsonObject.getStringOrNull("name") ?: return null,
                sections = jsonObject.getSectionsOrNull("sections") ?: return null
            )
        }
    }

}