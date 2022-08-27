package com.donald.musictheoryapp.composables.decoration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val ShadowStartColor = Color(0x19000000)
private val ShadowEndColor = Color(0x00FFFFFF)

@Preview
@Composable
private fun ShadowPreview() = TopShadow(
    modifier = Modifier
        .fillMaxWidth()
        .height(4.dp)
)

@Composable
fun BottomShadow(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ShadowStartColor,
                        ShadowEndColor
                    )
                )
            )
    )
}

@Composable
fun TopShadow(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ShadowEndColor,
                        ShadowStartColor
                    )
                )
            )
    )
}