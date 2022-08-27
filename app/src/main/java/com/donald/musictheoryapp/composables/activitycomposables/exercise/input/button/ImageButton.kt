package com.donald.musictheoryapp.composables.activitycomposables.exercise.input.button

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.buttonInputColors
import com.donald.musictheoryapp.composables.theme.exerciseDimens
import com.donald.musictheoryapp.composables.theme.exerciseTypography

@Preview
@Composable
private fun ImageButtonPreview() = CustomTheme {
    ImageButton(
        painter = painterResource(id = R.drawable.test_time_signature),
        questionButtonState = QuestionButtonState.ReadMode(
            selected = false,
            number = 1,
        ),
        modifier = Modifier.size(100.dp)
    )
}

@Composable
fun ImageButton(
    painter: Painter,
    questionButtonState: QuestionButtonState,
    modifier: Modifier = Modifier,
) = QuestionButtonTemplate(
    questionButtonState = questionButtonState,
    modifier = modifier
) {
    Box {
        Image(
            painter = painter,
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary),
            modifier = Modifier
                .align(Alignment.Center)
                .aspectRatio(with(painter.intrinsicSize) { width / height })
                .padding(8.dp)
        )

        if (questionButtonState is QuestionButtonState.ReadMode) Box(
            modifier = Modifier.padding(MaterialTheme.exerciseDimens.imageButtonIndexLabelPadding).size(20.dp)
        ) {
            val circleColor = MaterialTheme.buttonInputColors.imageButtonIndexLabelBackground
            Canvas(
                modifier = Modifier.align(Alignment.Center).fillMaxSize(),
                onDraw = {
                    drawCircle(color = circleColor, center = size.center, radius = size.width / 2)
                }
            )
            Text(
                text = questionButtonState.number.toString(),
                color = MaterialTheme.buttonInputColors.imageButtonIndexLabelText,
                fontSize = with(LocalDensity.current) { 12.dp.toSp() },
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}