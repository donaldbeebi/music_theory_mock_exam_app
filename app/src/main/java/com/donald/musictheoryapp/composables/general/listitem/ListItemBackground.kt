package com.donald.musictheoryapp.composables.general

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.donald.musictheoryapp.composables.theme.dimens
import com.donald.musictheoryapp.composables.theme.listItemDimens

@Deprecated("Don't use this")
@Composable
fun ListItemBackground(
    barColor: Color,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.background(MaterialTheme.colors.surface)) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(MaterialTheme.listItemDimens.colorBarWidth)
                .background(barColor)
                .align(Alignment.CenterStart)
        )
    }
}