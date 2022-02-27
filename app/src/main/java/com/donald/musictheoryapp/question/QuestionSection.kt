package com.donald.musictheoryapp.question

import com.donald.musictheoryapp.Utils.getGroups
import org.json.JSONArray
import org.json.JSONObject

class QuestionSection(
    val number: Int,
    val name: String,
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
        val jsonObject = JSONObject()
        val groupArray = JSONArray()
        for (group in groups) {
            groupArray.put(group.toJson())
        }
        jsonObject.put("number", number)
        jsonObject.put("name", name)
        jsonObject.put("groups", groupArray)
        return jsonObject
    }

    companion object {

        fun fromJson(jsonObject: JSONObject): QuestionSection {
            return QuestionSection(
                number = jsonObject.getInt("number"),
                name = jsonObject.getString("name"),
                groups = jsonObject.getGroups()
            )
        }

    }

}