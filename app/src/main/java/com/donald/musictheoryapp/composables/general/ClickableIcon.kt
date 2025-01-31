package com.donald.musictheoryapp.composables.general

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.theme.CustomTheme

@Preview
@Composable
fun ClickableIconPreview() = CustomTheme {
    ClickableIcon(
        painter = painterResource(R.drawable.ic_more_button),
        color = MaterialTheme.colors.primary,
        imageSizeFraction = 0.8F,
        modifier = Modifier.size(60.dp),
        onClick = {}
    )
}

@Composable
fun ClickableIcon(
    painter: Painter,
    color: Color,
    modifier: Modifier = Modifier,
    imageSizeFraction: Float = 1F,
    rippleRadius: Dp = Dp.Unspecified,
    onClick: () -> Unit
) = Box(
    modifier = modifier.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(bounded = false, radius = rippleRadius),
        onClick = onClick
    )
) {
    Image(
        painter = painter,
        colorFilter = ColorFilter.tint(color),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(imageSizeFraction).align(Alignment.Center)
    )
}
