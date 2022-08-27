package com.donald.musictheoryapp.composables.activitycomposables.exercise.input.button

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.donald.musictheoryapp.customview.scoreview.ScoreView
import com.donald.musictheoryapp.music.musicxml.*

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
private fun ScoreQuestionPreview() = ScoreButton(
    score = MockScore,
    questionButtonState = QuestionButtonState.InputMode(
        pressed = false,
        selected = false,
        onDown = {},
        onUp = {}
    ),
    modifier = Modifier.width(70.dp).height(60.dp)
)

@Composable
fun ScoreButton(
    score: Score,
    questionButtonState: QuestionButtonState,
    modifier: Modifier = Modifier,
) = QuestionButtonTemplate(
    questionButtonState = questionButtonState,
    modifier = modifier,
) {
    val color = MaterialTheme.colors.onPrimary
    AndroidView(
        factory = { context ->
            ScoreView(context, color = color).apply {
                setScore(score)
            }
        },
        modifier = Modifier.padding(8.dp)
    )
}