package com.donald.musictheoryapp.composables.activitycomposables.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.general.StandardTextField
import com.donald.musictheoryapp.composables.general.TextFieldState
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.moreShapes
import com.donald.musictheoryapp.composables.theme.standardAlertDialogTypography
import com.donald.musictheoryapp.util.Profile

@Preview
@Composable
private fun SignUpDialogPreview() = CustomTheme {
    Box {
        SignUpDialog(Profile.Type.Google, { _, _ -> Unit }, {})
    }
}

@Composable
fun BoxScope.SignUpDialog(
    profileType: Profile.Type,
    onConfirm: (String, Profile.LangPref) -> Unit,
    onDismiss: () -> Unit,
) {
    var nicknameValue by remember { mutableStateOf("") }
    Surface(
        color = MaterialTheme.colors.surface,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.align(Alignment.Center).padding(horizontal = 32.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.sign_up_dialog_title, stringResource(profileType.stringResource)),
                style = MaterialTheme.standardAlertDialogTypography.title,
                color = MaterialTheme.colors.onSurface,
            )
            StandardTextField(
                value = nicknameValue,
                label = stringResource(R.string.sign_up_dialog_nickname_text_field_hint),
                state = TextFieldState.Enabled { newValue -> nicknameValue = newValue },
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.End),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = stringResource(R.string.sign_up_dialog_confirm),
                    style = MaterialTheme.standardAlertDialogTypography.button,
                    color = MaterialTheme.colors.secondary,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false),
                        onClick = { onConfirm(nicknameValue, Profile.LangPref.English) }
                    )
                )
                Text(
                    text = stringResource(R.string.sign_up_dialog_cancel),
                    style = MaterialTheme.standardAlertDialogTypography.button,
                    color = MaterialTheme.colors.secondary,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false),
                        onClick = onDismiss
                    )
                )
            }
        }
    }
}