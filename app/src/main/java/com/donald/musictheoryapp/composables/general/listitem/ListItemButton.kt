package com.donald.musictheoryapp.composables.general.listitem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.theme.CustomTheme

@Preview
@Composable
fun ListItemButtonPreview() = CustomTheme {
    ListItemButton(
        painter = painterResource(R.drawable.ic_expand_button_expanded),
        background = MaterialTheme.colors.primary,
        imageColor = MaterialTheme.colors.onPrimary,
        state = ListItemButtonState.Disabled,
        modifier = Modifier.height(60.dp)
    )
}

@Composable
fun ListItemButton(
    painter: Painter,
    background: Color,
    imageColor: Color,
    //onClick: () -> Unit,
    state: ListItemButtonState,
    modifier: Modifier = Modifier
) = Box(
    modifier = modifier
        .background(background)
        .fillMaxHeight()
        .width(60.dp)
        .then(
            when (state) {
                is ListItemButtonState.Enabled -> Modifier.clickable(onClick = state.onClick)
                ListItemButtonState.Disabled -> Modifier
            }
        )
) {
    Image(
        painter = painter,
        colorFilter = ColorFilter.tint(imageColor),
        contentDescription = null,
        modifier = Modifier
            .align(Alignment.Center)
            .padding(horizontal = 10.dp)
            .fillMaxSize()
    )
}

sealed class ListItemButtonState {
    class Enabled(
        val onClick: () -> Unit
    ) : ListItemButtonState()
    object Disabled : ListItemButtonState()
}