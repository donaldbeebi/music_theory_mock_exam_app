package com.donald.musictheoryapp.question

import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.util.*
import org.json.JSONObject
import com.donald.musictheoryapp.util.Time.Companion.sec
import org.json.JSONArray
import java.lang.StringBuilder
import java.util.*

class Exercise(
    var savedPageIndex: Int,
    val type: Type,
    val title: String,
    val date: Date,
    var timeRemaining: Time,
    val sections: Array<QuestionSection>,
    val groups: Array<QuestionGroup>,
    val questions: Array<Question>
) {

    val ended: Boolean
        get() = timeRemaining == 0.sec
    val points: Int
        get() = if (ended) questions.sumOf { it.points } else -1
    val maxPoints: Int
        get() = questions.sumOf { it.maxPoints }
    val questionCount: Int
        get() = questions.size

    fun sectionOf(group: QuestionGroup): QuestionSection {
        sections.forEach { section ->
            if (group in section) return section
        }
        throw IllegalStateException("Question group is not found in this section")
    }

    fun sectionOf(question: Question): QuestionSection {
        sections.forEach { section ->
            if (question in section) return section
        }
        throw IllegalStateException("Question is not found in this section")
    }

    fun groupOf(question: Question): QuestionGroup {
        groups.forEach { group ->
            if (question in group) return group
        }
        throw IllegalStateException("Question is not found in this section")
    }

    @Deprecated("Use property instead", ReplaceWith("sections[sectionIndex]"))
    fun sectionAt(sectionIndex: Int): QuestionSection {
        return sections[sectionIndex]
    }

    @Deprecated("Use property instead", ReplaceWith("groups[groupIndex]"))
    fun groupAt(groupIndex: Int): QuestionGroup {
        return groups[groupIndex]
    }

    @Deprecated("Use property instead", ReplaceWith("questions[questionIndex]"))
    fun questionAt(questionIndex: Int): Question {
        return questions[questionIndex]
    }

    @Deprecated("Use property instead", ReplaceWith("groups.size"))
    fun groupCount(): Int {
        return groups.size
    }

    fun questionAt(groupIndex: Int, localQuestionIndex: Int): Question {
        return groups[groupIndex].questions[localQuestionIndex]
    }

    @Deprecated("Use property", ReplaceWith("questions.size"))
    fun questionCount(): Int {
        return questions.size
    }

    fun indexOf(question: Question): Int {
        for (i in questions.indices) {
            if (questions[i] === question) return i
        }
        return -1
    }

    fun indexOf(group: QuestionGroup): Int {
        for (i in groups.indices) {
            if (groups[i] === group) return i
        }
        return -1
    }

    fun indexOf(section: QuestionSection): Int {
        for (i in sections.indices) {
            if (sections[i] === section) return i
        }
        return -1
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (group in groups) {
            builder.append(group.toString()).append("\n")
            for (question in group.questions) {
                builder.append("    ").append(question.toString()).append("\n")
            }
        }
        return builder.toString()
    }

    fun sectionCount(): Int {
        return sections.size
    }

    fun toJson(): JSONObject {
        val jsonObject = JSONObject()
        val imageArray = JSONArray()
        val sectionArray = JSONArray()
        for (section in sections) {
            section.registerImages(imageArray)
            sectionArray.put(section.toJson())
        }
        jsonObject.apply {
            put("images", imageArray)
            put("saved_page_index", savedPageIndex)
            put("type", type.toString())
            put("title", title)
            put("date", date.time)
            put("time_remaining", timeRemaining.millis)
            put("points", points)
            put("max_points", maxPoints)
            put("sections", sectionArray)
        }
        return jsonObject
    }

    companion object {

        fun fromJsonOrNull(jsonObject: JSONObject): Exercise? {
            val sections = jsonObject.getSectionsOrNull() ?: return null
            val groups = ArrayList<QuestionGroup>()
            sections.forEach { section ->
                groups.addAll(section.groups)
            }
            val questions = ArrayList<Question>()
            sections.forEach { section ->
                section.groups.forEach { group ->
                    questions.addAll(group.questions)
                }
            }
            return Exercise(
                savedPageIndex = jsonObject.getIntOrNull("saved_page_index") ?: return null,
                type = jsonObject.getExerciseTypeOrNull() ?: return null,
                title = jsonObject.getExerciseTitleOrNull() ?: return null,
                date = jsonObject.getDateOrNull() ?: return null,
                timeRemaining = jsonObject.getTimeRemainingOrNull() ?: return null,
                sections = sections,
                groups = groups.toTypedArray(),
                questions = questions.toTypedArray()
            )
        }

    }

    enum class Type(private val string: String, val resId: Int) {

        TEST("test", R.string.exercise_type_test), PRACTICE("practice", R.string.exercise_type_practice);

        override fun toString() = string

        companion object {

            private val types = listOf(TEST, PRACTICE)

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

        }

    }

}