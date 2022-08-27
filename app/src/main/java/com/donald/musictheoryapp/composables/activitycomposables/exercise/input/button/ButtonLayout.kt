package com.donald.musictheoryapp.composables.activitycomposables.exercise.input.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.donald.musictheoryapp.composables.theme.CustomTheme

@Preview(showBackground = true)
@Composable
private fun ButtonLayoutPreview() = CustomTheme {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var debugText by remember { mutableStateOf("DEBUG TEXT HERE") }
        Text(
            text = debugText,
            fontSize = 12.sp
        )
        ButtonLayout(
            spacing = 16.dp,
            modifier = Modifier.width(200.dp).wrapContentHeight(),
            columnCount = 2,
            rowCount = 2,
            debug = { debugText = it }
        ) {
            Text(
                text = "A",
                modifier = Modifier.background(Color.Red)
            )
            Text(
                text = "B",
                modifier = Modifier.background(Color.Blue)
            )
            Text(
                text = "C",
                modifier = Modifier.background(Color.Cyan)
            )
            /*QuestionButtonTemplate(
            pressed = false,
            selected = false,
            onDown = {},
            onUp = {},
        ) {
            Box {
                Text("Su")
            }
        }*/
            //Text("Button Three")
            //Text("Button 4 Plus Some Words")
            //Box(Modifier.background(Color.Red).size(80.dp))
            //Box(Modifier.background(Color.Blue).size(100.dp))
        }
    }
}

// assuming width is given and height is unbounded
@Composable
fun ButtonLayout(
    modifier: Modifier = Modifier,
    spacing: Dp = Dp.Unspecified,
    columnCount: Int,
    rowCount: Int,
    maxAspectRatio: Float = Float.MAX_VALUE,
    debug: (String) -> Unit = {},
    content: @Composable () -> Unit,
) = Layout(
    modifier = modifier,
    content = content
) { measurables, constraints ->
    require(measurables.size > 1)
    // the measurables are the children
    // the constraints are coming from the parent
    val parentWidth = constraints.maxWidth
    val spacingInPx = spacing.toPx().toInt()

    /*val (columnCount, rowCount, buttonWidth, buttonHeight) = calcArrangement(
        measurables, parentWidth, spacingInPx, maxAspectRatio, debug
    )*/

    /*val columnCount = measurables.size//2
    val rowCount = 1//(measurables.size + columnCount - 1) / columnCount*/

    val buttonWidth = (parentWidth - spacingInPx * (columnCount - 1)) / columnCount
    val buttonHeight = measurables.maxOf { it.minIntrinsicHeight(buttonWidth) }.coerceAtLeast((buttonWidth.toFloat() / maxAspectRatio).toInt())

    val placeables = measurables.map { measurable ->
        measurable.measure(
            constraints.copy(
                minWidth = buttonWidth,
                maxWidth = buttonWidth,
                minHeight = buttonHeight,
                maxHeight = buttonHeight
            )
        )
    }

    val layoutWidth = parentWidth
    val layoutHeight = buttonHeight * rowCount + spacingInPx * (rowCount - 1)

    return@Layout layout(width = layoutWidth, height = layoutHeight) {
        var currentY = 0
        var currentPlaceableIndex = 0
        for (rowIndex in 0 until rowCount) {
            var currentX = 0
            for (columnIndex in 0 until columnCount) {
                if (currentPlaceableIndex == placeables.size) break // last placeable reached, breaking the loop
                val placeable = placeables[currentPlaceableIndex]
                placeable.place(x = currentX, y = currentY)
                currentX += buttonWidth + spacingInPx
                currentPlaceableIndex++
            }
            currentY += buttonHeight + spacingInPx
        }
    }
}

/*
private fun calcArrangement(
    measurables: List<Measurable>,
    parentWidth: Int,
    spacingInPx: Int,
    maxAspectRatio: Float,
    debug: (String) -> Unit = {}
): Arrangement {
    // first, try single row first
    val singleRowButtonWidth = (parentWidth - spacingInPx * (measurables.size - 1)) / measurables.size
    val singleRowButtonHeight = measurables.maxOf { it.minIntrinsicHeight(singleRowButtonWidth) }
    val singleRowAspectRatio = singleRowButtonWidth / singleRowButtonHeight
    return if (singleRowAspectRatio.toFloat() in 0.5F..1.5F) {//> 1F) {
        debug("min: ${measurables[0].minIntrinsicHeight(singleRowButtonWidth)} max: ${measurables[0].maxIntrinsicHeight(singleRowButtonWidth)}")
        Arrangement(
            columnCount = measurables.size,
            rowCount = 1,
            buttonWidth = singleRowButtonWidth,
            buttonHeight = singleRowButtonHeight.coerceAtLeast((singleRowButtonWidth / maxAspectRatio).toInt())
        )
    } else {
        debug("yo")
        val columnCount = 2
        val buttonWidth = (parentWidth - spacingInPx) / 2
        Arrangement(
            columnCount = columnCount,
            rowCount = (measurables.size + 1) / 2,
            buttonWidth = buttonWidth,
            buttonHeight = measurables.maxOf { it.minIntrinsicHeight(buttonWidth) }.coerceAtLeast((buttonWidth / maxAspectRatio).toInt())
        )
    }
}

private data class Arrangement(
    val columnCount: Int,
    val rowCount: Int,
    val buttonWidth: Int,
    val buttonHeight: Int
)*/