package com.donald.musictheoryapp.composables.activitycomposables.exerciseoverview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.donald.musictheoryapp.composables.general.ColorBarListDivider
import com.donald.musictheoryapp.composables.general.listitem.ListItemButtonState
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.question.Exercise
import com.donald.musictheoryapp.question.QuestionGroup
import com.donald.musictheoryapp.question.Section
import com.donald.musictheoryapp.question.SectionGroup
import com.donald.musictheoryapp.util.Time.Companion.hr
import java.util.*

val MockExercise1 = Exercise(
    savedPageIndex = 1,
    type = Exercise.Type.Test,
    title = "Test title",
    date = Date(),
    timeRemaining = 2.hr,
    redoInfo = null,
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
    }
)

val MockExercise2 = Exercise(
    savedPageIndex = 1,
    type = Exercise.Type.Test,
    title = "Test title",
    date = Date(),
    timeRemaining = 2.hr,
    redoInfo = null,
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
    }
)


@Preview
@Composable
private fun SectionGroupLazyColumnPreview() = CustomTheme(darkTheme = false) {
    ExerciseOverviewColumn(
        MockExercise1,
        currentExpandedSectionGroupIndex = 1,
        {},
        { _, _, _ -> },
    )
}

@Composable
fun ExerciseOverviewColumn(
    exercise: Exercise,
    currentExpandedSectionGroupIndex: Int,
    onExpandButtonPressed: (Int) -> Unit,
    onViewQuestion: (Int, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val sectionGroups = exercise.sectionGroups
    LazyColumn(
        modifier = modifier
    ) {
        items(sectionGroups.size) { sectionGroupIndex ->
            val sectionGroup = sectionGroups[sectionGroupIndex]
            val isExpanded = sectionGroupIndex == currentExpandedSectionGroupIndex
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OverviewListItem(
                    sectionGroup = sectionGroup,
                    state = OverviewListItemState.Enabled(
                        expanded = sectionGroupIndex == currentExpandedSectionGroupIndex,
                        onExpand = { onExpandButtonPressed(sectionGroupIndex) },
                        onViewQuestion = { sectionIndex, questionGroupIndex ->
                            onViewQuestion(sectionGroupIndex, sectionIndex, questionGroupIndex)
                        }
                    ),
                    showsTopShadow = sectionGroupIndex != sectionGroups.lastIndex
                )
            }
            AnimatedVisibility(sectionGroupIndex != sectionGroups.lastIndex && !isExpanded) {
                ColorBarListDivider(color = MaterialTheme.colors.primary)
            }
        }
    }
}