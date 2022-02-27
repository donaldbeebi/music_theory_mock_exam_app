package com.donald.musictheoryapp.question

import com.donald.musictheoryapp.Utils.*
import org.json.JSONObject
import com.donald.musictheoryapp.Utils.Time.Companion.hr
import org.json.JSONArray
import java.lang.StringBuilder
import java.util.*

class Exercise(
    val title: String,
    val date: Date,
    var timeRemaining: Time,
    private val sections: Array<QuestionSection>,
    private val groups: Array<QuestionGroup>,
    private val questions: Array<Question>
) {

    val points = questions.sumOf { it.points }
    val maxPoints = questions.sumOf { it.maxPoints }
    val questionCount = questions.size

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

    fun sectionAt(sectionIndex: Int): QuestionSection {
        return sections[sectionIndex]
    }

    fun groupAt(groupIndex: Int): QuestionGroup {
        return groups[groupIndex]
    }

    fun questionAt(questionIndex: Int): Question {
        return questions[questionIndex]
    }

    fun groupCount(): Int {
        return groups.size
    }

    fun questionAt(groupIndex: Int, localQuestionIndex: Int): Question {
        return groups[groupIndex].questions[localQuestionIndex]
    }

    @Deprecated("Use property")
    fun questionCount(): Int {
        return questions.size
    }

    fun questionIndexOf(question: Question): Int {
        for (i in questions.indices) {
            if (questions[i] === question) return i
        }
        return -1
    }

    fun groupIndexOf(group: QuestionGroup): Int {
        for (i in groups.indices) {
            if (groups[i] === group) return i
        }
        return -1
    }

    fun sectionIndexOf(section: QuestionSection): Int {
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

        fun fromJson(jsonObject: JSONObject): Exercise {
            val sections = jsonObject.getSections()
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
                title = jsonObject.getString("title"),
                date = jsonObject.getDate("date"),
                timeRemaining = if (jsonObject.isNull("time_remaining")) 2.hr else jsonObject.getTime("time_remaining"),
                sections = sections,
                groups = groups.toTypedArray(),
                questions = questions.toTypedArray()
            )
        }

    }

}