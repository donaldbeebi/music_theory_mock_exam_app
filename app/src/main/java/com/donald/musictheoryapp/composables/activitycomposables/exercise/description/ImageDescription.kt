package com.donald.musictheoryapp.composables.activitycomposables.exercise.description

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned

@Composable
fun ImageDescription(
    painter: Painter,
    modifier: Modifier = Modifier
) {
    //var width = 0
    //val aspectRatio = painter.intrinsicSize.width / painter.intrinsicSize.height
    //var enlarged by remember { mutableStateOf(false) }
    //var offsetX by remember { mutableStateOf(0) }
    //ZoomableBox(enlarged = enlarged) {
        Image(
            painter = painter,
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
            modifier = modifier
                /*.onGloballyPositioned { width = it.size.width }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        if (enlarged) {
                            change.consumeAllChanges()
                            offsetX = (offsetX + dragAmount.x.toInt()).coerceIn((-width / 2)..(width / 2))
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            offsetX = width / 2
                            enlarged = !enlarged
                        }
                    )
                }
                .graphicsLayer {
                    if (enlarged) {
                        scaleX *= 2
                        scaleY *= 2
                        translationX += offsetX
                    }
                }
                .aspectRatio(
                    //if (enlarged) aspectRatio / 2
                    aspectRatio
                )*/
        )
    //}
}