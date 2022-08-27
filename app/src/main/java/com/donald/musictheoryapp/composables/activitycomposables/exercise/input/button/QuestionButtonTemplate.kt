package com.donald.musictheoryapp.composables.activitycomposables.exercise.input.button

import android.view.MotionEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.buttonInputColors
import com.donald.musictheoryapp.util.toggle

private val ButtonHeight = 16.dp
private val PressedHeight = ButtonHeight / 4
private val SelectedHeight = ButtonHeight / 2

@Preview
@Composable
private fun QuestionButtonPreview() = CustomTheme {
    var pressed by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(false) }
    QuestionButtonTemplate(
        questionButtonState = QuestionButtonState.InputMode(
            pressed = pressed,
            selected = selected,
            onDown = { pressed = true },
            onUp = { selected = toggle(selected) }
        ),
        modifier = Modifier.size(100.dp)
    ) { Text("Testing") }
}

@Composable
fun QuestionButtonTemplate(
    /*pressed: Boolean,
    selected: Boolean,
    onDown: () -> Unit,
    onUp: () -> Unit,*/
    questionButtonState: QuestionButtonState,
    modifier: Modifier = Modifier,
    //topFaceSize: DpSize? = null,
    content: @Composable () -> Unit
) {
    val (pressed, selected, thenModifier) = getStates(questionButtonState)
    Box(
        modifier = modifier.then(thenModifier)
    ) {
        val topPadding = ButtonHeight - when {
            pressed -> PressedHeight
            selected -> SelectedHeight
            else -> ButtonHeight
        }
        val bottomPadding = ButtonHeight - topPadding
        Surface(
            color = if (selected) {
                MaterialTheme.buttonInputColors.buttonFrontFaceSelected
            } else {
                MaterialTheme.buttonInputColors.buttonFrontFaceDefault
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding),
            content = { Unit }
        )
        Surface(
            color = if (selected) {
                MaterialTheme.buttonInputColors.buttonTopFaceSelected
            } else {
                MaterialTheme.buttonInputColors.buttonTopFaceDefault
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding, bottom = bottomPadding),
            content = content
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun getStates(questionButtonState: QuestionButtonState): Triple<Boolean, Boolean, Modifier> {
    return when (questionButtonState) {
        is QuestionButtonState.InputMode -> {
            val modifier = Modifier.pointerInteropFilter { event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> questionButtonState.onDown()
                    MotionEvent.ACTION_UP -> questionButtonState.onUp()
                    else -> {}
                }
                true
            }
            Triple(questionButtonState.pressed, questionButtonState.selected, modifier)
        }
        is QuestionButtonState.ReadMode -> {
            Triple(questionButtonState.selected, questionButtonState.selected, Modifier)
        }
    }
}