package com.donald.musictheoryapp.composables.activitycomposables.exercise.input.button

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.activitycomposables.exercise.ImageProvider
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.music.musicxml.*
import com.donald.musictheoryapp.util.toggle

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

@Preview
@Composable
private fun ButtonPreview() = CustomTheme {
    var pressed by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(false) }
    val mockImage = painterResource(R.drawable.test_time_signature)
    val imageProvider = object : ImageProvider {
        override fun getImage(imageName: String) = mockImage
    }
    with(imageProvider) {
        QuestionButton(
            content = MockScore.toDocument().asXML(),
            buttonType = ButtonType.Score,
            questionButtonState = QuestionButtonState.InputMode(
                pressed = pressed,
                selected = selected,
                onDown = { pressed = true },
                onUp = { selected = toggle(selected); pressed = false }
            )
            //modifier = Modifier
            //    .width(70.dp)
            //    .height(100.dp)
            //topFaceSize = DpSize(50.dp, 40.dp)
        )
    }
}

@Composable
fun ImageProvider.QuestionButton(
    content: String,
    buttonType: ButtonType,
    questionButtonState: QuestionButtonState,
    modifier: Modifier = Modifier,
) = when (buttonType) {
    ButtonType.Text -> TextButton(
        text = content,
        questionButtonState = questionButtonState,
        modifier = modifier,
    )
    ButtonType.Image -> ImageButton(
        painter = run { getImage(content) },
        questionButtonState = questionButtonState,
        modifier = modifier,
    )
    ButtonType.Score -> ScoreButton(
        score = Score.fromXml(content),
        questionButtonState = questionButtonState,
        modifier = modifier,
    )
}

sealed class QuestionButtonState {
    class ReadMode(
        val selected: Boolean,
        val number: Int
    ) : QuestionButtonState()
    class InputMode(
        val pressed: Boolean,
        val selected: Boolean,
        val onDown: () -> Unit,
        val onUp: () -> Unit
    ) : QuestionButtonState()
}