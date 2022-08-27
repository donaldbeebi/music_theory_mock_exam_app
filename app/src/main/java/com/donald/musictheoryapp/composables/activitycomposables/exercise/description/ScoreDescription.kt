package com.donald.musictheoryapp.composables.activitycomposables.exercise.description

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.customview.scoreview.ScoreView
import com.donald.musictheoryapp.music.musicxml.*

@Preview
@Composable
private fun ScoreDescriptionPreview() = CustomTheme {
    ScoreDescription(
        score = MockScore,
        modifier = Modifier.width(400.dp).wrapContentHeight()
    )
}

@Composable
fun ScoreDescription(
    score: Score,
    modifier: Modifier = Modifier
) {
    //var scoreWidth = 0
    //var enlarged by remember { mutableStateOf(false) }
    //var offsetX by remember { mutableStateOf(0) }
    val color = MaterialTheme.colors.onSurface
    //ZoomableBox(enlarged = enlarged) {
        AndroidView(
            factory = { context ->
                ScoreView(context, color = color).apply {
                    setScore(score = score)
                }
            },
            /*update = { view ->
                view.setScore(score)
            },*/
            modifier = modifier
                /*.onGloballyPositioned {
                    scoreWidth = it.size.width
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        if (enlarged) {
                            change.consumeAllChanges()
                            offsetX = (offsetX + dragAmount.x.toInt()).coerceIn((-scoreWidth / 2)..(scoreWidth / 2))
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            offsetX = scoreWidth / 2
                            enlarged = !enlarged
                        }
                    )
                }
                .graphicsLayer {
                    if (enlarged) {
                        scaleX *= 2
                        scaleY *= 2
                        translationX += offsetX
                    }
                }*/
        )
    //}
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