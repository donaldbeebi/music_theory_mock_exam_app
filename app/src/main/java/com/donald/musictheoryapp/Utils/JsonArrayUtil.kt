package com.donald.musictheoryapp.Utils

import com.donald.musictheoryapp.question.*
import kotlin.Throws
import com.donald.musictheoryapp.music.MusicXML.Score
import org.xmlpull.v1.XmlPullParserException
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

fun JSONObject.getStrings(key: String): Array<String> {
    val array = getJSONArray(key)
    return Array(array.length()) { index ->
        array.getString(index)
    }
}

fun JSONObject.getOptions(): Array<String> {
    return getStrings("options")
}

fun JSONObject.getDescriptions(): Array<Description> {
    val array = getJSONArray("descriptions")
    return Array(array.length()) { index ->
        Description.fromJson(array.getJSONObject(index))
    }
}

fun JSONObject.getInputHint(): String? {
    return if (isNull("input_hint")) null else getString("input_hint")
}

fun JSONObject.getQuestions(): Array<Question> {
    val array = getJSONArray("questions")
    return Array(array.length()) { index ->
        Question.fromJson(array.getJSONObject(index))
    }
}

/*
fun JSONObject.getAllQuestions(): Array<Question> {
    val array = ArrayList<Question>()
    val sections = getJSONArray("sections")
    for (sectionIndex in 0 until sections.length()) {
        val section = sections.getJSONObject(sectionIndex)
        val groups = section.getJSONArray("groups")
        for (groupIndex in 0 until groups.length()) {
            val group = groups.getJSONObject(groupIndex)
            val questions = group.getJSONArray("questions")
            for (questionIndex in 0 until questions.length()) {
                array += Question.fromJson(questions.getJSONObject(questionIndex))
            }
        }
    }
    return array.toTypedArray()
}

 */

fun JSONObject.getGroups(): Array<QuestionGroup> {
    val array = getJSONArray("groups")
    return Array(array.length()) { index ->
        QuestionGroup.fromJson(array.getJSONObject(index))
    }
}

/*
fun JSONObject.getAllGroups(): Array<QuestionGroup> {
    val array = ArrayList<QuestionGroup>()
    val sections = getJSONArray("sections")
    for (sectionIndex in 0 until sections.length()) {
        val section = sections.getJSONObject(sectionIndex)
        val groups = section.getJSONArray("groups")
        for (groupIndex in 0 until groups.length()) {
            array += QuestionGroup.fromJson(groups.getJSONObject(groupIndex))
        }
    }
    return array.toTypedArray()
}

 */

fun JSONObject.getSections(): Array<QuestionSection> {
    val array = getJSONArray("sections")
    return Array(array.length()) { index ->
        QuestionSection.fromJson(array.getJSONObject(index))
    }
}

fun JSONObject.getScore(): Score {
    return Score.fromXml(getString("score"))
}

fun JSONObject.getInputType(): TextInputQuestion.InputType {
    return TextInputQuestion.InputType.values()[getInt(INPUT_TYPE)]
}

fun JSONObject.getQuestionType(): Question.Type {
    return Question.Type.values()[getInt(QUESTION_TYPE)]
}

fun JSONObject.getDescriptionType(): Description.Type {
    return Description.Type.values()[getInt(DESCRIPTION_TYPE)]
}

fun JSONObject.getOptionType(): MultipleChoiceQuestion.OptionType {
    return MultipleChoiceQuestion.OptionType.values()[getInt(OPTION_TYPE)]
}

fun JSONObject.getDate(key: String): Date {
    return Date(getLong(key))
}

fun JSONObject.getTime(key: String): Time {
    return Time(getLong(key))
}

object JsonArrayUtil {
    @Throws(JSONException::class) fun strings(`object`: JSONObject, key: String): Array<String?> {
        val array = `object`.getJSONArray(key)
        val length = array.length()
        val answers = arrayOfNulls<String>(length)
        for (i in 0 until length) answers[i] = array.getString(i)
        return answers
    }

    @Throws(JSONException::class) fun options(`object`: JSONObject): Array<String?> {
        return strings(`object`, "options")
    }

    @Throws(JSONException::class) fun descriptions(`object`: JSONObject): Array<Description?> {
        val array = `object`.getJSONArray("descriptions")
        val length = array.length()
        val descriptions = arrayOfNulls<Description>(length)
        for (i in 0 until length) descriptions[i] = Description.fromJson(array.getJSONObject(i))
        return descriptions
    }

    @Throws(JSONException::class, IOException::class, XmlPullParserException::class) fun questions(`object`: JSONObject): Array<Question?> {
        val array = `object`.getJSONArray("questions")
        val length = array.length()
        val questions = arrayOfNulls<Question>(length)
        for (i in 0 until length) questions[i] = Question.fromJson(array.getJSONObject(i))
        return questions
    }

    @JvmStatic @Throws(JSONException::class, IOException::class, XmlPullParserException::class) fun groups(`object`: JSONObject, section: QuestionSection?): Array<QuestionGroup?> {
        val array = `object`.getJSONArray("groups")
        val length = array.length()
        val groups = arrayOfNulls<QuestionGroup>(length)
        for (i in 0 until length) groups[i] = QuestionGroup.fromJson(array.getJSONObject(i))
        return groups
    }

    @Throws(JSONException::class, IOException::class, XmlPullParserException::class) fun sections(`object`: JSONObject): Array<QuestionSection?> {
        val array = `object`.getJSONArray("sections")
        val length = array.length()
        val sections = arrayOfNulls<QuestionSection>(length)
        for (i in 0 until length) {
            sections[i] = QuestionSection.fromJson(array.getJSONObject(i))
        }
        return sections
    }
}