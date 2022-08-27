package com.donald.musictheoryapp.question

import android.os.Parcel
import android.os.Parcelable
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.util.*
import com.donald.musictheoryapp.util.Time.Companion.hr
import org.json.JSONObject
import com.donald.musictheoryapp.util.Time.Companion.sec
import org.json.JSONArray
import java.lang.StringBuilder
import java.util.*

private val ExerciseDefaultFinishTime = 2.hr

@Deprecated("Use residence folder name instead")
data class RedoInfo(
    val parentExerciseData: ExerciseData,
    val redoNumber: Int,
    val folderName: String
) : Parcelable {

    private constructor(parcel: Parcel) : this(
        parentExerciseData = parcel.readParcelable<ExerciseData>(ExerciseData::class.java.classLoader) ?: throw IllegalStateException(),
        redoNumber = parcel.readInt(),
        folderName = parcel.readString() ?: throw IllegalStateException()
    )

    override fun describeContents() = 0

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("parent_exercise_data", parentExerciseData.toJson())
            put("redo_number", redoNumber)
            put("folder_name", folderName)
        }
    }

    companion object CREATOR : Parcelable.Creator<RedoInfo> {
        override fun createFromParcel(source: Parcel?): RedoInfo? {
            return source?.let { RedoInfo(it) }
        }
        override fun newArray(size: Int) = arrayOfNulls<RedoInfo>(size)
        fun fromJson(jsonObject: JSONObject): RedoInfo {
            return RedoInfo(
                jsonObject.getExerciseData("parent_exercise_data"),
                jsonObject.getInt("redo_number"),
                jsonObject.getString("folder_name")
            )
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(parentExerciseData, flags)
        parcel.writeInt(redoNumber)
        parcel.writeString(folderName)
    }

}

class Exercise(
    var savedPageIndex: Int,
    val type: Type,
    val title: String,
    val date: Date,
    var timeRemaining: Time,
    val sectionGroups: List<SectionGroup>,
    val residenceFolderName: String
) {
    var redoInfo: RedoInfo? = null
    val isRedo: Boolean
        get() = redoInfo != null
    val hasEnded: Boolean
        get() = timeRemaining == 0.sec
    val points: Int
        get() = if (hasEnded) parentQuestions.sumOf { it.points } else -1
    val maxPoints: Int
        get() = parentQuestions.sumOf { it.maxPoints }
    val questionCount: Int
        get() = parentQuestions.size
    val sections: List<Section> = sectionGroups.flatMap { it.sections }
    val groups: List<QuestionGroup> = sections.flatMap { it.questionGroups }
    val parentQuestions: List<ParentQuestion> = groups.flatMap { it.parentQuestions }
    val childQuestions: List<ChildQuestion> = parentQuestions.flatMap { it.childQuestions }
    val fileName: String
        get() = exerciseFileName(type, date, redoInfo)

    @Deprecated("")
    constructor(
        savedPageIndex: Int,
        type: Type,
        title: String,
        date: Date,
        timeRemaining: Time,
        sectionGroups: List<SectionGroup>,
        redoInfo: RedoInfo? = null
    ) : this(savedPageIndex, type, title, date, timeRemaining, sectionGroups, "") {
        this.redoInfo = redoInfo
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (group in groups) {
            builder.append(group.toString()).append("\n")
            for (question in group.parentQuestions) {
                builder.append("    ").append(question.toString()).append("\n")
            }
        }
        return builder.toString()
    }

    fun sectionCount(): Int {
        return sectionGroups.size
    }

    @Deprecated("Use copyNew() instead")
    fun copyNew(redoInfo: RedoInfo): Exercise {
        require(!isRedo)
        val copiedSections = List(sectionGroups.size) { i -> sectionGroups[i].copyNew() }
        return Exercise(
            savedPageIndex = 0,
            type,
            title,
            Date(),
            timeRemaining = 2.hr,
            copiedSections,
            redoInfo
        )
    }

    fun copyNew(): Exercise {
        val copiedSections = List(sectionGroups.size) { i -> sectionGroups[i].copyNew() }
        return Exercise(
            savedPageIndex = 0,
            type,
            title,
            Date(),
            timeRemaining = ExerciseDefaultFinishTime,
            copiedSections,
            residenceFolderName = residenceFolderName,
        )
    }

    fun toJson(): JSONObject {
        val jsonObject = JSONObject()
        val imageArray = JSONArray()
        val sectionArray = JSONArray()
        for (sectionGroup in sectionGroups) {
            sectionGroup.registerImages(imageArray)
            sectionArray.put(sectionGroup.toJson())
        }
        jsonObject.apply {
            put("images", imageArray)
            put("saved_page_index", savedPageIndex)
            put("type", type.toJsonValue())
            put("title", title)
            put("date", date.toJsonValue())
            put("time_remaining", timeRemaining.toJsonValue())
            put("points", points)
            put("max_points", maxPoints)
            put("section_groups", sectionArray)
            redoInfo?.let { put("redo_info", it.toJson()) }
            put("residence_folder_name", residenceFolderName)
        }
        return jsonObject
    }

    companion object {

        fun fromJson(jsonObject: JSONObject): Exercise {
            return Exercise(
                savedPageIndex = jsonObject.getInt("saved_page_index"),
                type = jsonObject.getExerciseType("type"),
                title = jsonObject.getExerciseTitle("title"),
                date = jsonObject.getDate("date"),
                timeRemaining = jsonObject.getTime("time_remaining"),
                sectionGroups = jsonObject.getSectionGroups("section_groups"),
                //redoInfo = if (jsonObject.has("redo_info")) jsonObject.getRedoInfo("redo_info") else null,
                residenceFolderName = jsonObject.getString("residence_folder_name")
            )
        }

        @Deprecated("Use JSONObject.getExercise() instead")
        fun fromJsonOrNull(jsonObject: JSONObject, residenceFolderName: String? = null): Exercise? {
            return Exercise(
                savedPageIndex = jsonObject.getIntOrNull("saved_page_index") ?: return null,
                type = jsonObject.getExerciseTypeOrNull("type") ?: return null,
                title = jsonObject.getExerciseTitleOrNull("title") ?: return null,
                date = jsonObject.getDateOrNull("date") ?: return null,
                timeRemaining = jsonObject.getTimeOrNull("time_remaining") ?: return null,
                sectionGroups = jsonObject.getSectionGroupsOrNull("section_groups") ?: return null,
                //redoInfo = if (jsonObject.has("redo_info")) jsonObject.getRedoInfo("redo_info") else null
                residenceFolderName = jsonObject.getString("residence_folder_name")
            )
        }

    }

    enum class Type(private val string: String, val resId: Int) {
        Test("test", R.string.exercise_type_test), Practice("practice", R.string.exercise_type_practice);
        override fun toString() = string
        fun toJsonValue(): String {
            return string
        }
        companion object {
            private val types = listOf(Test, Practice)
            fun fromString(string: String): Type {
                types.forEach {
                    if (it.string == string) return it
                }
                throw IllegalStateException("No matching string")
            }
            fun fromStringOrNull(string: String): Type? {
                types.forEach {
                    if (it.string == string) return it
                }
                return null
            }
            fun fromOrdinal(ordinal: Int): Type {
                if (ordinal !in 0..types.size) throw IllegalStateException("No matching ordinal")
                return types[ordinal]
            }
            fun fromJsonValue(string: String): Type {
                return fromString(string)
            }
        }
    }

}