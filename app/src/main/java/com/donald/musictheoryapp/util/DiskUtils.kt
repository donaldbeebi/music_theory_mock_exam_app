package com.donald.musictheoryapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.donald.musictheoryapp.question.Exercise
import com.donald.musictheoryapp.util.Time.Companion.ms
import org.json.JSONObject
import java.io.*
import java.util.*

private val REDO_FILE_NAME_REGEX = ".*_redo_[0-9][0-9]*".toRegex()

private fun Context.getDirectory(type: Exercise.Type, residenceFolderName: String): File {
    val baseDirectory = when (type) {
        Exercise.Type.Practice -> "practices"
        Exercise.Type.Test -> "tests"
    }
    return File(filesDir, "exercise/$baseDirectory/$residenceFolderName")
}

private fun Context.getDirectory(exercise: Exercise) = getDirectory(exercise.type, exercise.residenceFolderName)

private fun Context.getDirectory(exerciseData: ExerciseData) = getDirectory(exerciseData.type, exerciseData.residenceFolderName)

fun Context.saveExercise(exercise: Exercise) {
    val saveDirectory = run {
        val baseDirectory = when (exercise.type) {
            Exercise.Type.Practice -> "practices"
            Exercise.Type.Test -> "tests"
        }
        "exercise/$baseDirectory/${exercise.residenceFolderName}"
    }
    val destination = File(filesDir, "$saveDirectory/${exercise.fileName}.json")
    FileOutputStream(destination).use { output ->
        output.write(exercise.toJson().toString().toByteArray())
    }
}

fun Context.storeNewExerciseJson(exerciseJson: JSONObject): Exercise? {
    val residenceFolderName = exerciseFileName(
        exerciseJson.getExerciseType("type"),
        exerciseJson.getDate("date"),
        null
    )
    if (!exerciseJson.has("residence_folder_name")) {
        exerciseJson.put("residence_folder_name", residenceFolderName)
    }

    val filesDir = filesDir
    val type = exerciseJson.getExerciseType("type")
    val date = exerciseJson.getDate("date")

    val saveDirectory = run {
        val baseDirectory = when (type) {
            Exercise.Type.Practice -> "practices"
            Exercise.Type.Test -> "tests"
        }
        "exercise/$baseDirectory/$residenceFolderName"
    }
    val fileName = exerciseFileName(type, date, null)

    // saving to the local storage
    val dir = File(filesDir, saveDirectory)
    if (!dir.exists()) dir.mkdirs()
    val destination = File(dir, "$fileName.json")
    destination.createNewFile()
    FileOutputStream(destination).use { outputStream ->
        outputStream.write(exerciseJson.toString().toByteArray())
    }

    return exerciseJson.toExerciseOrNull()
}

fun Context.createAndSaveRedoExercise(exercise: Exercise): Exercise {
    val newExercise = exercise.copyNew()
    saveExercise(newExercise)
    return newExercise
}

fun Context.retrieveExercise(exerciseData: ExerciseData): Exercise {
    val dir = getDirectory(exerciseData)
    if (!dir.exists()) throw IllegalStateException()
    val destination = File(dir, "${exerciseData.fileName}.json")
    if (!destination.exists()) throw IllegalStateException("File ${destination.name} does not exists! ${exerciseData.redoInfo == null}")
    val jsonString = String(destination.readBytes())
    return JSONObject(jsonString).toExercise()
}

fun Context.retrieveExerciseOrNull(exerciseData: ExerciseData): Exercise? {
    val dir = getDirectory(exerciseData)
    if (!dir.exists()) throw IllegalStateException()
    val destination = File(dir, "${exerciseData.fileName}.json")
    if (!destination.exists()) throw IllegalStateException("File ${destination.name} at ${dir.path} does not exists!")
    val jsonString = String(destination.readBytes())
    return JSONObject(jsonString).toExerciseOrNull()
}

