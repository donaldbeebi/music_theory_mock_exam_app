package com.donald.musictheoryapp

import android.content.Context
import android.util.Log
import com.donald.musictheoryapp.question.Exercise
import org.json.JSONObject
import org.skyscreamer.jsonassert.JSONAssert
import java.io.*
import java.util.*

class ExerciseListItem(val title: String, val points: Int, val maxPoints: Int, val date: Date)

fun saveExercise(context: Context, exercise: Exercise) {
    saveExercise(context, exercise.toJson())
}

fun saveExercise(context: Context, jsonObject: JSONObject) {
    // TODO CHARSET?
    val bytes = jsonObject.toString().toByteArray()
    val filesDir = context.filesDir

    // saving to the local storage
    val dir = File(filesDir, "exercise/tests")
    if (!dir.exists()) dir.mkdirs()
    val title = jsonObject.getString("title")
    val date = jsonObject.getLong("date")
    val destination = File(dir, "$title $date.json")
    destination.createNewFile()
    FileOutputStream(destination).run {
        write(bytes)
        close()
    }
    postToList(filesDir, jsonObject)

    /*
    // saving to the firebase cloud storage
    val storageRef = Firebase.storage.reference
    val targetRef = storageRef.child("exercise/tests/$title $date.json")
    val uploadTask = targetRef.putBytes(bytes)
    uploadTask.addOnProgressListener { snapshot ->
        val progress = (100.0 * snapshot.bytesTransferred) / snapshot.totalByteCount
        Log.d("ExerciseStorage", "Upload is $progress% done")
    }.addOnPausedListener {
        Toast.makeText(context, "Firebase upload paused :O", Toast.LENGTH_LONG).show()
    }.addOnSuccessListener {
        Toast.makeText(context, "Firebase upload successful :)", Toast.LENGTH_LONG).show()
    }.addOnFailureListener {
        Toast.makeText(context, "Firebase upload NOT successful :(", Toast.LENGTH_LONG).show()
    }

     */
    // debug
    val exercise = Exercise.fromJson(jsonObject)
    Log.d("ExerciseStorage", (JSONAssert.assertEquals(jsonObject, exercise.toJson(), false).toString()))
}

fun retrieveExerciseLocal(context: Context, fileName: String): Exercise {
    val filesDir = context.filesDir
    val dir = File(filesDir, "exercise/tests")
    if (!dir.exists()) throw IllegalStateException()
    val destination = File(dir, "$fileName.json")
    if (!destination.exists()) throw IllegalStateException()
    val jsonString = String(destination.readBytes())
    Log.d("ExerciseStorage", jsonString)
    return Exercise.fromJson(JSONObject(jsonString))
}

private fun postToList(filesDir: File, jsonObject: JSONObject) {
    val dir = File(filesDir, "exercise")
    if (!dir.exists()) {
        dir.mkdirs()
    }
    val destination = File(dir, "list.csv")
    if (!destination.exists()) {
        destination.createNewFile()
    }
    val title = jsonObject.getString("title")
    val points = jsonObject.getInt("points")
    val maxPoints = jsonObject.getInt("max_points")
    val date = Date(jsonObject.getLong("date"))
    val writer = FileWriter(destination, true)
    if (destination.length() != 0L) {
        writer.append('\n')
    }
    writer.append(
        "$title,$points,$maxPoints,${date.time}"
    )
    writer.close()
}

fun getExerciseList(context: Context): Array<ExerciseListItem> {
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
            assert(args.size == 4)
            ExerciseListItem(args[0], args[1].toInt(), args[2].toInt(), Date(args[3].toLong()))
        }
    }
}