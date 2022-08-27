package com.donald.musictheoryapp.composables.activitycomposables.exercise.description

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.donald.musictheoryapp.composables.theme.exerciseTypography

@Composable
fun TextDescription(
    text: String,
    modifier: Modifier = Modifier
) = Text(
    text = text,
    style = MaterialTheme.exerciseTypography.textDescription,
    color = MaterialTheme.colors.onSurface,
    modifier = modifier
)