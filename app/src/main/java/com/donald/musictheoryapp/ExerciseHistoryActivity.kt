package com.donald.musictheoryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.donald.musictheoryapp.composables.StandardScreen
import com.donald.musictheoryapp.composables.activitycomposables.exercisehistory.HistoryListItem
import com.donald.musictheoryapp.composables.activitycomposables.exercisehistory.HistoryListItemState
import com.donald.musictheoryapp.composables.general.*
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.popupMenuDimens
import com.donald.musictheoryapp.question.Exercise
import com.donald.musictheoryapp.util.*
import com.donald.musictheoryapp.util.Time.Companion.hr
import java.util.*

// TODO: LOADING SCREEN BEFORE EXERCISE LIST IS LOADED
// TODO: LOADING SCREEN WHEN SORTING

class ExerciseHistoryActivity : AppCompatActivity() {
    private var state: State by mutableStateOf(newRegularState())
    private var displayDataList by mutableStateOf<List<DisplayData>?>(null)

    private fun onViewExercise(exerciseData: ExerciseData) {
        val intent = Intent(this, ExerciseOverviewActivity::class.java)
        intent.putExtra("exercise_data", exerciseData)
        startActivity(intent)
    }

    private fun onResumeExercise(exerciseData: ExerciseData) {
        runDiskIO {
            val sortedList = getExerciseDataList(exerciseData).sortedByDescending { it.date }
            val exerciseDataToDo = sortedList[0]
            runMain {
                val intent = Intent(this, ExerciseActivity::class.java)
                intent.putExtra("action", "resume")
                intent.putExtra("exercise_data", exerciseDataToDo)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun onSort(sortType: SortType) {
        runCPUBound {
            val displayDataList = this.displayDataList ?: throw IllegalStateException()
            val filteredList = displayDataList.filter { it.isVisible }
            val sortedList = when (sortType) {
                SortType.LatestFirst -> filteredList.sortedByDescending { it.exerciseData.date }
                SortType.EarliestFirst -> filteredList.sortedBy { it.exerciseData.date }
                SortType.TestFirst -> filteredList.sortedBy { it.exerciseData.type }
                SortType.PracticeFirst -> filteredList.sortedByDescending { it.exerciseData.type }
                SortType.UnfinishedFirst -> filteredList.sortedBy { it.exerciseData.ended }
                SortType.FinishedFirst -> filteredList.sortedByDescending { it.exerciseData.ended }
            }
            runMain { this.displayDataList = sortedList }
        }
    }

    private fun onDelete(indexToRemove: Int) {
        val displayDataList = this.displayDataList ?: throw IllegalStateException()
        displayDataList[indexToRemove].startDeletion()
        //runDiskIO { deleteExerciseAll(displayDataList[indexToRemove].exerciseData) }
    }

    private fun onDismissFocusContent() {
        state = newRegularState()
    }

    private val onBackPressed = fun() {
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Screen(
                displayDataList,
                state,
                onSortButtonClick = { state = newSortPopupMenuState() },
                onDeleteButtonClick = { state = newDeletingExerciseState() },
                onReturnToRegular = { state = newRegularState() },
                onBackPressed
            )
        }

        runDiskIO {
            val exerciseDataList = getExerciseDataList().sortedByDescending { it.date }
            val displayDataList = exerciseDataList.map { DisplayData(it) }
            runMain { this.displayDataList = displayDataList }
        }
        /*
        runDiskIO {
            val list = List(5) { index ->
                DisplayData(
                    mockExercise(
                        title = index.toString(),
                        type = if (index % 2 == 0) Exercise.Type.Test else Exercise.Type.Practice,
                        date = Date(),
                        finished = index % 2 == 0
                    ).exerciseData()
                ).also { Thread.sleep(10) }
            }
            runMain { this.displayDataList = list }
        }

         */
    }

    override fun onBackPressed() = finish()

    sealed class State {
        class Regular(
            val onViewExercise: (ExerciseData) -> Unit,
            val onPromptResumeExercise: (ExerciseData) -> Unit,
        ) : State()
        class ConfirmResumeDialog(
            val onConfirmResume: () -> Unit,
            val onDismiss: () -> Unit
        ) : State()
        class SortPopupMenu(
            val onSort: (SortType) -> Unit,
            val onDismiss: () -> Unit
        ) : State()
        class DeletingExercise(
            val onDelete: (Int) -> Unit
        ) : State()
    }

    private fun newRegularState() = State.Regular(
        onViewExercise = this::onViewExercise,
        onPromptResumeExercise = { exerciseData ->
            state = State.ConfirmResumeDialog(
                onConfirmResume = { onResumeExercise(exerciseData) },
                onDismiss = this::onDismissFocusContent
            )
        }
    )
    /*private fun newConfirmResumeDialogState() = State.ConfirmResumeDialog(
        onConfirm = this::onStartExercise,
        onDismiss = this::onDismissFocusContent
    )*/
    private fun newSortPopupMenuState() = State.SortPopupMenu(
        onSort = this::onSort,
        onDismiss = this::onDismissFocusContent
    )
    private fun newDeletingExerciseState() = State.DeletingExercise(
        onDelete = this::onDelete
    )
}

@Preview
@Composable
private fun MainContentPreview() = CustomTheme {
    Screen(
        List(0) {
            DisplayData(
                ExerciseData(
                    type = Exercise.Type.Test,
                    date = Date(),
                    title = "",
                    timeRemaining = 2.hr,
                    points = 0,
                    maxPoints = 0,
                    residenceFolderName = ""
                )
            )
        },
        onReturnToRegular = {},
        onDeleteButtonClick = {},
        onSortButtonClick = {},
        state = ExerciseHistoryActivity.State.Regular(
            {},
            {}
        ),
        onBackPressed = {}
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun Screen(
    displayDataList: List<DisplayData>?,
    state: ExerciseHistoryActivity.State,
    onSortButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onReturnToRegular: () -> Unit,
    //onViewExercise: (ExerciseData) -> Unit,
    //onStartExercise: (ExerciseData) -> Unit,
    //onSort: (SortType) -> Unit,
    //onDelete: (Int) -> Unit,
    onBackPressed: () -> Unit
) = CustomTheme {
    StandardScreen(
        activityTitle = stringResource(R.string.exercise_history_activity_title),
        showsFocusBackdrop = state is ExerciseHistoryActivity.State.SortPopupMenu
                || state is ExerciseHistoryActivity.State.ConfirmResumeDialog,
        onBackPressed = onBackPressed,
        onBackDropTap = when (state) {
            is ExerciseHistoryActivity.State.SortPopupMenu -> state.onDismiss
            is ExerciseHistoryActivity.State.ConfirmResumeDialog -> state.onDismiss
            else -> fun() { Unit }
        },
        appBarExtras = {
            AppBarButtons(
                deleteMode = state is ExerciseHistoryActivity.State.DeletingExercise,
                onDeleteMode = when (state) {
                    is ExerciseHistoryActivity.State.DeletingExercise -> onReturnToRegular
                    else -> onDeleteButtonClick
                },
                promptPopupMenu = when (state) {
                    is ExerciseHistoryActivity.State.SortPopupMenu -> onReturnToRegular
                    else -> onSortButtonClick
                }
            )
        },
        focusContent = { FocusContent(state) }
    ) {
        AnimatedContent(
            targetState = displayDataList,
            transitionSpec = {
                val exitDurationMillis = 200
                fadeIn(
                    animationSpec = tween(durationMillis = 500, delayMillis = exitDurationMillis)
                ) with fadeOut(
                    animationSpec = tween(durationMillis = exitDurationMillis)
                )
            }
        ) { targetDisplayDataList ->
            when (targetDisplayDataList?.size) {
                null -> AnimatedVisibility(
                    visibleState = remember { MutableTransitionState(false).apply { targetState = true } },
                    enter = fadeIn(tween(delayMillis = 500, durationMillis = 500))
                ) {
                    Box(Modifier.fillMaxSize()) {
                        CircularLoadingIndicator(text = stringResource(R.string.exercise_history_loading), modifier = Modifier.align(Alignment.Center))
                    }
                }
                0 -> Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(R.string.exercise_list_no_exercise),
                        fontSize = 18.sp,
                        color = MaterialTheme.colors.onBackground,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> HistoryColumn(targetDisplayDataList, state)
            }
        }
    }
}

@Composable
private fun HistoryColumn(
    displayDataList: List<DisplayData>,
    state: ExerciseHistoryActivity.State,
) {
    LazyColumn {
        items(displayDataList.size) { index ->
            val (exerciseData, visibleState) = displayDataList[index]
            AnimatedVisibility(visibleState = visibleState) {
                Column {
                    HistoryListItem(
                        exerciseData,
                        when (state) {
                            is ExerciseHistoryActivity.State.Regular -> HistoryListItemState.Regular(
                                onNextButtonClicked = if (exerciseData.ended) {
                                    { state.onViewExercise(displayDataList[index].exerciseData) }
                                } else {
                                    { state.onPromptResumeExercise(displayDataList[index].exerciseData) }
                                }
                            )
                            is ExerciseHistoryActivity.State.DeletingExercise -> HistoryListItemState.Deleting(
                                onDelete = { state.onDelete(index) }
                            )
                            else -> HistoryListItemState.Disabled
                        }
                    )
                    AnimatedVisibility(
                        visible = index != displayDataList.lastVisibleIndex
                    ) {
                        ColorBarListDivider(
                            color = if (state is ExerciseHistoryActivity.State.DeletingExercise) {
                                MaterialTheme.colors.secondary
                            } else {
                                MaterialTheme.colors.primary
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxScope.FocusContent(
    //showsPopup: Boolean,
    //onSort: (SortType) -> Unit
    state: ExerciseHistoryActivity.State
) {
    AnimatedVisibility(
        visible = state is ExerciseHistoryActivity.State.SortPopupMenu,
        enter = slideInVertically(
            initialOffsetY = { height -> -height },
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)
        ),
        exit = slideOutVertically(
            targetOffsetY = { height -> -height },
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        ),
        modifier = Modifier
            .align(Alignment.TopEnd)
            .clipToBounds()
            .padding(bottom = 32.dp)
    ) {
        PopupMenu(
            menu = sortMenu,
            rowHeight = MaterialTheme.popupMenuDimens.popupMenuRowHeight,
            state = when (state) {
                is ExerciseHistoryActivity.State.SortPopupMenu -> {
                    PopupMenuState.Enabled(
                        onFinalItemClicked = { sortType -> state.onSort(sortType) }
                    )
                }
                else -> {
                    PopupMenuState.Disabled
                }
            },
            modifier = Modifier
                .width(250.dp)
                .padding(12.dp)
        )
    }
    if (state is ExerciseHistoryActivity.State.ConfirmResumeDialog) StandardAlertDialog(
        title = stringResource(R.string.confirm_resume_dialog_title),
        description = stringResource(R.string.confirm_resume_dialog_description),
        positiveText = stringResource(R.string.confirm_resume_dialog_yes),
        onPositive = state.onConfirmResume,
        negativeText = stringResource(R.string.confirm_resume_dialog_no),
        onNegative = state.onDismiss,
        onDismiss = state.onDismiss
    )
}

@Composable
private fun AppBarButtons(deleteMode: Boolean, onDeleteMode: () -> Unit, promptPopupMenu: () -> Unit) {
    Row {
        AppBarButton(
            painter = if (deleteMode) painterResource(R.drawable.ic_cross) else painterResource(R.drawable.ic_edit),
            imageSizeFraction = 0.65F,
            onClick = onDeleteMode
        )
        AppBarButton(
            painter = painterResource(R.drawable.ic_more_button),
            onClick = promptPopupMenu
        )
    }
}

private val sortByDate = page(
    title = { PopupMenuTitleText(stringResource(R.string.sort_context_menu_sort_by_title)) },
    item(
        content = { PopupMenuItemText(stringResource(R.string.sort_context_menu_sort_by_latest_first)) },
        value = SortType.LatestFirst
    ),
    item(
        content = { PopupMenuItemText(stringResource(R.string.sort_context_menu_sort_by_earliest_first)) },
        value = SortType.EarliestFirst
    )
)

private val sortByType = page(
    title = { PopupMenuTitleText(stringResource(R.string.sort_context_menu_sort_by_title)) },
    item(
        content = { PopupMenuItemText(stringResource(R.string.sort_context_menu_sort_by_test_first)) },
        value = SortType.TestFirst
    ),
    item(
        content = { PopupMenuItemText(stringResource(R.string.sort_context_menu_sort_by_practice_first)) },
        value = SortType.PracticeFirst
    )
)

private val sortByFinishStatus = page(
    title = { PopupMenuTitleText(stringResource(R.string.sort_context_menu_sort_by_title)) },
    item(
        content = { PopupMenuItemText(stringResource(R.string.sort_context_menu_sort_by_unfinished_first)) },
        value = SortType.UnfinishedFirst
    ),
    item(
        content = { PopupMenuItemText(stringResource(R.string.sort_context_menu_sort_by_finished_first)) },
        value = SortType.FinishedFirst
    )
)

// TODO: DEBUG
private val sortMenu = menu(
    page(
        title = { PopupMenuTitleText(stringResource(R.string.sort_context_menu_first_page_title)) },
        item(
            content = { PopupMenuItemText(stringResource(R.string.sort_popup_menu_sort_by_date)) },
            page = sortByDate
        ),
        item(
            content = { PopupMenuItemText(stringResource(R.string.sort_popup_menu_sort_by_type)) },
            page = sortByType
        ),
        item(
            content = { PopupMenuItemText(stringResource(R.string.sort_popup_menu_sort_by_finish_status)) },
            page = sortByFinishStatus
        )
    )
)

enum class SortType {
    LatestFirst, EarliestFirst,
    TestFirst, PracticeFirst,
    UnfinishedFirst, FinishedFirst,
}

private data class DisplayData(
    val exerciseData: ExerciseData,
    // as long as the target state of this visible state is set to false
    // the associated exercise data is bound to deletion
    val visibleState: MutableTransitionState<Boolean> = MutableTransitionState(true)
) {
    val isVisible: Boolean get() = visibleState.targetState == true
    val isDeleted: Boolean get() = visibleState.targetState == false
    fun startDeletion() { visibleState.targetState = false }
}

private val List<DisplayData>.lastVisibleIndex: Int
    get() {
        var currentIndex = lastIndex
        while (currentIndex >= 0) {
            if (this[currentIndex].isVisible) {
                return currentIndex
            } else {
                currentIndex--
            }
        }
        throw IllegalStateException()
    }