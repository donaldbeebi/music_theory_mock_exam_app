package com.donald.musictheoryapp.composables

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.composables.decoration.BottomShadow
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.moreColors

@Composable
private fun Content(
    backgroundColor: Color,
    showsFocusBackdrop: Boolean,
    onBackDropTap: (() -> Unit)?,
    focusContent: (@Composable BoxScope.() -> Unit)?,
    content: @Composable (BoxScope.() -> Unit),
    modifier: Modifier = Modifier
) = Box(
    modifier = modifier.background(backgroundColor)
) {
    content()
    androidx.compose.animation.AnimatedVisibility(
        visible = showsFocusBackdrop,
        enter = fadeIn(tween(durationMillis = 600)),
        exit = fadeOut(tween(durationMillis = 300)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.moreColors.focusBackdrop)
                /*.pointerInteropFilter { motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    onDismissFocus?.invoke()
                }
                return@pointerInteropFilter true
            }*/
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onBackDropTap?.invoke() }
                )
        )
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        focusContent?.invoke(this)
    }
    BottomShadow(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .fillMaxWidth()
            .height(4.dp)
    )
}

@Composable
fun StandardScreen(
    backgroundColor: Color = MaterialTheme.colors.background,
    showsFocusBackdrop: Boolean = false,
    onBackDropTap: (() -> Unit)? = null,
    focusContent: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) = Content(
    backgroundColor = backgroundColor,
    showsFocusBackdrop = showsFocusBackdrop,
    onBackDropTap = onBackDropTap,
    focusContent = focusContent,
    content = content,
    modifier = Modifier.fillMaxSize()
)

@Composable
fun StandardScreen(
    backgroundColor: Color = MaterialTheme.colors.background,
    activityTitle: String,
    showsFocusBackdrop: Boolean = false,
    onBackPressed: (() -> Unit)? = null,
    onBackDropTap: (() -> Unit)? = null,
    appBarExtras: (@Composable () -> Unit)? = null,
    focusContent: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        StandardTopAppBar(
            currentActivityTitle = activityTitle,
            onBackPressed = onBackPressed,
            extras = appBarExtras
        )
        Content(
            backgroundColor = backgroundColor,
            showsFocusBackdrop = showsFocusBackdrop,
            onBackDropTap = onBackDropTap,
            focusContent = focusContent,
            content = content,
            modifier = Modifier.weight(1F)
        )
    }
}

@Preview
@Composable
private fun StandardScreenPreview() = CustomTheme {
    StandardScreen(
        activityTitle = "current",
        onBackPressed = {}
    ) {
        Text(text = """
            Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.
        """.trimIndent())
    }
}