package com.donald.musictheoryapp.question

import kotlin.Throws
import org.xmlpull.v1.XmlPullParserException
import com.donald.musictheoryapp.Utils.getDescriptions
import com.donald.musictheoryapp.Utils.getQuestions
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class QuestionGroup(
    val number: Int,
    val name: String,
    val descriptions: Array<Description>,
    val questions: Array<Question>
) {

    val points: Int
        get() = questions.sumOf { it.points }
    val maxPoints: Int
        get() = questions.sumOf { it.maxPoints }

    operator fun contains(question: Question): Boolean {
        return questions.any { it === question }
    }

    fun registerImages(imageArray: JSONArray) {
        descriptions.forEach { description ->
            if (description.type == Description.Type.IMAGE) {
                imageArray.put(description.content)
            }
        }
        questions.forEach { question ->
            question.registerImages(imageArray)
        }
    }

    @Throws(JSONException::class) fun toJson(): JSONObject {
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
        for (question in questions) currentArray.put(question.toJson())
        jsonObject.put("questions", currentArray)
        return jsonObject
    }

    companion object {

        @JvmStatic @Throws(JSONException::class, IOException::class, XmlPullParserException::class)
        fun fromJson(jsonObject: JSONObject): QuestionGroup {
            return QuestionGroup(
                number = jsonObject.getInt("number"),
                name = jsonObject.getString("name"),
                descriptions = jsonObject.getDescriptions(),
                questions = jsonObject.getQuestions()
            )
        }

    }

}