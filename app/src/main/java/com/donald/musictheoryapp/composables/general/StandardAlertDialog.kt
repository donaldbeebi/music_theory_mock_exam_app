package com.donald.musictheoryapp.composables.general

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.standardAlertDialogDimens
import com.donald.musictheoryapp.composables.theme.standardAlertDialogTypography

@Preview
@Composable
private fun StandardAlertDialogPreview() = CustomTheme {
    StandardAlertDialog(
        title = "Title",
        description = "Description",
        Pair("SAVE", {}),
        Pair("END", {}),
        Pair("OK", {}),
        onDismiss = {}
    )
}

@Composable
fun StandardAlertDialog(
    title: String,
    description: String,
    positiveText: String,
    negativeText: String,
    onPositive: () -> Unit,
    onNegative: () -> Unit,
    onDismiss: () -> Unit
) = StandardAlertDialog(
    title = title,
    description = description,
    Pair(positiveText, onPositive),
    Pair(negativeText, onNegative),
    onDismiss = onDismiss
)

@Composable
fun StandardAlertDialog(
    title: String,
    description: String,
    vararg buttons: Pair<String, () -> Unit>,
    onDismiss: () -> Unit
 ) = AlertDialog(
    backgroundColor = MaterialTheme.colors.surface,
    onDismissRequest = { onDismiss() },
    title = {
        Text(
            text = title,
            style = MaterialTheme.standardAlertDialogTypography.title,
            color = MaterialTheme.colors.onSurface
        )
    },
    text = {
        Text(
            text = description,
            style = MaterialTheme.standardAlertDialogTypography.description,
            color = MaterialTheme.colors.onSurface
        )
    },
    buttons = {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.End),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 24.dp)
        ) {
            for ((buttonText, buttonOnClick) in buttons) Text(
                text = buttonText,
                style = MaterialTheme.standardAlertDialogTypography.button,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = false,
                        radius = MaterialTheme.standardAlertDialogDimens.buttonRippleRadius
                    ),
                    onClick = buttonOnClick
                )
            )
        }
    }
)