fun Context.deleteExerciseAll(exerciseData: ExerciseData) {
    val directory = getDirectory(exerciseData)

    /*
    val fileToDelete = File(directory, "${exerciseData.fileName}.json")
    if (!fileToDelete.exists()) {
        Log.d("ExerciseStorage", "File with directory ${fileToDelete.path} does not exist while attempting deletion")
        return false
    }

    return fileToDelete.delete().also { successful ->
        require (successful) { Log.d("ExerciseStorage", "File with directory ${fileToDelete.path} is found but failed to be deleted") }
    }

     */
    check(directory.exists()) { "Directory $directory from exercise data $exerciseData does not exist!" }
    check(directory.isDirectory) { "Directory $directory from exercise data $exerciseData is not a directory when attempting deletion!" }

    // deleting files inside
    val files = directory.listFiles() ?: throw IllegalStateException()
    files.forEach { file ->
        val successful = file.delete()
        require(successful == true) { "Failed to delete file ${file.name} when deleting files inside directory $directory" }
    }

    val successful = directory.delete()
    require(successful) { "Failed to delete directory $directory" }
}

fun Context.deleteExerciseSingle(exerciseData: ExerciseData) {
    val directory = getDirectory(exerciseData)
    val fileToDelete = File(directory, "${exerciseData.fileName}.json")

    check(fileToDelete.exists()) { "File ${exerciseData.fileName} does not exist" }
    check(fileToDelete.isFile) { "File ${exerciseData.fileName} is not a file " }

    val successful = fileToDelete.delete()
    require(successful) { "Failed to delete file ${exerciseData.fileName} " }
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
                residenceFolderName = ""
            )
        }
    }
}

fun Context.getExerciseDataList(): List<ExerciseData> {
    val list = ArrayList<ExerciseData>()

    val testFiles = getTestsDir().listFiles() ?: throw IllegalStateException()
    testFiles.forEach { subdirectory ->
        val files = subdirectory.listFiles() ?: throw IllegalStateException()
        check(files.isNotEmpty()) { "Directory $subdirectory is empty." }
        files[0].let { list += it.toExercise().exerciseData() }
    }

    val practiceFiles = getPracticesDir().listFiles() ?: throw IllegalStateException()
    practiceFiles.forEach { subdirectory ->
        val files = subdirectory.listFiles() ?: throw IllegalStateException()
        check(files.isNotEmpty()) { "Directory $subdirectory is empty." }
        files[0].let { list += it.toExercise().exerciseData() }
    }

    return list
}

fun Context.getExerciseList(exerciseData: ExerciseData): List<Exercise> {
    val directory = getDirectory(exerciseData)
    val files = directory.listFiles() ?: throw IllegalStateException()
    return List(files.size) { index ->
        val file = files[index]
        file.toExercise()
    }
}

private fun getFirstExerciseDataFromDir(file: File): ExerciseData {
    require(file.isDirectory)
    val files = file.listFiles() ?: throw IllegalStateException()
    val firstFile = files[0]
    return with(JSONObject(String(firstFile.readBytes()))) {
        ExerciseData(
            getExerciseType(),
            getExerciseTitle(),
            getDate(),
            getTime("time_remaining"),
            getInt("points"),
            getInt("points"),
            if (has("redo_info")) getRedoInfo("redo_info") else null,
            getString("residence_folder_name")
        )
    }
}

private fun extractExerciseListFromFiles(files: Array<File?>): List<List<ExerciseData>> {
    val outerList = ArrayList<List<ExerciseData>>()

    files.forEach { file ->
        if (file == null) return@forEach
        if (!file.nameWithoutExtension.matches(REDO_FILE_NAME_REGEX)) {
            // if this file is NOT a redo
            // 1. add this file to a list
            val innerList = ArrayList<ExerciseData>()
            innerList += extractExerciseDataFromFile(file)
            // 2. all child exercises to this list as well
            var currentRedoNumber = 0
            do {
                currentRedoNumber++
                val childExerciseFile = files.find { it?.nameWithoutExtension == "${file.nameWithoutExtension}_redo_$currentRedoNumber" }
                if (childExerciseFile != null) innerList += extractExerciseDataFromFile(childExerciseFile)
            } while (childExerciseFile != null)
            // 3. add this inner list to the outer list
            outerList += innerList
        }
    }

    return outerList
}

