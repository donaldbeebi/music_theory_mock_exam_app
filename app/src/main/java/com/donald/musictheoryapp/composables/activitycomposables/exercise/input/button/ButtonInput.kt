package com.donald.musictheoryapp.composables.activitycomposables.exercise.input.button

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.activitycomposables.exercise.ImageProvider
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.music.musicxml.*

private val HSpacing = 8.dp
private val VSpacing = 8.dp

@Preview
@Composable
private fun ButtonInputPreview() = CustomTheme {
    val mockImage = painterResource(R.drawable.test_time_signature)
    val imageProvider = ImageProvider { mockImage }
    with(imageProvider) {
        ButtonInput(
            //options = listOf("Cat", "Dog", "Sheep", "Giraffe"),
            options = listOf("a_time_signature_12_4", "a_time_signature_5_4", "a_time_signature_5_8"),
            selectedIndex = null,
            //options = List(4) { MockScore.toDocument().asXML() },
            buttonType = ButtonType.Image,
            buttonInputState = ButtonInputState.InputMode(onSelect = {}),
            modifier = Modifier.wrapContentSize()
        )
    }
}

@Composable
fun ImageProvider.ButtonInput(
    options: List<String>,
    selectedIndex: Int?,
    buttonType: ButtonType,
    buttonInputState: ButtonInputState,
    modifier: Modifier = Modifier
) {
    val (columnCount, rowCount) = getInputLayout(options, buttonType)

    ButtonLayout(
        spacing = 16.dp,
        maxAspectRatio = 1.8F,
        columnCount = columnCount,
        rowCount = rowCount,
        modifier = modifier
    ) {
        when (buttonInputState) {
            is ButtonInputState.InputMode -> {
                val onSelect = buttonInputState.onSelect
                var state by remember { mutableStateOf(InteractionState(0, pressed = false)) }
                options.forEachIndexed { optionIndex, option ->
                    QuestionButton(
                        content = option,
                        buttonType = buttonType,
                        questionButtonState = QuestionButtonState.InputMode(
                            pressed = with(state) { optionIndex.isPressed },
                            selected = optionIndex == selectedIndex /*with(state) { optionIndex.isSelected }*/,
                            onDown = {
                                check(!state.pressed)
                                state = if (state.interactingIndex == optionIndex) {
                                    // down on the same button
                                    InteractionState(
                                        interactingIndex = state.interactingIndex,
                                        pressed = true,
                                        //selected = state.selected
                                    )
                                } else {
                                    // down on a different button
                                    InteractionState(
                                        interactingIndex = optionIndex,
                                        pressed = true,
                                        //selected = false
                                    ).also { onSelect(null) }
                                }
                            },
                            onUp = {
                                check(state.pressed)
                                state = if (state.interactingIndex == optionIndex) {
                                    // up on the same button
                                    InteractionState(
                                        interactingIndex = state.interactingIndex,
                                        pressed = false,
                                        //selected = toggle(state.selected)
                                    )//.also { onSelect(if (it.selected) it.interactingIndex else null) }
                                        .also { onSelect(if (selectedIndex == null) state.interactingIndex else null) }
                                } else {
                                    // up on a different button
                                    throw IllegalStateException()
                                }
                            }
                        ),
                    )
                }
            }
            ButtonInputState.ReadMode -> {
                options.forEachIndexed { optionIndex, option ->
                    QuestionButton(
                        content = option,
                        buttonType = buttonType,
                        questionButtonState = QuestionButtonState.ReadMode(
                            selected = optionIndex == selectedIndex,
                            number = optionIndex + 1
                        ),
                    )
                }
            }
        }
    }
}

private fun ImageProvider.getInputLayout(options: List<String>, buttonType: ButtonType): InputLayout {
    require(options.isNotEmpty())
    return when(buttonType) {
        ButtonType.Text -> when {
            options.size in 3..5 && options.all { it.length <= 5 } -> {
                InputLayout(
                    columnCount = options.size,
                    rowCount = 1,
                    //aspectRatio = 1F
                )
            }
            else -> {
                val columnCount = 2
                InputLayout(
                    columnCount = columnCount,
                    rowCount = (options.size + columnCount - 1) / columnCount,
                    //aspectRatio = 1.5F
                )
            }
        }

        ButtonType.Image -> {
            var widestAspectRatio = Float.MIN_VALUE
            for (imageName in options) {
                val painter = getImage(imageName)
                val thisAspectRatio = with(painter) { intrinsicSize.width / intrinsicSize.height }
                widestAspectRatio = widestAspectRatio.coerceAtLeast(thisAspectRatio)
            }
            if (widestAspectRatio <= 1F) {
                InputLayout(
                    columnCount = options.size,
                    rowCount = 1,
                    //aspectRatio = widestAspectRatio
                )
            } else {
                val columnCount = 2
                InputLayout(
                    columnCount = columnCount,
                    rowCount = (options.size + columnCount - 1) / columnCount,
                    //aspectRatio = widestAspectRatio
                )
            }
        }

        ButtonType.Score -> {
            val columnCount = 2
            InputLayout(
                columnCount = columnCount,
                rowCount = (options.size + columnCount - 1) / columnCount,
                //aspectRatio = 1.5F
            )
        }
    }
}

private data class InteractionState(val interactingIndex: Int, val pressed: Boolean/*,val selected: Boolean*/) {
    val Int.isPressed get() = interactingIndex == this && pressed
    //val Int.isSelected get() = interactingIndex == this && selected
}

private data class InputLayout(val columnCount: Int, val rowCount: Int)

enum class ButtonType { Text, Image, Score }

private val MockScore = Score(
    parts = arrayOf(
        Part(
            id = "",
            measures = arrayOf(
                Measure(
                    attributes = Attributes(
                        divisions = 1,
                        key = Key(fifths = 0, mode = Key.Mode.MAJOR),
                        time = Time(4, 4),
                        staves = 1,
                        clefs = arrayOf(Clef(sign = Sign.G, line = 2, printObject = true))
                    ),
                    notes = arrayListOf(
                        Note(
                            printObject = false,
                            pitch = Pitch(step = Step.C, alter = 0, octave = 4),
                            duration = 1,
                            type = Type.Whole,
                            accidental = null,
                            chord = false,
                            staff = 1,
                            notations = null,
                        )
                    ),
                    barline = null
                )
            )
        )
    )
)

sealed class ButtonInputState {
    class InputMode(
        val onSelect: (Int?) -> Unit
    ) : ButtonInputState()
    object ReadMode : ButtonInputState()
}