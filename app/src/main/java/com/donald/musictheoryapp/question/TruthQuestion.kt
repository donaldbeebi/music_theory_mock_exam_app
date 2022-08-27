package com.donald.musictheoryapp.question

import com.donald.musictheoryapp.util.getDescriptions
import com.donald.musictheoryapp.util.getInputHint
import org.json.JSONObject

class TruthQuestion(
    number: Int,
    descriptions: List<Description>,
    inputHint: String?,
    val answer: Answer
) : ChildQuestion(number, descriptions, inputHint) {

    override val points: Int
        get() = if (answer.correct) 1 else 0
    override val maxPoints = 1
    override val correctAnswerCount = 1

    override fun acceptVisitor(visitor: QuestionVisitor) {
        visitor.visit(this)
    }

    override fun copyNew(): TruthQuestion {
        return TruthQuestion(
            number,
            List(descriptions.size) { i -> descriptions[i].copy() },
            inputHint,
            answer.copyNew()
        )
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
                inputHint = jsonObject.getInputHint(),
                answer = Answer.fromJson(jsonObject.getJSONObject("answer"))
            )
        }
    }

    class Answer(var userAnswer: Boolean?, val correctAnswer: Boolean) : ChildQuestion.Answer {

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

        override fun copyNew(): Answer {
            return Answer(null, correctAnswer)
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