package com.donald.musictheoryapp.composables.activitycomposables.exercise

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.composables.theme.CustomTheme
import java.lang.Integer.max

@Preview
@Composable
private fun SectionBarPreview() = CustomTheme {
    SectionBar(modifier = Modifier.width(400.dp)) {
        Text("Text that is really fucking long long long long long long", maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("Small text")
    }
}

@Composable
fun SectionBar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) = Layout(
    content = content,
    modifier = modifier
) { measurables, constraints ->
    check(measurables.size in 1..2)
    val firstMeasurable = measurables[0]
    val secondMeasurable = measurables.getOrNull(1)

    val secondWidth = secondMeasurable?.maxIntrinsicWidth(constraints.maxHeight)
    val firstWidth = firstMeasurable.maxIntrinsicWidth(constraints.maxHeight).coerceAtMost(constraints.maxWidth - (secondWidth ?: 0))

    val layoutWidth = constraints.maxWidth
    val layoutHeight = max(
        firstMeasurable.minIntrinsicHeight(firstWidth),
        secondWidth?.let { secondMeasurable.minIntrinsicHeight(it) } ?: 0
    )

    val firstPlaceable = firstMeasurable.measure(
        Constraints(
            minWidth = firstWidth,
            maxWidth = firstWidth,
            minHeight = layoutHeight,
            maxHeight = layoutHeight
        )
    )
    val secondPlaceable = secondWidth?.let {
        secondMeasurable.measure(
            Constraints(
                minWidth = secondWidth,
                maxWidth = secondWidth,
                minHeight = layoutHeight,
                maxHeight = layoutHeight
            )
        )
    }

    layout(
        width = layoutWidth,
        height = layoutHeight,
    ) {
        firstPlaceable.place(x = 0, y = 0)
        secondPlaceable?.place(x = layoutWidth - secondPlaceable.width, y = 0)
    }
}