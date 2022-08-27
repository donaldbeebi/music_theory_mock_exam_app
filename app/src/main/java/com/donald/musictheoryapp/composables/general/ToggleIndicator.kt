package com.donald.musictheoryapp.composables.general

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val IndicatorColor = Color(0x30_00_00_00)

@Composable
fun ToggleIndicator(modifier: Modifier = Modifier) = Surface(
    shape = CircleShape,
    color = IndicatorColor,
    modifier = modifier,
    content = {}
)
