package com.donald.musictheoryapp.question

import com.donald.musictheoryapp.util.forEachArg
import com.donald.musictheoryapp.util.getChildQuestions
import com.donald.musictheoryapp.util.getDescriptions
import org.json.JSONArray
import org.json.JSONObject

class ParentQuestion(
    val number: Int,
    val descriptions: List<Description> = emptyList(),
    val childQuestions: List<ChildQuestion>
) : Iterable<ChildQuestion> {

    val points: Int
        get() = childQuestions.sumOf { it.points }
    val maxPoints: Int
        get() = childQuestions.sumOf { it.maxPoints }

    fun registerImages(imageArray: JSONArray) {
        descriptions.forEach { description ->
            when (description.type) {
                Description.Type.Image -> imageArray.put(description.content)
                Description.Type.TextSpannable -> description.content.forEachArg { _, _, content -> imageArray.put(content) }
                else -> {}
            }
        }
        childQuestions.forEach { it.registerImages(imageArray) }
    }

    fun getQuestionImagesRequired(): List<String> {
        val images = ArrayList<String>()
        descriptions.forEach { description ->
            when (description.type) {
                Description.Type.Image -> images += description.content
                Description.Type.TextSpannable -> description.content.forEachArg { _, _, content -> images += content }
                else -> {}
            }
        }
        childQuestions.forEach { images += it.getQuestionImagesRequired() }
        return images
    }

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("number", number)

            put("descriptions",
                JSONArray().apply { descriptions.forEach { put(it.toJson()) } }
            )

            put("child_questions",
                JSONArray().apply { childQuestions.forEach { put(it.toJson()) } }
            )
        }
    }

    fun copyNew(): ParentQuestion {
        return ParentQuestion(
            number = number,
            descriptions = List(descriptions.size) { descriptions[it].copy() },
            childQuestions = List(childQuestions.size) { childQuestions[it].copyNew() }
        )
    }

    override fun iterator(): Iterator<ChildQuestion> = childQuestions.iterator()

    companion object {
        fun fromJson(jsonObject: JSONObject): ParentQuestion {
            return ParentQuestion(
                number = jsonObject.getInt("number"),
                descriptions = jsonObject.getDescriptions("descriptions"),
                childQuestions = jsonObject.getChildQuestions("child_questions")
            )
        }
    }

}