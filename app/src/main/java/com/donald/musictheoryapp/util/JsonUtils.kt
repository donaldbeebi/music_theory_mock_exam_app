package com.donald.musictheoryapp.util

import com.donald.musictheoryapp.question.*
import com.donald.musictheoryapp.music.musicxml.Score
import com.donald.musictheoryapp.util.Time.Companion.ms
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

fun JSONObject.getStrings(key: String): Array<String> {
    val array = getJSONArray(key)
    return Array(array.length()) { index ->
        array.getString(index)
    }
}

fun JSONObject.getStringsOrNull(key: String): Array<String>? {
    return try {
        getStrings(key)
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getOptions(): Array<String> {
    return getStrings("options")
}

fun JSONObject.getOptionsOrNull(): Array<String>? {
    return try {
        getOptions()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getDescriptions(): Array<Description> {
    val array = getJSONArray("descriptions")
    return Array(array.length()) { index ->
        Description.fromJson(array.getJSONObject(index))
    }
}

fun JSONObject.getDescriptionsOrNull(): Array<Description>? {
    return try {
        getDescriptions()
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

// TODO: THIS COULD CAUSE A BUG LATER
fun JSONObject.getInputHint(): String {
    return if (isNull("input_hint")) {
        throw JSONException("input_hint is null")
    } else {
        getString("input_hint")
    }
}

fun JSONObject.getInputHintOrNull(): String? {
    return try {
        if (!isNull("inputHint")) getInputHint()
        else null
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getQuestions(): Array<Question> {
    val array = getJSONArray("questions")
    return Array(array.length()) { index ->
        Question.fromJson(array.getJSONObject(index))
    }
}

fun JSONObject.getQuestionsOrNull(): Array<Question>? {
    return try {
        getQuestions()
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getGroups(): Array<QuestionGroup> {
    val array = getJSONArray("groups")
    return Array(array.length()) { index ->
        QuestionGroup.fromJson(array.getJSONObject(index))
    }
}

fun JSONObject.getGroupsOrNull(): Array<QuestionGroup>? {
    return try {
        getGroups()
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getSections(): Array<QuestionSection> {
    val array = getJSONArray("sections")
    return Array(array.length()) { index ->
        QuestionSection.fromJson(array.getJSONObject(index))
    }
}

fun JSONObject.getSectionsOrNull(): Array<QuestionSection>? {
    return try {
        getSections()
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getScore(): Score {
    return Score.fromXml(getString("score"))
}

fun JSONObject.getScoreOrNull(): Score? {
    return try {
        getScore()
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getInputType(): TextInputQuestion.InputType {
    return TextInputQuestion.InputType.values()[getInt(INPUT_TYPE)]
}

fun JSONObject.getInputTypeOrNull(): TextInputQuestion.InputType? {
    return try {
        getInputType()
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getQuestionType(): Question.Type {
    return Question.Type.values()[getInt(QUESTION_TYPE)]
}

fun JSONObject.getQuestionTypeOrNull(): Question.Type? {
    return try {
        getQuestionType()
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getDescriptionType(): Description.Type {
    return Description.Type.values()[getInt(DESCRIPTION_TYPE)]
}

fun JSONObject.getDescriptionTypeOrNull(): Description.Type? {
    return try {
        return getDescriptionType()
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getOptionType(): MultipleChoiceQuestion.OptionType {
    return MultipleChoiceQuestion.OptionType.values()[getInt(OPTION_TYPE)]
}

fun JSONObject.getOptionTypeOrNull(): MultipleChoiceQuestion.OptionType? {
    return try {
        getOptionType()
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getDate(): Date {
    return Date(getLong("date"))
}

fun JSONObject.getDateOrNull(): Date? {
    return try {
        getDate()
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getTime(key: String): Time {
    return getLong(key).ms
}

fun JSONObject.getTimeOrNull(key: String): Time? {
    return try {
        getTime(key)
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getStringOrNull(key: String): String? {
    return try {
        getString(key)
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getLongOrNull(key: String): Long? {
    return try {
        getLong(key)
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun parseJSONObjectOrNull(string: String): JSONObject? {
    return try {
        JSONObject(string)
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun parseJSONArray(string: String): JSONArray? {
    return try {
        JSONArray(string)
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getJSONArrayOrNull(key: String): JSONArray? {
    return try {
        getJSONArray(key)
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getCorrectAnswers(): List<Int> {
    val jsonArray = getJSONArray("correct_answers")
    return List(jsonArray.length()) { index ->
        jsonArray.getInt(index)
    }
}

fun JSONObject.getCorrectAnswersOrNull(): List<Int>? {
    return try {
        getCorrectAnswers()
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getExerciseType(): Exercise.Type {
    return Exercise.Type.fromString(getString("type"))
}

fun JSONObject.getExerciseTypeOrNull(): Exercise.Type? {
    return try {
        getExerciseType()
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getExerciseTitle(): String {
    return getString("title")
}

fun JSONObject.getExerciseTitleOrNull(): String? {
    return try {
        getExerciseTitle()
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getPoints(): Int {
    return getInt("points")
}

fun JSONObject.getPointsOrNull(): Int? {
    return try {
        getPoints()
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getMaxPoints(): Int {
    return getInt("max_points")
}

fun JSONObject.getMaxPointsOrNull(): Int? {
    return try {
        getMaxPoints()
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getTimeRemaining(): Time {
    return getTime("time_remaining")
}

fun JSONObject.getTimeRemainingOrNull(): Time? {
    return getTimeOrNull("time_remaining")
}

fun JSONObject.getIntOrNull(key: String): Int? {
    return try {
        getInt(key)
    } catch (e: JSONException) {
        null
    }
}

