package com.donald.musictheoryapp.pagedexercise

import android.content.res.Resources
import android.util.Log
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.question.Exercise
import com.donald.musictheoryapp.question.Question

class PagedExercise(val exercise: Exercise, resources: Resources) {

    private val pages: List<Page>
    val pageCount: Int
        get() = pages.size

    init {
        pages = ArrayList(countPages(exercise))

        exercise.sections.forEach { section ->

            if (section.descriptions.isNotEmpty()) {
                pages += Page(
                    sectionNumber = section.number.toString(),
                    sectionName = section.name,
                    questionString = "",
                    descriptions = section.descriptions,
                    question = null
                )
            }

            section.groups.forEach { group ->
                group.questions.forEach { question ->
                    val subQuestionString = if (group.questions.size > 1) question.number.toAlphabet() else ""
                    val questionString = resources.getString(R.string.question_string, "${group.number}$subQuestionString")
                    pages += Page(
                        sectionNumber = section.number.toString(),
                        sectionName = section.name,
                        questionString = questionString,
                        group.descriptions + question.descriptions,
                        question
                    )
                }
            }

        }

    }

    operator fun get(index: Int) = pages[index]

    fun pageIndexOf(question: Question): Int {
        pages.forEachIndexed { index, page ->
            if (page.question === question) return index
        }
        throw IllegalArgumentException("Question is not found in the paged exercise")
    }

}

private fun countPages(exercise: Exercise): Int {
    return exercise.questions.size + exercise.sections.count { it.descriptions.isNotEmpty() }
}

private fun Int.toAlphabet(): Char {
    require(this in 1..26)
    return ('a'.code + (this - 1)).toChar()
}