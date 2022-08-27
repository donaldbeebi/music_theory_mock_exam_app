package com.donald.musictheoryapp.composables.activitycomposables.exercise.input.interval

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.exerciseTypography
import com.donald.musictheoryapp.customview.scoreview.ScoreView
import com.donald.musictheoryapp.listener.PanelOnTouchListener
import com.donald.musictheoryapp.music.musicxml.*
import com.donald.musictheoryapp.question.IntervalInputQuestion

@Preview
@Composable
private fun IntervalInputPreview() = CustomTheme {
    IntervalInput(
        question = MockIntervalInputQuestion,
        intervalInputState = IntervalInputState.InputMode,
        modifier = Modifier.size(400.dp).background(MaterialTheme.colors.background)
    )
}

@Composable
fun IntervalInput(
    question: IntervalInputQuestion,
    intervalInputState: IntervalInputState,
    modifier: Modifier = Modifier
) {
    val onSurface = MaterialTheme.colors.onSurface
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (intervalInputState == IntervalInputState.InputMode) Text(
            text = stringResource(R.string.interval_input_tip),
            style = MaterialTheme.exerciseTypography.intervalInputTip,
            color = MaterialTheme.colors.onSurface
        )
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.surface,
            modifier = Modifier.fillMaxWidth(0.8F)
        ) {
            AndroidView(
                factory = when (intervalInputState) {
                    IntervalInputState.InputMode -> {
                        { context ->
                            ScoreView(context, color = onSurface).apply {
                                val score = question.score.copy()
                                val inputNote = question.answer.userAnswer ?: score.parts()[0].measures()[0].notes()[0].clone().also { question.answer.userAnswer = it }
                                score.parts()[0].measures()[0].notes().add(inputNote)
                                setScore(score)
                                inputMode = true
                                val listener = PanelOnTouchListener(this, score)
                                setOnTouchListener(listener)
                            }
                        }
                    }
                    IntervalInputState.ReadMode -> {
                        { context ->
                            ScoreView(context, color = onSurface).apply {
                                val score = question.score.copy()
                                val inputNote = question.answer.userAnswer ?: score.parts()[0].measures()[0].notes()[0].clone().also { question.answer.userAnswer = it }
                                score.parts()[0].measures()[0].notes().add(inputNote)
                                setScore(score)
                                inputMode = false
                            }
                        }
                    }
                    IntervalInputState.CorrectAnswerMode -> {
                        { context ->
                            ScoreView(context, color = onSurface).apply {
                                val score = question.score.copy()
                                val correctNote = question.answer.correctAnswer
                                score.parts()[0].measures()[0].notes().add(correctNote)
                                setScore(score)
                                inputMode = false
                            }
                        }
                    }
                }
            )
        }
    }
}

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
                    notes = ArrayList<Note>().apply {
                        add(
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
                        )

                    },
                    barline = null
                )
            )
        )
    )
)

private val MockIntervalInputQuestion = IntervalInputQuestion(
    number = 1,
    descriptions = emptyList(),
    inputHint = null,
    score = MockScore,
    requiredInterval = "",
    answer = IntervalInputQuestion.Answer(
        userAnswer = null,
        correctAnswer = Note(
            printObject = false,
            pitch = Pitch(step = Step.C, alter = 0, octave = 4),
            duration = 1,
            type = Type.Whole,
            accidental = null,
            chord = false,
            staff = 1,
            notations = null
        )
    )
)

enum class IntervalInputState {
    InputMode, ReadMode, CorrectAnswerMode
}