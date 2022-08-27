package com.donald.musictheoryapp.composables.activitycomposables.exercise.input.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.exerciseTypography

@Preview
@Composable
private fun TextButtonPreview() = CustomTheme {
    TextButton(
        text = "appoggiatura",
        questionButtonState = QuestionButtonState.InputMode(
            pressed = false,
            selected = false,
            onDown = {},
            onUp = {}
        ),
        modifier = Modifier.width(150.dp).height(100.dp)
    )
}

@Composable
fun TextButton(
    text: String,
    questionButtonState: QuestionButtonState,
    modifier: Modifier = Modifier,
) = QuestionButtonTemplate(
    questionButtonState,
    modifier,
) {
    Box {
        Text(
            text = text,
            style = MaterialTheme.exerciseTypography.textButton,
            color = MaterialTheme.colors.onPrimary,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.Center)
        )
    }
}
