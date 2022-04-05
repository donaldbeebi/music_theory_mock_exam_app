package com.donald.musictheoryapp.question

import com.donald.musictheoryapp.util.*
import org.json.JSONArray
import org.json.JSONObject

class QuestionSection(
    val number: Int,
    val name: String,
    val descriptions: Array<Description> = emptyArray(),
    val groups: Array<QuestionGroup>
) {

    val points: Int
        get() = groups.sumOf { it.points }
    val maxPoints: Int
        get() = groups.sumOf { it.maxPoints }

    operator fun contains(group: QuestionGroup): Boolean {
        return groups.any { it === group }
    }

    operator fun contains(question: Question): Boolean {
        groups.forEach { group ->
            if (question in group) return true
        }
        return false
    }

    fun registerImages(imageArray: JSONArray) {
        groups.forEach { group ->
            group.registerImages(imageArray)
        }
    }

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("number", number)
            put("name", name)
            put(
                "descriptions",
                JSONArray().apply {
                    descriptions.forEach { put(it.toJson()) }
                }
            )
            put(
                "groups",
                JSONArray().apply {
                    groups.forEach { put(it.toJson()) }
                }
            )
        }
    }

    companion object {

        fun fromJson(jsonObject: JSONObject): QuestionSection {
            return QuestionSection(
                number = jsonObject.getInt("number"),
                name = jsonObject.getString("name"),
                descriptions = jsonObject.getDescriptions(),
                groups = jsonObject.getGroups()
            )
        }

        fun fromJsonOrNull(jsonObject: JSONObject): QuestionSection? {
            return QuestionSection(
                number = jsonObject.getIntOrNull("number") ?: return null,
                name = jsonObject.getStringOrNull("name") ?: return null,
                descriptions = jsonObject.getDescriptionsOrNull() ?: return null,
                groups = jsonObject.getGroupsOrNull() ?: return null
            )
        }

    }

}