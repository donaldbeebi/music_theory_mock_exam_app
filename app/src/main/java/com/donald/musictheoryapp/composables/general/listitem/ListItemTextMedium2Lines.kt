package com.donald.musictheoryapp.composables.general.listitem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.donald.musictheoryapp.composables.theme.listItemTypography

@Preview
@Composable
private fun Preview() = ListItemTextMedium2Lines(
    text = "testing this really long name what if this happens",
    modifier = Modifier.width(100.dp)
)

@Composable
fun ListItemTextMedium2Lines(
    text: String,
    modifier: Modifier = Modifier
) = Box(
    modifier = modifier.height(with(LocalDensity.current) { 45.sp.toDp() })
) {
    Text(
        text = text,
        color = MaterialTheme.colors.onSurface,
        style = MaterialTheme.listItemTypography.medium,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.align(Alignment.CenterStart)
    )
}