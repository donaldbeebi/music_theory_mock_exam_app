package com.donald.musictheoryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.composables.StandardScreen
import com.donald.musictheoryapp.composables.elevation
import com.donald.musictheoryapp.composables.activitycomposables.exerciseoverview.*
import com.donald.musictheoryapp.composables.general.CircularLoadingIndicator
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.question.Exercise
import com.donald.musictheoryapp.util.*

class ExerciseOverviewActivity : AppCompatActivity() {

    private var exerciseDisplayData by mutableStateOf<ExerciseDisplayData?>(null)
    private var editMode by mutableStateOf(false)

    private val goToQuestion = fun(currentExerciseIndex: Int, sectionGroupIndex: Int, sectionIndex: Int, groupIndex: Int) {
        val exerciseList = this.exerciseDisplayData?.exerciseList ?: throw IllegalStateException("Exercise list is null")
        val exerciseData = exerciseList[currentExerciseIndex].exerciseData()
        val intent = Intent(this, ExerciseActivity::class.java).apply {
            putExtra("action", "read")
            putExtra("exercise_data", exerciseData)
            putExtra("section_group_index", sectionGroupIndex)
            putExtra("section_index", sectionIndex)
            putExtra("group_index", groupIndex)
        }
        startActivity(intent)
    }

    private val onChangeExercise = fun(exerciseIndex: Int) {
        val exerciseDisplayData = this.exerciseDisplayData ?: throw IllegalStateException()
        require(exerciseIndex in exerciseDisplayData.exerciseList.indices)
        this.exerciseDisplayData = ExerciseDisplayData(
            exerciseDisplayData.exerciseList,
            exerciseIndex,
            expandedSectionGroupIndex = -1
        )
    }

    private val onViewQuestion = fun(sectionGroupIndex: Int, sectionIndex: Int, questionGroupIndex: Int) {
        val exerciseDisplayData = this.exerciseDisplayData ?: throw IllegalStateException()
        goToQuestion(exerciseDisplayData.exerciseIndex, sectionGroupIndex, sectionIndex, questionGroupIndex)
    }

    private val onExpandButtonPressed = fun(index: Int) {
        val displayData = this.exerciseDisplayData ?: throw IllegalStateException()
        val currentExpandedSectionGroupIndex = displayData.expandedSectionGroupIndex
        if (currentExpandedSectionGroupIndex == index) {
            this.exerciseDisplayData = ExerciseDisplayData(
                displayData.exerciseList,
                displayData.exerciseIndex,
                expandedSectionGroupIndex = -1
            )
        } else {
            this.exerciseDisplayData = ExerciseDisplayData(
                displayData.exerciseList,
                displayData.exerciseIndex,
                expandedSectionGroupIndex = index
            )
        }
    }

