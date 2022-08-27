package com.donald.musictheoryapp.composables.general

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.composables.elevation
import com.donald.musictheoryapp.composables.theme.moreColors
import com.donald.musictheoryapp.composables.theme.standardButtonDimens
import com.donald.musictheoryapp.composables.theme.standardButtonTypography

@Composable
private fun StandardButton(
    buttonState: ButtonState,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    content: @Composable BoxScope.(contentColor: Color) -> Unit
) {
    val backgroundColor: Color
    val contentColor: Color
    val surfaceThenModifier: Modifier
    when (buttonState) {
        ButtonState.Disabled -> {
            backgroundColor = MaterialTheme.moreColors.disabled
            contentColor = MaterialTheme.moreColors.onDisabled
            surfaceThenModifier = Modifier
        }
        is ButtonState.Enabled -> {
            if (buttonState.highlighted) {
                backgroundColor = MaterialTheme.colors.secondary
                contentColor = MaterialTheme.colors.onSecondary
            } else {
                backgroundColor = MaterialTheme.colors.primary
                contentColor = MaterialTheme.colors.onPrimary
            }
            surfaceThenModifier = Modifier.clickable(onClick = buttonState.onClick)
        }
    }
    Surface(
        color = backgroundColor,
        shape = shape,
        elevation = elevation(2),
        modifier = modifier
            .sizeIn(minHeight = 32.dp, minWidth = 48.dp)
            .then(surfaceThenModifier)
    ) {
        Box(
            modifier = Modifier.padding(MaterialTheme.standardButtonDimens.defaultPadding)
        ) {
            content(contentColor)
        }
    }
}

@Composable
fun StandardTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    fontSize: TextUnit = MaterialTheme.standardButtonDimens.defaultFontSize
) = StandardTextButton(
    text = text,
    state = ButtonState.Enabled(onClick = onClick),
    modifier = modifier,
    shape = shape,
    fontSize = fontSize
)

@Composable
fun StandardTextButton(
    text: String,
    state: ButtonState,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    fontSize: TextUnit = MaterialTheme.standardButtonDimens.defaultFontSize
) = StandardButton(
    buttonState = state,
    modifier = modifier,
    shape = shape
) { contentColor ->
    Text(
        text = text,
        fontSize = fontSize,
        style = MaterialTheme.standardButtonTypography.button,
        color = contentColor,
        modifier = Modifier.align(Alignment.Center)
    )
}

sealed class ButtonState {
    object Disabled : ButtonState()
    class Enabled(
        val onClick: () -> Unit,
        val highlighted: Boolean = false
    ) : ButtonState()
}