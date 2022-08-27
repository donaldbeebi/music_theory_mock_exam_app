package com.donald.musictheoryapp.util

import android.os.Parcel
import android.os.Parcelable
import com.donald.musictheoryapp.question.Exercise
import com.donald.musictheoryapp.question.RedoInfo
import com.donald.musictheoryapp.util.Time.Companion.ms
import com.donald.musictheoryapp.util.Time.Companion.sec
import org.json.JSONObject
import java.util.*

fun exerciseFileName(type: Exercise.Type, date: Date, redoInfo: RedoInfo?): String {
    return when (redoInfo) {
        null -> "${type}_${date.time}"
        else -> "${redoInfo.parentExerciseData.fileName}_redo_${redoInfo.redoNumber}"
    }
}

data class ExerciseData(
    val type: Exercise.Type,
    val title: String,
    val date: Date,
    val timeRemaining: Time,
    val points: Int,
    val maxPoints: Int,
    val redoInfo: RedoInfo? = null,
    val residenceFolderName: String
) : Parcelable {

    val fileName: String
        get() = exerciseFileName(type, date, redoInfo)

    private val _children = ArrayList<ExerciseData>()

    private val children: List<ExerciseData>
        get() = _children

    val ended: Boolean
        get() = timeRemaining == 0.sec

    // TODO: DEBUG
    /*
    init {
        if (points != -1) points = Random().nextInt(30)
    }

     */

    private constructor(parcel: Parcel) : this(
        type = Exercise.Type.fromOrdinal(parcel.readInt()),
        title = parcel.readString() ?: throw IllegalStateException(),
        date = Date(parcel.readLong()),
        timeRemaining = parcel.readLong().ms,
        points = parcel.readInt(),
        maxPoints = parcel.readInt(),
        redoInfo = parcel.readParcelable(RedoInfo::class.java.classLoader),
        residenceFolderName = parcel.readString() ?: throw IllegalStateException()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeInt(type.ordinal)
            writeString(title)
            writeLong(date.time)
            writeLong(timeRemaining.millis)
            writeInt(points)
            writeInt(maxPoints)
            writeParcelable(redoInfo, 0)
            writeString(residenceFolderName)
        }
    }

    override fun describeContents() = 0

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("type", type.toString())
            put("title", title)
            put("date", date.toJsonValue())
            put("time_remaining", timeRemaining.toJsonValue())
            put("points", points)
            put("max_points", maxPoints)
            redoInfo?.let { put("redo_info", it) }
            put("residence_folder_name", residenceFolderName)
        }
    }

    companion object CREATOR : Parcelable.Creator<ExerciseData> {

        override fun createFromParcel(source: Parcel?): ExerciseData? {
            return source?.let { ExerciseData(it) }
        }

        override fun newArray(size: Int) = arrayOfNulls<ExerciseData>(size)

        fun fromExercise(exercise: Exercise) = ExerciseData(
            type = exercise.type,
            title = exercise.title,
            date = exercise.date,
            timeRemaining = exercise.timeRemaining,
            points = exercise.points,
            maxPoints = exercise.maxPoints,
            redoInfo = exercise.redoInfo,
            residenceFolderName = exercise.residenceFolderName
        )

        fun fromJson(jsonObject: JSONObject): ExerciseData {
            return ExerciseData(
                jsonObject.getExerciseType("type"),
                jsonObject.getExerciseTitle("title"),
                jsonObject.getDate("date"),
                jsonObject.getTime("time_remaining"),
                jsonObject.getInt("points"),
                jsonObject.getInt("max_points"),
                if (jsonObject.has("redo_info")) jsonObject.getRedoInfo("redo_info") else null,
                jsonObject.getString("residence_folder_name")
            )
        }

    }

}

fun Exercise.exerciseData() = ExerciseData.fromExercise(this)