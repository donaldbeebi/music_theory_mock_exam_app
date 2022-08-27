package com.donald.musictheoryapp.pagedexercise

import android.content.res.Resources
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.question.*
import com.donald.musictheoryapp.util.toAlphabet
import com.donald.musictheoryapp.util.toRomanNumerals

class PagedExercise(private val pages: List<Page>)/*(val exercise: Exercise, questionStringFormatter: (String) -> String)*/ : Collection<Page> {
    //private val pages: List<Page>
    val pageCount: Int
        get() = pages.size
    val imagesToLoad: List<String> = run {
        val imagesToLoad = ArrayList<String>()
        for (page in pages) {
            page.images uniqueAddTo imagesToLoad
        }
        imagesToLoad
    }

    /*constructor(exercise: Exercise, resources: Resources) : this(
        exercise = exercise,
        questionStringFormatter = { numberString -> resources.getString(R.string.question_string, numberString) }
    )*/

    /*init {
        val imagesToLoad = ArrayList<String>()
        pages = ArrayList(countPages(exercise))

        exercise.sectionGroups.forEach { sectionGroup ->
            sectionGroup.forEach { section ->
                var sectionPageIndex: Int? = null

                // a dedicated page for the section descriptions
                if (section.descriptions.isNotEmpty()) {
                    val images = section.getSectionImagesRequired()
                    pages += Page(
                        sectionString = "${getSectionNumber(sectionGroup, section)} ${sectionGroup.name}",
                        sectionPageIndex = null,
                        questionString = "",
                        descriptions = section.descriptions,
                        question = null,
                        images = images
                    )
                    sectionPageIndex = pages.lastIndex
                    images uniqueAddTo imagesToLoad
                }

                // a page for the question
                section.questionGroups.forEach { group ->
                    group.parentQuestions.forEach { parentQuestion ->
                        parentQuestion.childQuestions.forEach { childQuestion ->
                            val questionString = questionStringFormatter(
                                getQuestionString(
                                    exercise.sectionGroups.size, section.questionGroups.size, group.parentQuestions.size, parentQuestion.childQuestions.size,
                                    section.number, group.number, parentQuestion.number, childQuestion.number
                                )
                            )
                            val images = group.getGroupImagesRequired() + parentQuestion.getQuestionImagesRequired()
                            pages += Page(
                                //sectionNumber = getSectionNumber(sectionGroup, section),
                                sectionString = "${getSectionNumber(sectionGroup, section)} ${sectionGroup.name}",
                                sectionPageIndex = sectionPageIndex,
                                questionString = questionString,
                                descriptions = group.descriptions + parentQuestion.descriptions + childQuestion.descriptions,
                                question = childQuestion,
                                images = images
                            )
                            images uniqueAddTo imagesToLoad
                        }
                    }
                }
            }
        }

        this.imagesToLoad = imagesToLoad
    }*/

    operator fun get(index: Int) = pages[index]

    fun pageIndexOf(question: ChildQuestion): Int {
        pages.forEachIndexed { index, page ->
            if (page.question === question) return index
        }
        throw IllegalArgumentException("Question is not found in the paged exercise")
    }

    // collection overridden properties and methods
    override fun iterator(): Iterator<Page> = pages.listIterator()

    override val size: Int
        get() = pages.size

    override fun contains(element: Page): Boolean = pages.contains(element)

    override fun containsAll(elements: Collection<Page>): Boolean = pages.containsAll(elements)

    override fun isEmpty(): Boolean = pages.isEmpty()

