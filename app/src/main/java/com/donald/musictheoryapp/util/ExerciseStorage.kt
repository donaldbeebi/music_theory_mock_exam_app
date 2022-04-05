package com.donald.musictheoryapp.util

import android.content.Context
import android.util.Log
import com.donald.musictheoryapp.question.Exercise
import com.donald.musictheoryapp.util.Time.Companion.ms
import org.json.JSONObject
import java.io.*
import java.util.*

fun saveExercise(context: Context, exercise: Exercise): Boolean {
    return saveExercise(context, exercise.toJson())
}

fun fileNameOrNull(exerciseJson: JSONObject): String? {
    val title = exerciseJson.getStringOrNull("title") ?: return null
    val date = exerciseJson.getLongOrNull("date") ?: return null
    return "${title}_$date"
}

fun fileName(exerciseData: ExerciseData): String {
    return "${exerciseData.type}_${exerciseData.date.time}"
}

fun folderName(type: Exercise.Type): String {
    return when (type) {
        Exercise.Type.PRACTICE -> "practices"
        Exercise.Type.TEST -> "tests"
    }
}

fun saveExercise(context: Context, exerciseJson: JSONObject): Boolean {
    // TODO CHARSET?
    val bytes = exerciseJson.toString().toByteArray()
    val filesDir = context.filesDir
    val type = exerciseJson.getExerciseType()
    val folderName = when (type) {
        Exercise.Type.PRACTICE -> "practices"
        Exercise.Type.TEST -> "tests"
    }

    try {
        // saving to the local storage
        val dir = File(filesDir, "exercise/$folderName")
        if (!dir.exists()) dir.mkdirs()
        val date = exerciseJson.getLong("date")
        val destination = File(dir, "${type}_$date.json")
        destination.createNewFile()
        FileOutputStream(destination).run {
            write(bytes)
            close()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        return false
    }
    return true
    //postToList(context, exerciseJson)

    // debug
    //val exercise = Exercise.fromJson(exerciseJson)
    //Log.d("ExerciseStorage", (JSONAssert.assertEquals(exerciseJson, exercise.toJson(), false).toString()))
}

fun retrieveExercise(context: Context, exerciseData: ExerciseData): Exercise? {
    val filesDir = context.filesDir
    val folderName = folderName(exerciseData.type)

    val dir = File(filesDir, "exercise/$folderName")
    if (!dir.exists()) throw IllegalStateException()
    val destination = File(dir, "${exerciseData.type}_${exerciseData.date.time}.json")
    if (!destination.exists()) throw IllegalStateException()
    val jsonString = String(destination.readBytes())
    return Exercise.fromJsonOrNull(JSONObject(jsonString))
}

fun deleteExercise(context: Context, exerciseData: ExerciseData): Boolean {
    val filesDir = context.filesDir
    val fileName = fileName(exerciseData)
    val folderName = folderName(exerciseData.type)

    val fileToDelete = File(filesDir, "exercise/$folderName/$fileName.json")
    if (!fileToDelete.exists()) {
        Log.d("ExerciseStorage", "File with directory exercise/$folderName/$fileName.json does not exist while attempting deletion")
        return false
    }

    return fileToDelete.delete().also { successful ->
        if (!successful) {
            Log.d("ExerciseStorage", "File with directory exercise/$folderName/$fileName.json is found but failed to be deleted")
        }
    }
}

@Deprecated("Do not use this")
private fun postToList(context: Context, jsonObject: JSONObject): Boolean {
    val filesDir = context.filesDir
    val dir = File(filesDir, "exercise")
    if (!dir.exists()) {
        dir.mkdirs()
    }
    val destination = File(dir, "list.csv")
    if (!destination.exists()) {
        destination.createNewFile()
    }
    val type = jsonObject.getExerciseTypeOrNull() ?: return false
    val title = jsonObject.getExerciseTitleOrNull() ?: return false
    val points = jsonObject.getPointsOrNull() ?: return false
    val maxPoints = jsonObject.getMaxPointsOrNull() ?: return false
    val date = jsonObject.getDateOrNull() ?: return false
    val writer = FileWriter(destination, true)
    if (destination.length() != 0L) {
        writer.append('\n')
    }
    writer.append(
        "$type,$title,${date.time},$points,$maxPoints"
    )
    writer.close()
    return true
}

@Deprecated("Use getExerciseList instead")
fun getExerciseListOld(context: Context): Array<ExerciseData> {
    val filesDir = context.filesDir
    val destination = File(filesDir, "exercise/list.csv")
    return if (!destination.exists()) {
        emptyArray()
    }
    else {
        val reader = FileReader(destination)
        val lines = reader.readLines()
        reader.close()
        Array(lines.size) { index ->
            val args = lines[index].split(',')
            assert(args.size == 5)
            ExerciseData(
                type = Exercise.Type.fromString(args[0]),
                title = args[1],
                date = Date(args[2].toLong()),
                timeRemaining = 0L.ms,
                points = args[3].toInt(),
                maxPoints = args[4].toInt(),
            )
        }
    }
}

fun getExerciseList(context: Context): List<ExerciseData> {
    val filesDir = context.filesDir
    val list = ArrayList<ExerciseData>()

    // 1. practices
    val practicesFolder = File(filesDir, "exercise/practices")
    val practicesFiles = practicesFolder.listFiles()
    practicesFiles?.forEach { file ->
        val jsonString = String(file.readBytes())
        val exerciseJson = JSONObject(jsonString)
        list += ExerciseData(
            exerciseJson.getExerciseType(),
            exerciseJson.getExerciseTitle(),
            exerciseJson.getDate(),
            exerciseJson.getTimeRemaining(),
            exerciseJson.getPoints(),
            exerciseJson.getMaxPoints()
        )
    }

    // 2. tests
    val testsFolder = File(filesDir, "exercise/tests")
    val testsFiles = testsFolder.listFiles()
    testsFiles?.forEach { file ->
        val jsonString = String(file.readBytes())
        val exerciseJson = JSONObject(jsonString)
        list += ExerciseData(
            exerciseJson.getExerciseType(),
            exerciseJson.getExerciseTitle(),
            exerciseJson.getDate(),
            exerciseJson.getTimeRemaining(),
            exerciseJson.getPoints(),
            exerciseJson.getMaxPoints()
        )
    }

    return list
}