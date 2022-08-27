package com.donald.musictheoryapp.composables.activitycomposables.exerciseoverview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.general.StandardAlertDialog
import com.donald.musictheoryapp.composables.theme.CustomTheme

@Preview
@Composable
private fun RedoExerciseDialog() = CustomTheme {
    Box(
        modifier = Modifier
            .size(500.dp)
            .background(MaterialTheme.colors.surface)
    ) {
        RedoExerciseDialog(
            {}, {}
        )
    }
}

@Composable
fun RedoExerciseDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) = StandardAlertDialog(
    title = stringResource(R.string.redo_exercise_dialog_title),
    description = stringResource(R.string.redo_exercise_dialog_description),
    positiveText = stringResource(R.string.ok),
    negativeText = stringResource(R.string.cancel),
    onPositive = onConfirm,
    onNegative = onCancel,
    onDismiss = onCancel
)