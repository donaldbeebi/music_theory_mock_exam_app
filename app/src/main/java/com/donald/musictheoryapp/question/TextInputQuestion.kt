package com.donald.musictheoryapp.question

import com.donald.musictheoryapp.util.getDescriptions
import com.donald.musictheoryapp.util.getInputHint
import com.donald.musictheoryapp.util.getInputType
import org.json.JSONArray
import org.json.JSONObject

// TODO: 1 ANSWER PER TEXT INPUT? OR NOT?
class TextInputQuestion(
    number: Int,
    descriptions: List<Description>,
    inputHint: String?,
    val inputType: InputType,
    val answers: List<Answer>
) : ChildQuestion(number, descriptions, inputHint) {

    override val points: Int
        get() = answers.count { it.correct }
    override val maxPoints = answers.size
    override val correctAnswerCount = answers.sumOf { it.correctAnswers.size }

    override fun acceptVisitor(visitor: QuestionVisitor) {
        visitor.visit(this)
    }

    override fun copyNew(): TextInputQuestion {
        return TextInputQuestion(
            number,
            List(descriptions.size) { i -> descriptions[i].copy() },
            inputHint,
            inputType,
            List(answers.size) { i -> answers[i].copyNew() }
        )
    }

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

        fun fromJson(jsonObject: JSONObject): TextInputQuestion {
            return TextInputQuestion(
                number = jsonObject.getInt("number"),
                descriptions = jsonObject.getDescriptions(),
                inputHint = jsonObject.getInputHint(),
                inputType = jsonObject.getInputType(),
                answers = jsonObject.getJSONArray("answers").run {
                    List(this.length()) { index ->
                        Answer.fromJson(this.getJSONObject(index))
                    }
                }
            )
        }

    }

    enum class InputType {
        Text, Number
    }

    class Answer(var userAnswer: String?, val correctAnswers: List<String>) : ChildQuestion.Answer {

        override val correct: Boolean
            get() = correctAnswers.any { it == userAnswer }

        override fun copyNew(): Answer {
            return Answer(null, correctAnswers)
        }

        fun toJson(): JSONObject {
            return JSONObject().apply {
                put("user_answer", userAnswer ?: JSONObject.NULL)
                put("correct_answers", JSONArray().apply { correctAnswers.forEach { put(it) } })
            }
        }

        companion object {
            fun fromJson(jsonObject: JSONObject): Answer {
                return Answer(
                    if (jsonObject.isNull("user_answer")) null else jsonObject.getString("user_answer"),
                    jsonObject.getJSONArray("correct_answers").run { List(length()) { i -> getString(i) } }
                )
            }
        }

    }

}