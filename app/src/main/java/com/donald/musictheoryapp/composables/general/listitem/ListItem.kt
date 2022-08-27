package com.donald.musictheoryapp.composables.general.listitem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.listItemDimens

@Preview
@Composable
private fun ListItemPreview() = CustomTheme {
    ListItem(
        color = MaterialTheme.colors.primary,
        modifier = Modifier.width(400.dp).height(80.dp),
        buttonImagePainter = painterResource(R.drawable.ic_next_button),
        buttonImageColor = MaterialTheme.colors.onPrimary,
        buttonState = ListItemButtonState.Disabled,
    ) {
        Text("Testing")
    }
}

@Composable
fun ListItem(
    color: Color,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) = ListItem(
    color = color,
    modifier = modifier,
    buttonContent = null,
    content = content
)

@Composable
fun ListItem(
    color: Color,
    modifier: Modifier = Modifier,
    buttonImagePainter: Painter,
    buttonImageColor: Color,
    //buttonOnClick: () -> Unit,
    buttonState: ListItemButtonState,
    content: @Composable BoxScope.() -> Unit
) = ListItem(
    color = color,
    modifier = modifier,
    buttonContent = {
        ListItemButton(
            painter = buttonImagePainter,
            background = color,
            imageColor = buttonImageColor,
            state = buttonState
        )
    },
    content = content
)

@Composable
private fun ListItem(
    color: Color,
    modifier: Modifier = Modifier,
    buttonContent: (@Composable () -> Unit)?,
    content: @Composable BoxScope.() -> Unit
) = Row(
    modifier = modifier.background(MaterialTheme.colors.surface)
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(MaterialTheme.listItemDimens.colorBarWidth)
            .background(color)
    )
    Box(
        modifier = Modifier
            .padding(
                horizontal = MaterialTheme.listItemDimens.innerHPadding,
                vertical = MaterialTheme.listItemDimens.innerVPadding
            )
            .weight(1F)
            .fillMaxSize(),
        content = { content() }
    )
    buttonContent?.invoke()
}

/*sealed class ListItemState {
    class Enabled(
        val buttonOnClick: () -> Unit
    ) : ListItemState()
    object Disabled : ListItemState()
}*/