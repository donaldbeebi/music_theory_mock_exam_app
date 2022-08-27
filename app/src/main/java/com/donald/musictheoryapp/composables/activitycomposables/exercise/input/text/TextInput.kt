package com.donald.musictheoryapp.composables.activitycomposables.exercise.input.text

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.composables.general.StandardTextField
import com.donald.musictheoryapp.composables.general.TextFieldState
import com.donald.musictheoryapp.composables.theme.moreShapes
import com.donald.musictheoryapp.composables.theme.textInputTypography
import com.donald.musictheoryapp.composables.theme.textInputColors
import com.donald.musictheoryapp.question.TextInputQuestion
import com.donald.musictheoryapp.util.Quad

@Preview
@Composable
private fun TextInputPreview() = TextInput(
    label = "Label",
    text = "Input value",
    textInputState = TextInputState.InputMode(
        inputType = TextInputQuestion.InputType.Number,
        onInput = {},
        onDone = {}
    ),
    modifier = Modifier.size(200.dp)
)

@Composable
fun TextInput(
    label: String,
    text: String,
    /*inputType: TextInputQuestion.InputType,
    onInput: (String) -> Unit,
    onDone: KeyboardActionScope.() -> Unit,*/
    textInputState: TextInputState,
    modifier: Modifier = Modifier,
) {
    // CLEAN THIS UP WITH SEALED CLASS
    val (enabled, onValueChange, keyboardActions, keyboardOptions) = getStates(textInputState)
    StandardTextField(
        value = text,
        label = label,
        state = if (enabled) {
            TextFieldState.Enabled(onValueChange)
        } else {
            TextFieldState.Disabled
        },
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        modifier = modifier
    )
    /*TextField(
        value = text,
        enabled = enabled,
        onValueChange = onValueChange,
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
        modifier = modifier,
        singleLine = true,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions
    )*/
}

private fun getStates(textInputState: TextInputState): Quad<Boolean, (String) -> Unit, KeyboardActions, KeyboardOptions> {
    return when (textInputState) {
        TextInputState.ReadMode -> Quad(false, { Unit }, KeyboardActions(), KeyboardOptions.Default)
        is TextInputState.InputMode -> Quad(
            true,
            textInputState.onInput,
            KeyboardActions(onDone = textInputState.onDone),
            KeyboardOptions(
                keyboardType = if (textInputState.inputType == TextInputQuestion.InputType.Number) {
                    KeyboardType.Number
                } else {
                    KeyboardType.Text
                },
                imeAction = ImeAction.Done
            )
        )
    }
}

sealed class TextInputState {
    object ReadMode : TextInputState()
    class InputMode(
        val inputType: TextInputQuestion.InputType,
        val onInput: (String) -> Unit,
        val onDone: KeyboardActionScope.() -> Unit,
    ) : TextInputState()
}