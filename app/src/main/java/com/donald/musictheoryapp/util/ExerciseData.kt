package com.donald.musictheoryapp.util

import android.os.Parcel
import android.os.Parcelable
import com.donald.musictheoryapp.question.Exercise
import com.donald.musictheoryapp.util.Time.Companion.ms
import com.donald.musictheoryapp.util.Time.Companion.sec
import java.util.*
import kotlin.Comparator

data class ExerciseData(
    val type: Exercise.Type,
    val title: String,
    val date: Date,
    val timeRemaining: Time,
    var points: Int,
    val maxPoints: Int
) : Parcelable {

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
        maxPoints = parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeInt(type.ordinal)
            writeString(title)
            writeLong(date.time)
            writeLong(timeRemaining.millis)
            writeInt(points)
            writeInt(maxPoints)
        }
    }

    override fun describeContents() = 0

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
            maxPoints = exercise.maxPoints
        )

    }

}