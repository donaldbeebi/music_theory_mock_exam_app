package com.donald.musictheoryapp.question

import com.donald.musictheoryapp.util.getDescriptions
import com.donald.musictheoryapp.util.getInputHintOrNull
import org.json.JSONObject

class TruthQuestion(
    number: Int,
    descriptions: Array<Description>,
    inputHint: String?,
    val answer: Answer
) : Question(number, descriptions, inputHint) {

    override val points: Int
        get() = if (answer.correct) 1 else 0
    override val maxPoints = 1

    override fun acceptVisitor(visitor: QuestionVisitor) {
        visitor.visit(this)
    }

    override fun toPartialJson(): JSONObject {
        return JSONObject().apply {
            put(QUESTION_TYPE, Type.TRUTH.ordinal)
            put("answer", answer.toJson())
        }
    }

    companion object {

        const val TRUE_ANSWER = "true"
        const val FALSE_ANSWER = "false"
        fun fromJson(jsonObject: JSONObject): TruthQuestion {
            return TruthQuestion(
                number = jsonObject.getInt("number"),
                descriptions = jsonObject.getDescriptions(),
                inputHint = jsonObject.getInputHintOrNull(),
                answer = Answer.fromJson(jsonObject.getJSONObject("answer"))
            )
        }

    }

    class Answer(var userAnswer: Boolean?, val correctAnswer: Boolean) : Question.Answer {

        override val correct: Boolean
            get() {
                return userAnswer == correctAnswer
            }

        fun toJson(): JSONObject {
            return JSONObject().apply {
                put("user_answer", if (userAnswer == null) JSONObject.NULL else userAnswer)
                put("correct_answer", correctAnswer)
            }
        }

        companion object {

            fun fromJson(jsonObject: JSONObject): Answer {
                return Answer(
                    if (jsonObject.isNull("user_answer")) null else jsonObject.getBoolean("user_answer"),
                    jsonObject.getBoolean("correct_answer")
                )
            }

        }

    }

}