package com.donald.musictheoryapp.util

import com.donald.musictheoryapp.question.*
import com.donald.musictheoryapp.music.musicxml.Score
import com.donald.musictheoryapp.util.Time.Companion.ms
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

fun JSONObject.getStringArray(key: String): Array<String> {
    val array = getJSONArray(key)
    return Array(array.length()) { index ->
        array.getString(index)
    }
}

fun JSONObject.getStringList(key: String): List<String> {
    val array = getJSONArray(key)
    return List(array.length()) { index ->
        array.getString(index)
    }
}

fun JSONObject.getStringsOrNull(key: String): Array<String>? {
    return try {
        getStringArray(key)
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getStringListOrNull(key: String): List<String>? {
    return try {
        getStringList(key)
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getOptions(): List<String> {
    return getStringList("options")
}

fun JSONObject.getOptionsOrNull(): List<String>? {
    return try {
        getOptions()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Deprecated("Use the one with key")
fun JSONObject.getDescriptions(): List<Description> {
    val array = getJSONArray("descriptions")
    return List(array.length()) { index ->
        Description.fromJson(array.getJSONObject(index))
    }
}

@Deprecated("Use the one with key")
fun JSONObject.getDescriptionsOrNull(): List<Description>? {
    return try {
        getDescriptions()
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getDescriptions(key: String): List<Description> {
    val array = getJSONArray(key)
    return List(array.length()) { index ->
        Description.fromJson(array.getJSONObject(index))
    }
}

fun JSONObject.getDescriptionsOrNull(key: String): List<Description>? {
    return try {
        getDescriptions(key)
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

// TODO: THIS COULD CAUSE A BUG LATER
fun JSONObject.getInputHint(): String? {
    return when {
        has("input_hint") && isNull("input_hint") -> null
        else -> getString("input_hint")
    }
}

@Deprecated("Use getInputHint instead")
fun JSONObject.getInputHintOrNull(): String? {
    return try {
        if (!isNull("inputHint")) getInputHint()
        else null
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

@Deprecated("Use the one with key instead")
fun JSONObject.getChildQuestions(): List<ChildQuestion> {
    val array = getJSONArray("questions")
    return List(array.length()) { index ->
        ChildQuestion.fromJson(array.getJSONObject(index))
    }
}

@Deprecated("Use the one with key instead")
fun JSONObject.getChildQuestionsOrNull(): List<ChildQuestion>? {
    return try {
        getChildQuestions()
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getChildQuestions(key: String): List<ChildQuestion> {
    val array = getJSONArray(key)
    return List(array.length()) { index ->
        ChildQuestion.fromJson(array.getJSONObject(index))
    }
}

fun JSONObject.getChildQuestionsOrNull(key: String): List<ChildQuestion>? {
    return try {
        getChildQuestions(key)
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getParentQuestions(key: String): List<ParentQuestion> {
    val array = getJSONArray(key)
    return List(array.length()) { index ->
        ParentQuestion.fromJson(array.getJSONObject(index))
    }
}

fun JSONObject.getParentQuestionsOrNull(key: String): List<ParentQuestion>? {
    return try {
        getParentQuestions(key)
    } catch (e: JSONException) {
        null
    }
}

fun JSONObject.getGroups(key: String): List<QuestionGroup> {
    val array = getJSONArray(key)
    return List(array.length()) { index ->
        QuestionGroup.fromJson(array.getJSONObject(index))
    }
}

fun JSONObject.getGroupsOrNull(key: String): List<QuestionGroup>? {
    val array = getJSONArray(key)
    return List(array.length()) { index ->
        QuestionGroup.fromJsonOrNull(array.getJSONObject(index)) ?: return null
    }
}

fun JSONObject.getSections(key: String): List<Section> {
    val array = getJSONArray(key)
    return List(array.length()) { index ->
        Section.fromJson(array.getJSONObject(index))
    }
}

fun JSONObject.getSectionsOrNull(key: String): List<Section>? {
    val array = getJSONArray(key)
    return List(array.length()) { index ->
        Section.fromJsonOrNull(array.getJSONObject(index)) ?: return null
    }
}

fun JSONObject.getSectionGroups(key: String): List<SectionGroup> {
    val array = getJSONArray(key)
    return List(array.length()) { index ->
        SectionGroup.fromJson(array.getJSONObject(index))
    }
}

fun JSONObject.getSectionGroupsOrNull(key: String): List<SectionGroup>? {
    val array = getJSONArray(key)
    return List(array.length()) { index ->
        SectionGroup.fromJsonOrNull(array.getJSONObject(index)) ?: return null
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

fun JSONObject.getQuestionType(): ChildQuestion.Type {
    return ChildQuestion.Type.values()[getInt(QUESTION_TYPE)]
}

fun JSONObject.getQuestionTypeOrNull(): ChildQuestion.Type? {
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

fun Date.toJsonValue(): Long = this.time

fun JSONObject.getDate(key: String = "date"): Date {
    return Date(getLong(key))
}

fun JSONObject.getDateOrNull(key: String = "date"): Date? {
    return try {
        getDate(key)
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

@Deprecated("Use tryGetString instead")
fun JSONObject.getStringOrNull(key: String): String? {
    return try {
        getString(key)
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.tryGetString(key: String): Result<String?, JSONException> {
    val string: String? = try {
        getString(key)
    } catch (e: JSONException) {
        return Result.Error(e)
    }
    return Result.Value(string)
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

fun JSONObject.getExerciseType(key: String = "type"): Exercise.Type {
    return Exercise.Type.fromJsonValue(getString(key))
}

fun JSONObject.getExerciseTypeOrNull(key: String = "type"): Exercise.Type? {
    return try {
        getExerciseType(key)
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getExerciseTitle(key: String = "title"): String {
    return getString(key)
}

fun JSONObject.getExerciseTitleOrNull(key: String = "title"): String? {
    return try {
        getExerciseTitle(key)
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

@Deprecated("Use getInt() instead", ReplaceWith("getInt(\"points\")"))
fun JSONObject.getPoints(): Int {
    return getInt("points")
}

@Deprecated("Use getInt() instead", ReplaceWith("getInt(\"points\")"))
fun JSONObject.getPointsOrNull(): Int? {
    return try {
        getPoints()
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

@Deprecated("Use getInt() instead", ReplaceWith("getInt(\"points\")"))
fun JSONObject.getMaxPoints(): Int {
    return getInt("max_points")
}

@Deprecated("Use getInt() instead", ReplaceWith("getInt(\"points\")"))
fun JSONObject.getMaxPointsOrNull(): Int? {
    return try {
        getMaxPoints()
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

@Deprecated("Use getTime() instead", ReplaceWith("getTime(\"time_remaining\")"))
fun JSONObject.getTimeRemaining(): Time {
    return getTime("time_remaining")
}

@Deprecated("Use getTime() instead", ReplaceWith("getTime(\"time_remaining\")"))
fun JSONObject.getTimeRemainingOrNull(): Time? {
    return getTimeOrNull("time_remaining")
}

@Deprecated("Use tryGetInt instead")
fun JSONObject.getIntOrNull(key: String): Int? {
    return try {
        getInt(key)
    } catch (e: JSONException) {
        null
    }
}

fun JSONObject.tryGetInt(key: String): Result<Int?, JSONException> {
    val int = try {
        if (isNull(key)) return Result.Value(null)
        getInt(key)
    } catch (e: JSONException) {
        return Result.Error(e)
    }
    return Result.Value(int)
}

fun JSONObject.getRedoInfo(key: String): RedoInfo {
    return RedoInfo.fromJson(getJSONObject(key))
}

fun JSONObject.getRedoInfoOrNull(key: String): RedoInfo? {
    return try {
        getRedoInfo(key)
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getExerciseData(key: String): ExerciseData {
    return ExerciseData.fromJson(getJSONObject(key))
}

fun JSONObject.getExerciseDataOrNull(key: String): ExerciseData? {
    return try {
        getExerciseData(key)
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.getExercise(key: String): Exercise {
    return getJSONObject(key).toExercise()
}

fun JSONObject.getExerciseOrNull(key: String): Exercise? {
    return try {
        getExercise(key)
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.toExercise(): Exercise {
    return Exercise.fromJson(this)
}

fun JSONObject.toExerciseOrNull(): Exercise? {
    return try {
        Exercise.fromJson(this)
    } catch (e: JSONException) {
        e.printStackTrace()
        null
    }
}

fun JSONObject.tryGetJSONObject(key: String): Result<JSONObject?, JSONException> {
    val jsonObject = try {
        getJSONObject(key)
    } catch (e: JSONException) {
        return Result.Error(e)
    }
    return Result.Value(jsonObject)
}

fun JSONObject.tryGetJSONArray(key: String): Result<JSONArray?, JSONException> {
    val jsonArray = try {
        getJSONArray(key)
    } catch (e: JSONException) {
        return Result.Error(e)
    }
    return Result.Value(jsonArray)
}

fun JSONArray.tryGetJSONObject(index: Int): Result<JSONObject?, JSONException> {
    val jsonObject: JSONObject? = try {
        getJSONObject(index)
    } catch (e: JSONException) {
        return Result.Error(e)
    }
    return Result.Value(jsonObject)
}