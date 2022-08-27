package com.donald.musictheoryapp.composables.activitycomposables.exerciseoverview

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.theme.CustomTheme

@Preview(showBackground = true, widthDp = 60, heightDp = 60)
@Composable
private fun RedoButtonPreview() = CustomTheme {
    RedoButton(
        {},
        elevation = 4.dp,
        modifier = Modifier
            .height(64.dp)
            .width(64.dp)
            .padding(16.dp)
    )
}

@Composable
fun RedoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    elevation: Dp = 0.dp
) = Surface(
    elevation = elevation,
    shape = MaterialTheme.shapes.small,
    color = MaterialTheme.colors.secondary,
    modifier = modifier.clickable { onClick() }
) {
   Image(
       painter = painterResource(R.drawable.ic_redo_button),
       contentDescription = null,
       colorFilter = ColorFilter.tint(MaterialTheme.colors.onSecondary),
       modifier = Modifier.fillMaxSize().padding(4.dp)
   )
}
