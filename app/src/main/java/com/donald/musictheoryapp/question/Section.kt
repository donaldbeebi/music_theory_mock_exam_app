package com.donald.musictheoryapp.question

import com.donald.musictheoryapp.util.*
import org.json.JSONArray
import org.json.JSONObject

class Section(
    val number: Int,
    //val name: String,
    val descriptions: List<Description> = emptyList(),
    val questionGroups: List<QuestionGroup>
) : Iterable<QuestionGroup> {

    val points: Int
        get() = questionGroups.sumOf { it.points }
    val maxPoints: Int
        get() = questionGroups.sumOf { it.maxPoints }

    operator fun contains(group: QuestionGroup): Boolean {
        return questionGroups.any { it === group }
    }

    operator fun contains(question: ChildQuestion): Boolean {
        questionGroups.forEach { group ->
            if (question in group) return true
        }
        return false
    }

    fun getSectionImagesRequired(): List<String> = descriptions
        .filter { it.type == Description.Type.Image }
        .map { it.content }

    fun getAllImagesRequired(): List<String> {
        val allImages = ArrayList<String>()

        allImages += getSectionImagesRequired()

        questionGroups.forEach { allImages += it.getAllImagesRequired() }

        return allImages
    }

    fun registerImages(imageArray: JSONArray) {
        questionGroups.forEach { group ->
            group.registerImages(imageArray)
        }
    }

    fun copyNew(): Section {
        return Section(
            number,
            //name,
            List(descriptions.size) { i -> descriptions[i].copy() },
            List(questionGroups.size) { i -> questionGroups[i].copyNew() }
        )
    }

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("number", number)
            //put("name", name)
            put(
                "descriptions",
                JSONArray().apply {
                    descriptions.forEach { put(it.toJson()) }
                }
            )
            put(
                "groups",
                JSONArray().apply {
                    questionGroups.forEach { put(it.toJson()) }
                }
            )
        }
    }

    override fun iterator(): Iterator<QuestionGroup> = questionGroups.iterator()

    companion object {

        fun fromJson(jsonObject: JSONObject): Section {
            return Section(
                number = jsonObject.getInt("number"),
                //name = jsonObject.getString("name"),
                descriptions = jsonObject.getDescriptions("descriptions"),
                questionGroups = jsonObject.getGroups("groups")
            )
        }

        fun fromJsonOrNull(jsonObject: JSONObject): Section? {
            return Section(
                number = jsonObject.getIntOrNull("number") ?: return null,
                //name = jsonObject.getStringOrNull("name") ?: return null,
                descriptions = jsonObject.getDescriptionsOrNull("descriptions") ?: return null,
                questionGroups = jsonObject.getGroupsOrNull("groups") ?: return null
            )
        }

    }

}