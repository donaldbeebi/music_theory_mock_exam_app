package com.donald.musictheoryapp.composables.general

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import com.donald.musictheoryapp.composables.theme.appBarDimens
import com.donald.musictheoryapp.composables.theme.listItemDimens

@Composable
fun AppBarButton(
    painter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageSizeFraction: Float = 0.7F
) = ClickableIcon(
    painter = painter,
    color = MaterialTheme.colors.primary,
    imageSizeFraction = imageSizeFraction,
    rippleRadius = MaterialTheme.appBarDimens.clickableRippleRadius,
    modifier = modifier
        .fillMaxHeight(0.8F)
        .aspectRatio(1F),
    onClick = onClick
)