    private val onRedoExercise = fun() {
        val exerciseData = exerciseDisplayData?.exerciseList?.get(0) ?: throw IllegalStateException("Exercise list is null")
        val newExercise = createAndSaveRedoExercise(exerciseData)
        val intent = Intent(this, ExerciseActivity::class.java).apply {
            putExtra("action", "redo")
            putExtra("exercise_data", newExercise.exerciseData())
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
        finish()
    }

    private val onToggleEditMode = fun() {
        editMode = !editMode
    }

    private val onDeleteExercise = fun(exerciseIndex: Int) {
        val exerciseDisplayData = this.exerciseDisplayData ?: throw IllegalStateException()
        val exerciseList = exerciseDisplayData.exerciseList
        val exerciseData = exerciseList[exerciseIndex].exerciseData()

        runDiskIO {
            deleteExerciseSingle(exerciseData)
            runMain {
                val newExerciseList = exerciseList.filter { it != exerciseList[exerciseIndex] }
                this.exerciseDisplayData = ExerciseDisplayData(
                    newExerciseList,
                    exerciseDisplayData.exerciseIndex.coerceAtMost(newExerciseList.size - 1),
                    expandedSectionGroupIndex = -1
                )
                this.editMode = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val exerciseData = intent.getParcelableExtra<ExerciseData>("exercise_data")
            ?: throw IllegalStateException("Exercise data is not provided")

        setContent {
            CustomTheme {
                ScreenWithDialogs(
                    exerciseDisplayData,
                    editMode,
                    onExpandButtonPressed,
                    onBackPressed = { finish() },
                    onRedoBarStepPressed = onChangeExercise,
                    onEditButtonPressed = onToggleEditMode,
                    onViewQuestion,
                    onDeleteExercise,
                    onRedoExercise
                )
            }
        }

        runDiskIO {
            //val list = List(exerciseDataList.size) { index ->
            //    this.retrieveExercise(exerciseDataList[index])
            //}
            val list = getExerciseList(exerciseData)
            runMain {
                exerciseDisplayData = ExerciseDisplayData(
                    exerciseIndex = list.lastIndex,
                    exerciseList = list,
                    expandedSectionGroupIndex = -1
                )
            }
        }
    }

    override fun onBackPressed() { finish() }

}

@Preview
@Composable
private fun ScreenPreview() = CustomTheme {
    var exercise by remember { mutableStateOf(MockExercise1) }
    StandardScreen(
        activityTitle = "Overview",
        onBackPressed = { /*TODO*/ }
    ) {
        HistoryColumn(
            ExerciseDisplayData(
                exerciseIndex = 0,
                exerciseList = listOf(exercise, exercise),
                expandedSectionGroupIndex = 1
            ),
            editMode = false,
            {},
            onRedoBarStepPressed = {},
            {},
            { _, _, _ -> },
            {},
            { exercise = toggle(currentValue = exercise, MockExercise1, MockExercise2) },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun ScreenWithDialogs(
    displayData: ExerciseDisplayData?,
    editMode: Boolean,
    onExpandButtonPressed: (Int) -> Unit,
    onBackPressed: () -> Unit,
    onRedoBarStepPressed: (Int) -> Unit,
    onEditButtonPressed: () -> Unit,
    onViewQuestion: (Int, Int, Int) -> Unit,
    onDeleteExercise: (Int) -> Unit,
    onRedoExercise: () -> Unit
) {
    var deleteExerciseDialogData by remember { mutableStateOf<DialogData?>(null) }
    val promptDeleteExerciseDialog = fun(data: DialogData) { deleteExerciseDialogData = data }

    var showsRedoExerciseDialog by remember { mutableStateOf(false) }
    val promptRedoExerciseDialog = fun() { showsRedoExerciseDialog = true }

    Screen(
        displayData,
        editMode,
        onExpandButtonPressed,
        onRedoBarStepPressed,
        onEditButtonPressed,
        onViewQuestion,
        promptDeleteExerciseDialog,
        promptRedoExerciseDialog,
        onBackPressed
    )

    deleteExerciseDialogData?.let {
        DeleteExerciseDialog(
            dialogData = it,
            onConfirm = {
                onDeleteExercise(it.exerciseDataIndex)
                deleteExerciseDialogData = null
            },
            onCancel = { deleteExerciseDialogData = null }
        )
    }

    if (showsRedoExerciseDialog) {
        RedoExerciseDialog(
            onConfirm = {
                onRedoExercise()
                showsRedoExerciseDialog = false
            },
            onCancel = { showsRedoExerciseDialog = false }
        )
    }
}

@Composable
private fun Screen(
    displayData: ExerciseDisplayData?,
    editMode: Boolean,
    onExpandButtonPressed: (Int) -> Unit,
    onRedoBarStepPressed: (Int) -> Unit,
    onEditButtonPressed: () -> Unit,
    onViewQuestion: (Int, Int, Int) -> Unit,
    promptDeleteExerciseDialog: (DialogData) -> Unit,
    promptRedoExerciseDialog: () -> Unit,
    onBackPressed: () -> Unit
) = StandardScreen(
    activityTitle = stringResource(R.string.exercise_overview_activity_title),
    onBackPressed = onBackPressed
) {
    if (displayData != null) {
        val visibleState = remember { MutableTransitionState(false) }
        AnimatedVisibility(
            visibleState = visibleState.apply { targetState = true },
            enter = fadeIn(animationSpec = tween(durationMillis = 500))
        ) {
            HistoryColumn(
                displayData,
                editMode,
                onExpandButtonPressed,
                onRedoBarStepPressed,
                onEditButtonPressed,
                onViewQuestion,
                promptDeleteExerciseDialog,
                promptRedoExerciseDialog,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
    }
    val visibleState = remember { MutableTransitionState(false) }
    AnimatedVisibility(
        visibleState = visibleState.apply { targetState = displayData == null },
        enter = fadeIn(animationSpec = tween(durationMillis = 500, delayMillis = 500)),
        exit = fadeOut(animationSpec = tween(durationMillis = 500)),
        modifier = Modifier.align(Alignment.Center)
    ) {
        CircularLoadingIndicator(
            text = stringResource(R.string.exercise_overview_loading),
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun HistoryColumn(
    displayData: ExerciseDisplayData,
    editMode: Boolean,
    onExpandButtonPressed: (Int) -> Unit,
    onRedoBarStepPressed: (Int) -> Unit,
    onEditButtonPressed: () -> Unit,
    onViewQuestion: (Int, Int, Int) -> Unit,
    promptDeleteExerciseDialog: (DialogData) -> Unit,
    promptRedoExerciseDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        val (exerciseList, exerciseIndex, expandedSectionGroupIndex) = displayData
        val exercise = exerciseList[exerciseIndex]
        // redo bar and button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .height(80.dp)
        ) {
            Surface(
                elevation = elevation(1),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.weight(1F),
            ) {
                RedoBar(
                    modifier = Modifier.fillMaxSize(),
                    stepCount = exerciseList.size,
                    currentStep = exerciseIndex,
                    editMode = editMode,
                    onStepPressed = onRedoBarStepPressed,
                    onEditButtonPressed = onEditButtonPressed,
                    onDeletePressed = { exerciseDataIndex ->
                        promptDeleteExerciseDialog(
                            DialogData(
                                exerciseDataCount = exerciseIndex,
                                exerciseDataIndex = exerciseDataIndex
                            )
                        )
                    }
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(60.dp)
            ) {
                RedoButton(
                    onClick = promptRedoExerciseDialog,
                    elevation = elevation(1),
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp),
                )
                Text(
                    text = stringResource(R.string.redo_button),
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.secondary
                )
            }
        }

        // question list
        Box(
            modifier = Modifier.weight(1F)
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                elevation = elevation(1),
            ) {
                AnimatedContent(
                    targetState = exercise,
                    transitionSpec = {
                        val exitDurationMillis = 200
                        fadeIn(
                            animationSpec = tween(durationMillis = 500, delayMillis = exitDurationMillis)
                        ) with fadeOut(
                            animationSpec = tween(durationMillis = exitDurationMillis)
                        )
                        /*
                        val duration = 500
                        val delay = duration + 200
                        slideInHorizontally(
                            animationSpec = tween(durationMillis = duration, delayMillis = delay)
                        ) + fadeIn(
                            animationSpec = tween(durationMillis = duration, delayMillis = delay)
                        ) with slideOutHorizontally(
                            animationSpec = tween(durationMillis = duration)
                        ) + fadeOut(
                            animationSpec = tween(durationMillis = duration)
                        )

                         */
                    }
                ) { targetExercise ->
                    ExerciseOverviewColumn(
                        exercise = targetExercise,
                        currentExpandedSectionGroupIndex = expandedSectionGroupIndex,
                        onExpandButtonPressed = onExpandButtonPressed,
                        onViewQuestion = onViewQuestion,
                    )
                }
            }
        }
    }
}

private data class ExerciseDisplayData(
    val exerciseList: List<Exercise>,
    val exerciseIndex: Int,
    val expandedSectionGroupIndex: Int
)