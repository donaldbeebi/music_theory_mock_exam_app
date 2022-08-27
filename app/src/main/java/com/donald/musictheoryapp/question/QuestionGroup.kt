package com.donald.musictheoryapp.question

import com.donald.musictheoryapp.util.*
import kotlin.Throws
import org.xmlpull.v1.XmlPullParserException
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class QuestionGroup(
    val number: Int,
    val name: String,
    val descriptions: List<Description>,
    val parentQuestions: List<ParentQuestion>
): Iterable<ParentQuestion> {

    val points: Int
        get() = parentQuestions.sumOf { it.points }
    val maxPoints: Int
        get() = parentQuestions.sumOf { it.maxPoints }

    operator fun contains(question: ChildQuestion): Boolean {
        return parentQuestions.any { parentQuestion ->
            question in parentQuestion
        }
    }

    fun getGroupImagesRequired(): List<String> = descriptions
        .filter { it.type == Description.Type.Image }
        .map { it.content }

    fun getAllImagesRequired(): List<String> {
        val allImages = ArrayList<String>()

        allImages += getGroupImagesRequired()

        parentQuestions.forEach {
            allImages += it.getQuestionImagesRequired()
        }

        return allImages
    }

    fun registerImages(imageArray: JSONArray) {
        descriptions.forEach { description ->
            if (description.type == Description.Type.Image) {
                imageArray.put(description.content)
            }
        }
        parentQuestions.forEach { question ->
            question.registerImages(imageArray)
        }
    }

    fun copyNew(): QuestionGroup {
        return QuestionGroup(
            number,
            name,
            List(descriptions.size) { i -> descriptions[i].copy() },
            List(parentQuestions.size) { i -> parentQuestions[i].copyNew() }
        )
    }

    operator fun get(index: Int): ParentQuestion = parentQuestions[index]

    fun toJson(): JSONObject {
        val jsonObject = JSONObject()

        // 1. number
        jsonObject.put("number", number)

        // 2. topic
        jsonObject.put("name", name)

        // 3. descriptions
        var currentArray = JSONArray()
        for (description in descriptions) currentArray.put(description.toJson())
        jsonObject.put("descriptions", currentArray)

        // 4. questions
        currentArray = JSONArray()
        for (question in parentQuestions) currentArray.put(question.toJson())
        jsonObject.put("parent_questions", currentArray)
        return jsonObject
    }

    companion object {
        fun fromJson(jsonObject: JSONObject): QuestionGroup {
            return QuestionGroup(
                number = jsonObject.getInt("number"),
                name = jsonObject.getString("name"),
                descriptions = jsonObject.getDescriptions("descriptions"),
                parentQuestions = jsonObject.getParentQuestions("parent_questions")
            )
        }

        fun fromJsonOrNull(jsonObject: JSONObject): QuestionGroup? {
            return QuestionGroup(
                number = jsonObject.getIntOrNull("number") ?: return null,
                name = jsonObject.getStringOrNull("name") ?: return null,
                descriptions = jsonObject.getDescriptionsOrNull("descriptions") ?: return null,
                parentQuestions = jsonObject.getParentQuestionsOrNull("parent_questions") ?: return null
            )
        }
    }

    override fun iterator(): Iterator<ParentQuestion> = parentQuestions.iterator()

}