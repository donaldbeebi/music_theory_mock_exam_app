package com.donald.musictheoryapp.question

import android.util.Log
import com.donald.musictheoryapp.Utils.*
import kotlin.Throws
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MultipleChoiceQuestion(
    number: Int,
    descriptions: Array<Description>,
    inputHint: String?,
    val options: Array<String>,
    val optionType: OptionType,
    val answer: Answer
) : Question(number, descriptions, inputHint) {

    override val points: Int
        get() = if (answer.correct) 1 else 0
    override val maxPoints = 1

    override fun acceptVisitor(visitor: QuestionVisitor) {
        visitor.visit(this)
    }

    override fun registerImages(imageArray: JSONArray) {
        super.registerImages(imageArray)
        if (optionType == OptionType.IMAGE) {
            for (option in options) {
                imageArray.put(option)
            }
        }
    }

    @Throws(JSONException::class)
    override fun toPartialJson(): JSONObject {
        val jsonObject = JSONObject()
        val array = JSONArray()
        jsonObject.put(QUESTION_TYPE, Type.MULTIPLE_CHOICE.ordinal)
        for (option in options) array.put(option)
        jsonObject.put("options", array)
        jsonObject.put(OPTION_TYPE, optionType.ordinal)
        jsonObject.put("answer", answer.toJson())
        return jsonObject
    }

    companion object {

        @Throws(JSONException::class)
        fun fromJson(jsonObject: JSONObject): MultipleChoiceQuestion {
            return MultipleChoiceQuestion(
                number = jsonObject.getInt("number"),
                descriptions = jsonObject.getDescriptions(),
                inputHint = jsonObject.getInputHint(),
                options = jsonObject.getOptions(),
                optionType = jsonObject.getOptionType(),
                answer = Answer.fromJson(jsonObject.getJSONObject("answer")),
            )
        }

    }

    enum class OptionType {
        TEXT, IMAGE, SCORE
    }

    class Answer(var userAnswer: Int?, val correctAnswer: Int) : Question.Answer {

        override val correct: Boolean
            get() = userAnswer == correctAnswer

        fun toJson(): JSONObject {
            val jsonObject = JSONObject()
            jsonObject.put("user_answer", userAnswer ?: JSONObject.NULL)
            jsonObject.put("correct_answer", correctAnswer)
            return jsonObject
        }

        companion object {

            fun fromJson(jsonObject: JSONObject): Answer {
                return Answer(
                    if (jsonObject.isNull("user_answer")) null else jsonObject.getInt("user_answer"),
                    jsonObject.getInt("correct_answer")
                )
            }

        }

    }

}