    companion object {
        operator fun invoke(exercise: Exercise, resources: Resources) = this(
            exercise = exercise,
            questionStringFormatter = { numberString -> resources.getString(R.string.question_string, numberString) }
        )
        operator fun invoke(exercise: Exercise, questionStringFormatter: (String) -> String): PagedExercise {
            //val imagesToLoad = ArrayList<String>()
            val pages = ArrayList<Page>(countPages(exercise))

            exercise.sectionGroups.forEach { sectionGroup ->
                sectionGroup.forEach { section ->
                    var sectionPageIndex: Int? = null

                    // a dedicated page for the section descriptions
                    if (section.descriptions.isNotEmpty()) {
                        val images = section.getSectionImagesRequired()
                        pages += Page(
                            sectionString = "${getSectionNumber(sectionGroup, section)} ${sectionGroup.name}",
                            pageToPeekIndex = null,
                            questionString = "",
                            descriptions = section.descriptions,
                            question = null,
                            images = images
                        )
                        sectionPageIndex = pages.lastIndex
                        //images uniqueAddTo imagesToLoad
                    }

                    // a page for the question
                    section.questionGroups.forEach { group ->
                        group.parentQuestions.forEach { parentQuestion ->
                            parentQuestion.childQuestions.forEach { childQuestion ->
                                val questionString = questionStringFormatter(
                                    getQuestionString(
                                        exercise.sectionGroups.size, section.questionGroups.size, group.parentQuestions.size, parentQuestion.childQuestions.size,
                                        section.number, group.number, parentQuestion.number, childQuestion.number
                                    )
                                )
                                val images = group.getGroupImagesRequired() + parentQuestion.getQuestionImagesRequired()
                                pages += Page(
                                    //sectionNumber = getSectionNumber(sectionGroup, section),
                                    sectionString = "${getSectionNumber(sectionGroup, section)} ${sectionGroup.name}",
                                    pageToPeekIndex = sectionPageIndex,
                                    questionString = questionString,
                                    descriptions = group.descriptions + parentQuestion.descriptions + childQuestion.descriptions,
                                    question = childQuestion,
                                    images = images
                                )
                                //images uniqueAddTo imagesToLoad
                            }
                        }
                    }
                }
            }
            //this.imagesToLoad = imagesToLoad
            return PagedExercise(pages)
        }
    }

}

private fun countPages(exercise: Exercise): Int {
    return exercise.parentQuestions.size + exercise.sectionGroups.sumOf { sectionGroup ->
        sectionGroup.count { section ->
            section.descriptions.isNotEmpty()
        }
    }
}

private infix fun List<String>.uniqueAddTo(imagesToLoad: ArrayList<String>) {
    this.forEach { if (it !in imagesToLoad) imagesToLoad.add(it) }
}

@Deprecated("")
private const val SECTION = 3
private const val GROUP = 0
private const val PARENT = 1
private const val CHILD = 2
private fun getQuestionString(
    sectionCount: Int, groupCount: Int, parentCount: Int, childCount: Int,
    sectionNumber: Int, groupNumber: Int, parentNumber: Int, childNumber: Int
): String {
    val builder = StringBuilder()
    var depth = 0
    for (element in GROUP..CHILD) {
        val number = when (element) {
            SECTION -> {
                if (sectionCount > 1) {
                    depth++
                    sectionNumber
                } else {
                    continue
                }
            }
            GROUP -> {
                if (groupCount > 1) {
                    depth++
                    groupNumber
                } else {
                    continue
                }
            }
            PARENT -> {
                if (parentCount > 1) {
                    depth++
                    parentNumber
                } else {
                    continue
                }
            }
            CHILD -> {
                if (childCount > 1) {
                    depth++
                    childNumber
                } else {
                    continue
                }
            }
            else -> throw IllegalStateException("$element")
        }
        when (depth) {
            1 -> {
                builder.append(number)
            }
            2 -> {
                builder.append('.').append(number)
            }
            3 -> {
                builder.append(number.toAlphabet())
            }
            4 -> {
                builder.append(number.toRomanNumerals())
            }
            else -> throw IllegalStateException("$depth")
        }
    }
    return builder.toString()
}

private fun getSectionNumber(sectionGroup: SectionGroup, section: Section): String {
    return if (sectionGroup.sections.size == 1) {
        sectionGroup.number.toString()
    } else {
        "${sectionGroup.number}.${section.number}"
    }
}