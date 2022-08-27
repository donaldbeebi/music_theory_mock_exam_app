package com.donald.musictheoryapp.composables.activitycomposables.exerciseoverview

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.general.StandardAlertDialog
import com.donald.musictheoryapp.composables.theme.CustomTheme

@Preview
@Composable
private fun DeleteExerciseDialogPreview() = CustomTheme {
    DeleteExerciseDialog(
        DialogData(4, 2), {}, {}
    )
}

@Composable
fun DeleteExerciseDialog(
    dialogData: DialogData,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) = StandardAlertDialog(
    title = if (dialogData.exerciseDataIndex == dialogData.exerciseDataCount - 1) {
        stringResource(R.string.delete_exercise_dialog_title_latest_attempt)
    } else {
        stringResource(R.string.delete_exercise_dialog_title_nth_attempt, dialogData.exerciseDataIndex + 1)
    },
    description = stringResource(R.string.delete_exercise_dialog_description),
    positiveText = stringResource(R.string.ok),
    negativeText = stringResource(R.string.cancel),
    onPositive = onConfirm,
    onNegative = onCancel,
    onDismiss = onCancel
)

data class DialogData(val exerciseDataCount: Int, val exerciseDataIndex: Int)