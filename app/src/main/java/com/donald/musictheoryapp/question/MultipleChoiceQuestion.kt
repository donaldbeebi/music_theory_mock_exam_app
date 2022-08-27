package com.donald.musictheoryapp.question

import com.donald.musictheoryapp.util.*
import kotlin.Throws
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MultipleChoiceQuestion(
    number: Int,
    descriptions: List<Description>,
    inputHint: String?,
    val options: List<String>,
    val optionType: OptionType,
    val answer: Answer
) : ChildQuestion(number, descriptions, inputHint) {

    override val points: Int
        get() = if (answer.correct) 1 else 0
    override val maxPoints = 1
    override val correctAnswerCount = answer.correctAnswers.size

    override fun acceptVisitor(visitor: QuestionVisitor) {
        visitor.visit(this)
    }

    override fun getAdditionalQuestionImagesRequired(): List<String> {
        return if (optionType == OptionType.Image) {
            options
        } else {
            emptyList()
        }
    }

    override fun registerImages(imageArray: JSONArray) {
        super.registerImages(imageArray)
        if (optionType == OptionType.Image) {
            for (option in options) {
                imageArray.put(option)
            }
        }
    }

    override fun copyNew(): MultipleChoiceQuestion {
        return MultipleChoiceQuestion(
            number,
            List(descriptions.size) { i -> descriptions[i].copy() },
            inputHint,
            List(options.size) { i -> options[i] },
            optionType,
            answer.copyNew()
        )
    }

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

    enum class OptionType { Text, Image, Score }

    class Answer(var userAnswer: Int?, val correctAnswers: List<Int>) : ChildQuestion.Answer {

        override val correct: Boolean
            get() = correctAnswers.any { it == userAnswer }

        override fun copyNew(): Answer {
            return Answer(
                null,
                List(correctAnswers.size) { i -> correctAnswers[i] }
            )
        }

        fun toJson(): JSONObject {
            val jsonObject = JSONObject()
            jsonObject.put("user_answer", userAnswer ?: JSONObject.NULL)
            jsonObject.put(
                "correct_answers",
                JSONArray().apply { correctAnswers.forEach { put(it) } }
            )
            return jsonObject
        }

        companion object {

            fun fromJson(jsonObject: JSONObject): Answer {
                return Answer(
                    if (jsonObject.isNull("user_answer")) null else jsonObject.getInt("user_answer"),
                    jsonObject.getCorrectAnswers()
                )
            }

        }

    }

}