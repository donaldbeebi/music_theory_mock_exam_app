package com.donald.musictheoryapp.question

import com.donald.musictheoryapp.util.getQuestionType
import kotlin.Throws
import org.xmlpull.v1.XmlPullParserException
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

abstract class Question(
    val number: Int,
    val descriptions: Array<Description>,
    val inputHint: String?
) {

    abstract val points: Int
    abstract val maxPoints: Int

    abstract fun acceptVisitor(visitor: QuestionVisitor)

    open fun registerImages(imageArray: JSONArray) {
        for (description in descriptions) {
            if (description.type == Description.Type.IMAGE) {
                imageArray.put(description.content)
            }
        }
    }

    inline fun visit(
        crossinline whenMultipleChoice: (MultipleChoiceQuestion) -> Unit,
        crossinline whenTextInput: (TextInputQuestion) -> Unit,
        crossinline whenTruth: (TruthQuestion) -> Unit,
        crossinline whenCheckBox: (CheckBoxQuestion) -> Unit,
        crossinline whenIntervalInput: (IntervalInputQuestion) ->  Unit
    ) {
        acceptVisitor(
            object : QuestionVisitor {
                override fun visit(question: MultipleChoiceQuestion) = whenMultipleChoice(question)
                override fun visit(question: TextInputQuestion) = whenTextInput(question)
                override fun visit(question: TruthQuestion) = whenTruth(question)
                override fun visit(question: CheckBoxQuestion) = whenCheckBox(question)
                override fun visit(question: IntervalInputQuestion) = whenIntervalInput(question)
            }
        )
    }

    @Throws(JSONException::class)
    protected abstract fun toPartialJson(): JSONObject
    @Throws(JSONException::class)
    fun toJson(): JSONObject {
        val jsonObject = toPartialJson()
        // 1. number
        jsonObject.put("number", number)

        // 2. descriptions
        val currentArray = JSONArray()
        for (description in descriptions) currentArray.put(description.toJson())
        jsonObject.put("descriptions", currentArray)

        // 3. panel hint
        val inputHint = inputHint
        if (inputHint == null) jsonObject.put("input_hint", JSONObject.NULL)
        else jsonObject.put("input_hint", inputHint)
        return jsonObject
    }

    companion object {

        @JvmStatic
        @Throws(JSONException::class, IOException::class, XmlPullParserException::class)
        fun fromJson(jsonObject: JSONObject): Question {
            val question: Question = when (jsonObject.getQuestionType()) {
                Type.MULTIPLE_CHOICE -> MultipleChoiceQuestion.fromJson(jsonObject)
                Type.TEXT_INPUT -> TextInputQuestion.fromJson(jsonObject)
                Type.CHECK_BOX -> CheckBoxQuestion.fromJson(jsonObject)
                Type.TRUTH -> TruthQuestion.fromJson(jsonObject)
                Type.INTERVAL_INPUT -> IntervalInputQuestion.fromJson(jsonObject)
            }
            return question
        }

    }

    enum class Type { MULTIPLE_CHOICE, TEXT_INPUT, CHECK_BOX, TRUTH, INTERVAL_INPUT }

    interface QuestionVisitor {

        fun visit(question: MultipleChoiceQuestion)
        fun visit(question: TextInputQuestion)
        fun visit(question: TruthQuestion)
        fun visit(question: CheckBoxQuestion)
        fun visit(question: IntervalInputQuestion)

    }

    interface Answer {

        val correct: Boolean

    }

}