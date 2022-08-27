package com.donald.musictheoryapp.composables.activitycomposables.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.activitycomposables.exercise.description.*
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.music.musicxml.*
import com.donald.musictheoryapp.pagedexercise.Page
import com.donald.musictheoryapp.question.Description

@Preview
@Composable
private fun DescriptionPanelPreview() = CustomTheme {
    val mockImage1 = painterResource(R.drawable.test_time_signature)
    val mockImage2 = painterResource(R.drawable.test_score_image)
    val mockImage3 = painterResource(R.drawable.test_transparent_image)
    val imageProvider = ImageProvider { imageName ->
        when (imageName) {
            "2_2" -> mockImage1
            "test_score_image" -> mockImage2
            "test_transparent_image" -> mockImage3
            else -> throw IllegalStateException("Image name of \"$imageName\" not found")
        }
    }
    Box(Modifier.background(MaterialTheme.colors.surface)) {
        with(imageProvider) {
            DescriptionPanel(
                descriptions = listOf(
                    Description(Description.Type.Text, "Mock text description"),
                    Description(Description.Type.TextEmphasize, "Mock text emphasize description"),
                    Description(Description.Type.TextSpannable, "Mock text {2_2} description"),
                    Description(Description.Type.Image, "test_transparent_image"),
                    Description(Description.Type.Score, MockScore.toDocument().asXML())
                ),
                modifier = Modifier
                    .width(500.dp)
                    .height(400.dp)
            )
        }
    }
}

@Composable
fun ImageProvider.DescriptionPanel(
    descriptions: List<Description>,
    modifier: Modifier = Modifier
) = LazyColumn(
    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    modifier = modifier
) {
    //for (descriptionIndex in page.descriptions.indices) {
    items(descriptions.size/*, key = { page.descriptions[it].hashCode() }*/) { descriptionIndex ->
        val description = descriptions[descriptionIndex]
        when (description.type) {
            Description.Type.Text -> TextDescription(description.content)
            Description.Type.TextEmphasize -> Box(Modifier.fillMaxWidth()) {
                TextEmphasizeDescription(
                    text = description.content,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Description.Type.TextSpannable -> Box(Modifier.fillMaxWidth()) {
                TextSpannableDescription(
                    spannable = description.content,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Description.Type.Image -> ImageDescription(
                painter = getImage(description.content),
                modifier = Modifier.fillMaxWidth()
            )
            Description.Type.Score -> ScoreDescription(
                score = Score.fromXml(description.content),
                modifier = Modifier.fillMaxWidth()
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