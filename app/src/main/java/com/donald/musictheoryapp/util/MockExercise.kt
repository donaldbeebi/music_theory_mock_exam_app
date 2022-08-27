package com.donald.musictheoryapp.util

import com.donald.musictheoryapp.question.Exercise
import com.donald.musictheoryapp.question.QuestionGroup
import com.donald.musictheoryapp.question.Section
import com.donald.musictheoryapp.question.SectionGroup
import com.donald.musictheoryapp.util.Time.Companion.hr
import java.util.*

fun mockExercise(
    title: String = "",
    type: Exercise.Type = Exercise.Type.Test,
    date: Date,
    finished: Boolean
) = Exercise(
    savedPageIndex = 1,
    title = title,
    type = type,
    date = date,
    timeRemaining = if (finished) 0.hr else 2.hr,
    sectionGroups = List(3) { groupIndex ->
        SectionGroup(
            groupIndex + 1,
            "section ${groupIndex + 1} name",
            listOf(
                Section(
                    number = groupIndex,
                    questionGroups = List(2) { i -> QuestionGroup(i, "Question group ${i + 1}", emptyList(), emptyList()) }
                ),
                Section(
                    number = groupIndex,
                    questionGroups = List(2) { i -> QuestionGroup(i, "Question group ${i + 1}", emptyList(), emptyList()) }
                )
            )
        )
    },
    residenceFolderName = ""
)