private fun extractExerciseDataFromFile(file: File): ExerciseData {
    return with(JSONObject(String(file.readBytes()))) {
        ExerciseData(
            getExerciseType(),
            getExerciseTitle(),
            getDate(),
            getTime("time_remaining"),
            getInt("points"),
            getInt("points"),
            if (has("redo_info")) getRedoInfo("redo_info") else null,
            getString("residence_folder_name")
        )
    }
}

@Deprecated("Use getListOfExerciseDataList() instead")
fun getExerciseList(context: Context): List<List<ExerciseData>> {
    // 1. practices
    val practicesFolder = context.getPracticesDir()
    val practicesFiles: Array<File?> = practicesFolder.listFiles() ?: throw IllegalStateException()

    // 2. tests
    val testsFolder = context.getTestsDir()
    val testsFiles: Array<File?> = testsFolder.listFiles() ?: throw IllegalStateException()

    return extractExerciseListFromFiles(practicesFiles + testsFiles)
}

fun Context.getExerciseDataList(exerciseData: ExerciseData): List<ExerciseData> {
    return getExerciseList(exerciseData).map { it.exerciseData() }
}

fun Context.getListOfExerciseDataList(): List<List<ExerciseData>> {
    val list = ArrayList<List<ExerciseData>>()
    // 1. practices
    val practicesDir = getPracticesDir()
    practicesDir.forEachExerciseDataList { exerciseDataList ->
        list += exerciseDataList
    }

    // 2. tests
    val testsDir = getTestsDir()
    testsDir.forEachExerciseDataList { exerciseDataList ->
        list += exerciseDataList
    }

    return list
}

private inline fun File.forEachExerciseDataList(block: (List<ExerciseData>) -> Unit) {
    require(isDirectory)
    listFiles()?.forEach { file: File? ->
        require(file != null && file.isDirectory)
        val practiceJsonFiles = file.listFiles() ?: throw IllegalStateException()
        val redos = ArrayList<ExerciseData>()
        practiceJsonFiles.forEach { jsonFile: File? ->
            requireNotNull(jsonFile)
            redos += extractExerciseDataFromFile(jsonFile)
        }
        redos.sortBy { it.date }
        block(redos)
    } ?: throw IllegalStateException()
}

private fun Context.getExerciseDir(): File {
    val exerciseDir = File(filesDir, "exercise")
    if (!exerciseDir.exists()) exerciseDir.mkdir().also { successful -> require(successful == true) }
    return exerciseDir
}

private fun Context.getPracticesDir(): File {
    val exerciseDir = getExerciseDir()
    val practicesDir = File(exerciseDir, "practices")
    if (!practicesDir.exists()) practicesDir.mkdir().also { successful -> require(successful == true) }
    return practicesDir
}

private fun Context.getTestsDir(): File {
    val exerciseDir = getExerciseDir()
    val practicesDir = File(exerciseDir, "tests")
    if (!practicesDir.exists()) practicesDir.mkdir().also { successful -> require(successful == true) }
    return practicesDir
}

fun Context.imageExists(imageName: String): Boolean {
    val dir = File(filesDir, "images/$imageName.png")
    return dir.exists();
}

fun Context.saveImage(imageName: String, bitmap: Bitmap): Boolean {
    val filesDir = filesDir
    val dir = File(filesDir, "images")
    if (!dir.exists()) dir.mkdir()
    val destination = File(dir, "$imageName.png")
    destination.createNewFile()

    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
    FileOutputStream(destination).use {
        it.write(bos.toByteArray())
        it.flush()
        it.close()
    }
    //tracker.increment()
    return true
}

fun Context.retrieveImage(imageName: String): ImageBitmap {
    val path = "${filesDir.path}/images/$imageName.png"
    val bitmap = BitmapFactory.decodeFile(path)
        ?: throw IllegalStateException("Null bitmap from BitmapFactory.decodeFile() with image name: $imageName and path: $path")
    // TODO: HANDLE IMAGES THAT ARE CORRUPTED
    return bitmap.asImageBitmap()
}

private fun File.toExercise() = JSONObject(String(readBytes())).toExercise()