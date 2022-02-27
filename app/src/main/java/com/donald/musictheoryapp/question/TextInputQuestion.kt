package com.donald.musictheoryapp.question

import android.util.Log
import kotlin.Throws
import com.donald.musictheoryapp.Utils.getDescriptions
import com.donald.musictheoryapp.Utils.getInputHint
import com.donald.musictheoryapp.Utils.getInputType
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

// TODO: 1 ANSWER PER TEXT INPUT? OR NOT?
class TextInputQuestion(
    number: Int,
    descriptions: Array<Description>,
    inputHint: String?,
    val inputType: InputType,
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
        jsonObject.put(QUESTION_TYPE, Type.TEXT_INPUT.ordinal)
        jsonObject.put(INPUT_TYPE, inputType.ordinal)
        val array = JSONArray()
        for (answer in answers) {
            array.put(answer.toJson())
        }
        jsonObject.put("answers", array)
        return jsonObject
    }

    companion object {

        @Throws(JSONException::class)
        fun fromJson(jsonObject: JSONObject): TextInputQuestion {
            return TextInputQuestion(
                number = jsonObject.getInt("number"),
                descriptions = jsonObject.getDescriptions(),
                inputHint = jsonObject.getInputHint(),
                inputType = jsonObject.getInputType(),
                answers = jsonObject.getJSONArray("answers").run {
                    Array(this.length()) { index ->
                        Answer.fromJson(this.getJSONObject(index))
                    }
                }
            )
        }

    }

    enum class InputType {
        Text, Number
    }

    class Answer(var userAnswer: String?, val correctAnswer: String) : Question.Answer {

        override val correct: Boolean
            get() = userAnswer == correctAnswer

        @Throws(JSONException::class)
        fun toJson(): JSONObject {
            return JSONObject().apply {
                put("user_answer", userAnswer ?: JSONObject.NULL)
                put("correct_answer", correctAnswer)
            }
        }

        companion object {

            @Throws(JSONException::class)
            fun fromJson(jsonObject: JSONObject): Answer {
                return Answer(
                    if (jsonObject.isNull("user_answer")) null else jsonObject.getString("user_answer"),
                    jsonObject.getString("correct_answer")
                )
            }

        }

    }

}