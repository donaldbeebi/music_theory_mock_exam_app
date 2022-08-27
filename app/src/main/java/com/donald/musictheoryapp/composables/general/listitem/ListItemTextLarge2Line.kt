package com.donald.musictheoryapp.composables.general.listitem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.donald.musictheoryapp.composables.theme.listItemTypography

@Composable
fun ListItemTextLarge2Line(
    text: String,
    modifier: Modifier = Modifier
) = Box(
    modifier = Modifier
        .height(with(LocalDensity.current) { 55.sp.toDp() })
) {
    Text(
        text = text,
        style = MaterialTheme.listItemTypography.large,
        color = MaterialTheme.colors.onSurface,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.align(Alignment.CenterStart)
    )
}