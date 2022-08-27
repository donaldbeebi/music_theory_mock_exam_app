package com.donald.musictheoryapp.composables.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

val UnusedShapes = Shapes(
    small = RoundedCornerShape(
        topStartPercent = 40,
        topEndPercent = 20,
        bottomStartPercent = 20,
        bottomEndPercent = 40,
    ),//RoundedCornerShape(percent = 50),
    medium = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 8.dp,
        bottomStart = 8.dp,
        bottomEnd = 20.dp
    ),//RoundedCornerShape(18.dp),
    large = RoundedCornerShape(8.dp)
)

val Shapes = Shapes(
    small = RoundedCornerShape(percent = 40),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(8.dp)
)

object MoreShapes {
    val receipt = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
    val textEmphasize = RoundedCornerShape(8.dp)
    val textField = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
    val answerPanel = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
}