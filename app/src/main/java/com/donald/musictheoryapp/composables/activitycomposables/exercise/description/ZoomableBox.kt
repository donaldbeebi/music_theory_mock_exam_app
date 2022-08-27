package com.donald.musictheoryapp.composables.activitycomposables.exercise.description

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.theme.CustomTheme

@Preview(showBackground = true)
@Composable
private fun ZoomableBoxPreview() = CustomTheme {
    var debugString by remember { mutableStateOf("") }
    Column {
        Text(debugString)
        Box(modifier = Modifier.width(200.dp)) {
            ZoomableBox(
                enlarged = true,
                log = { debugString = it },
            ) {
                Image(painter = painterResource(id = R.drawable.test_transparent_image), contentDescription = null)
            }
        }
    }
}

@Composable
fun ZoomableBox(
    enlarged: Boolean,
    modifier: Modifier = Modifier,
    log: (String) -> Unit = {},
    content: @Composable () -> Unit
) = Layout(
    modifier = modifier,
    content = content
) { measurables, constraints ->
    check(measurables.size == 1)
    val measurable = measurables[0]
    val width = constraints.maxWidth
    val height = measurable.minIntrinsicHeight(width) * (if (enlarged) 2 else 1)
    val placeable = measurables[0].measure(
        Constraints(
            minWidth = width,
            maxWidth = width,
            minHeight = height,
            maxHeight = height
        )
    )
    layout(width = width, height = height) {
        placeable.place(x = 0, y = 0)
    }
}