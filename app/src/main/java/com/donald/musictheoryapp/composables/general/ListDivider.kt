package com.donald.musictheoryapp.composables.general

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.dimens
import com.donald.musictheoryapp.composables.theme.listItemDimens
import com.donald.musictheoryapp.composables.theme.moreColors

@Deprecated("")
@Composable
fun ListDivider(modifier: Modifier = Modifier) {
    Divider(
        color = MaterialTheme.moreColors.thinDivider,
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
    )
}

@Composable
fun ColorBarListDivider(
    color: Color,
    modifier: Modifier = Modifier,
    thickness: Dp = MaterialTheme.dimens.dividerThickness
) = Box(
    modifier = modifier
) {
    Divider(
        thickness = thickness,
        color = MaterialTheme.moreColors.thinDivider,
        modifier = Modifier.fillMaxWidth()
    )
    Divider(
        thickness = thickness,
        color = color,
        modifier = Modifier
            .width(MaterialTheme.listItemDimens.colorBarWidth)
    )
}

@Preview
@Composable
private fun ColorBarListDividerPreview() = CustomTheme {
    Box(
        modifier = Modifier.width(500.dp).height(40.dp)
    ) {
        ColorBarListDivider(
            color = MaterialTheme.colors.primary,
        )
    }
}