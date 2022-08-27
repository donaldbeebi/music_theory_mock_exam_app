package com.donald.musictheoryapp.composables.activitycomposables.exercise.description

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.composables.elevation
import com.donald.musictheoryapp.composables.theme.exerciseDimens
import com.donald.musictheoryapp.composables.theme.exerciseTypography
import com.donald.musictheoryapp.composables.theme.moreShapes

@Composable
fun TextEmphasizeDescription(
    text: String,
    modifier: Modifier = Modifier
) = Surface(
    shape = MaterialTheme.moreShapes.textEmphasize,
    elevation = elevation(1),
    color = MaterialTheme.colors.secondary,
    modifier = modifier.padding(8.dp)
) {
    Text(
        text = text,
        style = MaterialTheme.exerciseTypography.textEmphasizeDescription,
        color = MaterialTheme.colors.onSecondary,
        modifier = Modifier.padding(MaterialTheme.exerciseDimens.textEmphasizeInnerPadding)
    )
}