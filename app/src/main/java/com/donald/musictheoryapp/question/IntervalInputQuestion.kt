package com.donald.musictheoryapp.question

import kotlin.Throws
import com.donald.musictheoryapp.music.MusicXML.Score
import org.xmlpull.v1.XmlPullParserException
import com.donald.musictheoryapp.Utils.getDescriptions
import com.donald.musictheoryapp.Utils.getInputHint
import com.donald.musictheoryapp.Utils.getScore
import com.donald.musictheoryapp.music.MusicXML.Note
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class IntervalInputQuestion(
    number: Int,
    descriptions: Array<Description>,
    inputHint: String?,
    val score: Score,
    val requiredInterval: String,
    val answer: Answer
) : Question(number, descriptions, inputHint) {

    override val points: Int
        get() = if (answer.correct) 1 else 0
    override val maxPoints = 1

    override fun acceptVisitor(visitor: QuestionVisitor) {
        visitor.visit(this)
    }

    @Throws(JSONException::class)
    override fun toPartialJson(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put(QUESTION_TYPE, Type.INTERVAL_INPUT.ordinal)
        jsonObject.put("score", score.toDocument().asXML())
        jsonObject.put("required_interval", requiredInterval)
        jsonObject.put("answer", answer.toJson())
        return jsonObject
    }

    companion object {

        @Throws(JSONException::class, IOException::class, XmlPullParserException::class)
        fun fromJson(jsonObject: JSONObject): IntervalInputQuestion {
            val question = IntervalInputQuestion(
                number = jsonObject.getInt("number"),
                descriptions = jsonObject.getDescriptions(),
                inputHint = jsonObject.getInputHint(),
                score = jsonObject.getScore(),
                requiredInterval = jsonObject.getString("required_interval"),
                answer = Answer.fromJson(jsonObject.getJSONObject("answer"))
            )
            check(!(question.score.parts().size != 1 || question.score.parts()[0].measures().size != 1 || question.score.parts()[0].measures()[0].notes().size != 1)) { "Invalid score from question." }
            return question
        }

    }

    class Answer(var userAnswer: Note?, val correctAnswer: Note) : Question.Answer {

        override val correct: Boolean
            get() = userAnswer?.pitch?.equals(correctAnswer.pitch) ?: false

        @Throws(JSONException::class)
        fun toJson(): JSONObject {
            val jsonObject = JSONObject()
            jsonObject.put("user_answer", if (userAnswer == null) JSONObject.NULL else userAnswer)
            jsonObject.put("correct_answer", correctAnswer.toJson())
            return jsonObject
        }

        companion object {
            @Throws(JSONException::class)
            fun fromJson(jsonObject: JSONObject): Answer {
                return Answer(
                    if (jsonObject.isNull("user_answer")) null else Note.fromJson(jsonObject.getJSONObject("user_answer")),
                    Note.fromJson(jsonObject.getJSONObject("correct_answer"))
                )
            }
        }

    }

}