package com.donald.musictheoryapp.question

import kotlin.Throws
import com.donald.musictheoryapp.util.getDescriptions
import com.donald.musictheoryapp.util.getInputHintOrNull
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CheckBoxQuestion(
    number: Int,
    descriptions: Array<Description>,
    inputHint: String?,
    val answers: Array<Answer>
) : Question(number, descriptions, inputHint) {

    override val points: Int
        get() = answers.count { it.correct }
    override val maxPoints = answers.size

    override fun acceptVisitor(visitor: QuestionVisitor) {
        visitor.visit(this)
    }

    @Throws(JSONException::class)
    override fun toPartialJson(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put(QUESTION_TYPE, Type.CHECK_BOX.ordinal)
        val array = JSONArray()
        for (answer in answers) {
            array.put(answer.toJson())
        }
        jsonObject.put("answers", array)
        return jsonObject
    }

    companion object {

        const val CHECK_ANSWER = "check"
        const val CROSS_ANSWER = "cross"
        @Throws(JSONException::class)
        fun fromJson(jsonObject: JSONObject): CheckBoxQuestion {
            val question = CheckBoxQuestion(
                number = jsonObject.getInt("number"),
                descriptions = jsonObject.getDescriptions(),
                inputHint = jsonObject.getInputHintOrNull(),
                answers = jsonObject.getJSONArray("answers").run {
                    Array(this.length()) { index ->
                        Answer.fromJson(this.getJSONObject(index))
                    }
                }
            )
            return question
        }

    }

    class Answer(var userAnswer: Boolean?, val correctAnswer: Boolean) : Question.Answer {

        override val correct: Boolean
            get() = userAnswer == correctAnswer

        @Throws(JSONException::class)
        fun toJson(): JSONObject {
            val jsonObject = JSONObject()
            jsonObject.put("user_answer", if (userAnswer == null) JSONObject.NULL else userAnswer)
            jsonObject.put("correct_answer", correctAnswer)
            return jsonObject
        }

        companion object {

            @Throws(JSONException::class)
            fun fromJson(jsonObject: JSONObject): Answer {
                return Answer(
                    if (jsonObject.isNull("user_answer")) null else jsonObject.getBoolean("user_answer"),
                    jsonObject.getBoolean("correct_answer")
                )
            }

        }

    }
}