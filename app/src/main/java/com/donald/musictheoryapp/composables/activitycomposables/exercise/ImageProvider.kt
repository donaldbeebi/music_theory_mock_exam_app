package com.donald.musictheoryapp.composables.activitycomposables.exercise

import androidx.compose.ui.graphics.painter.Painter

fun interface ImageProvider {
    fun getImage(imageName: String): Painter
}