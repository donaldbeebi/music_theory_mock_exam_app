package com.donald.musictheoryapp.composables.general

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import com.donald.musictheoryapp.composables.theme.moreShapes
import com.donald.musictheoryapp.composables.theme.textInputColors
import com.donald.musictheoryapp.composables.theme.textInputTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StandardTextField(
    value: String,
    label: String,
    state: TextFieldState,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions()
) {
    val requester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    TextField(
        value = value,
        enabled = state != TextFieldState.Disabled,
        onValueChange = when (state) {
            is TextFieldState.Enabled -> {
                state.onValueChange
            }
            else -> fun(_: String) { Unit }
        },
        label = { Text(text = label, style = MaterialTheme.textInputTypography.label) },
        maxLines = 2,
        textStyle = MaterialTheme.textInputTypography.textField,
        shape = MaterialTheme.moreShapes.textField,
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.textInputColors.inputText,

            focusedLabelColor = MaterialTheme.colors.primary,
            unfocusedLabelColor = MaterialTheme.textInputColors.unfocused,

            focusedIndicatorColor = MaterialTheme.colors.primary,
            unfocusedIndicatorColor = MaterialTheme.textInputColors.unfocused,

            backgroundColor = MaterialTheme.textInputColors.background
        ),
        modifier = modifier
            .bringIntoViewRequester(requester)
            .onFocusChanged {
                 coroutineScope.launch {
                     delay(200) // weird workaround
                     requester.bringIntoView()
                 }
            },
        singleLine = true,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions
    )
}

sealed class TextFieldState {
    class Enabled(
        val onValueChange: (String) -> Unit
    ) : TextFieldState()
    object Disabled : TextFieldState()
}