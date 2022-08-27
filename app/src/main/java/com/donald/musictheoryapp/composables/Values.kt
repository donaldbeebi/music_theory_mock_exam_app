package com.donald.musictheoryapp.composables

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.composables.theme.dimens

@Deprecated("Use material theme")
val ElevationStep = 8.dp
@Composable
fun elevation(level: Int): Dp = MaterialTheme.dimens.elevationStep * level

@Deprecated("Use material theme")
val ColorBarWidth = 8.dp
@Deprecated("Use material theme")
val ListDividerWidth = 2.dp