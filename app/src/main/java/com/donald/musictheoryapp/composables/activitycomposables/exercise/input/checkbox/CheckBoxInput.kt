package com.donald.musictheoryapp.composables.activitycomposables.exercise.input.checkbox

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.exerciseDimens

@Preview
@Composable
private fun CheckBoxInputPreview() = CustomTheme {
    Box(
        Modifier.background(MaterialTheme.colors.background)
    ) {
        val answers = remember { mutableStateListOf(true, false, null) }
        CheckBoxInput(
            answers = answers,
            checkBoxInputState = CheckBoxInputState.InputMode(
                onInput = { answer, answerIndex -> answers[answerIndex] = answer },
            ),
            modifier = Modifier.width(200.dp)
        )
    }
}

@Composable
fun CheckBoxInput(
    answers: List<Boolean?>,
    checkBoxInputState: CheckBoxInputState,
    //onInput: (Boolean?, Int) -> Unit,
    modifier: Modifier = Modifier
) = Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier
) {
    answers.forEachIndexed { answerIndex, answer ->
        Surface(
            color = Color.Transparent,
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(MaterialTheme.exerciseDimens.checkBoxBorderThickness, MaterialTheme.colors.onSurface),
            modifier = Modifier
                .weight(1F)
                .aspectRatio(1F)
                .let { modifier ->
                    if (checkBoxInputState is CheckBoxInputState.InputMode) modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            when (answer) {
                                true -> checkBoxInputState.onInput(false, answerIndex)
                                false -> checkBoxInputState.onInput(null, answerIndex)
                                null -> checkBoxInputState.onInput(true, answerIndex)
                            }
                        }
                    )
                    else modifier
                }
        ) {
            when (answer) {
                true -> Image(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
                    modifier = Modifier.fillMaxSize()
                )
                false -> Image(
                    painter = painterResource(R.drawable.ic_cross),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
                    modifier = Modifier.fillMaxSize()
                )
                null -> {}
            }
        }
    }
}

sealed class CheckBoxInputState {
    object ReadMode : CheckBoxInputState()
    class InputMode(
        val onInput: (Boolean?, Int) -> Unit
    ) : CheckBoxInputState()
}