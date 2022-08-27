package com.donald.musictheoryapp.composables.activitycomposables.exercisehistory

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
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.elevation
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.exerciseHistoryTypography

@Preview
@Composable
private fun Preview() = CustomTheme {
    PointsBar(
        points = 10,
        modifier = Modifier.height(IntrinsicSize.Min)
    )
}

@Composable
fun PointsBar(
    points: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        elevation = elevation(1),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.exercise_history_points_remaining),
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.exerciseHistoryTypography.points,
                modifier = Modifier
                    .fillMaxHeight()
                    .background(MaterialTheme.colors.primary)
                    .padding(vertical = 4.dp, horizontal = 16.dp)
            )
            Text(
                text = points.toString(),
                style = MaterialTheme.exerciseHistoryTypography.points,
                color = MaterialTheme.colors.primary,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 4.dp, horizontal = 16.dp)
            )
        }
    }
}