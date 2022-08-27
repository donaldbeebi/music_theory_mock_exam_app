package com.donald.musictheoryapp.composables.animation

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale

@Composable
fun Pulsating(playing: Boolean, pulseFraction: Float = 1.2f, content: @Composable () -> Unit) {
    if (playing) {
        val infiniteTransition = rememberInfiniteTransition()

        val scale = infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = pulseFraction,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse
            )
        )

        Box(modifier = Modifier.scale(scale.value)) {
            content()
        }
    } else {
        content()
    }
}