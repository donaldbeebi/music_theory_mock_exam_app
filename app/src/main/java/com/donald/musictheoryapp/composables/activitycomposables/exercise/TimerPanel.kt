package com.donald.musictheoryapp.composables.activitycomposables.exercise.input

import android.os.CountDownTimer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.general.ClickableIcon
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.exerciseTypography
import com.donald.musictheoryapp.util.Time
import com.donald.musictheoryapp.util.Time.Companion.min
import com.donald.musictheoryapp.util.Time.Companion.ms
import com.donald.musictheoryapp.util.toggle

@Preview
@Composable
private fun TimerPanelPreview() = CustomTheme {
    var timeRemaining by remember { mutableStateOf(72.min) }
    remember {
        object : CountDownTimer(timeRemaining.millis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished.ms
            }
            override fun onFinish() {

            }
        }.start()
    }
    var paused by remember { mutableStateOf(false) }
    TimerPanel(
        timerState = TimerState.Counting(timeRemaining = 72.min, onPause = { Unit }),
        modifier = Modifier.height(IntrinsicSize.Min).wrapContentWidth()
            .background(MaterialTheme.colors.surface)
    )
}

@Composable
fun TimerPanel(
    timerState: TimerState,
    modifier: Modifier = Modifier
) {
    val time = timerState.timeRemaining
    val onClick: () -> Unit
    val paused: Boolean
    when (timerState) {
        is TimerState.Counting -> {
            paused = false
            onClick = timerState.onPause
        }
        is TimerState.Paused -> {
            paused = true
            onClick = timerState.onResume
        }
        else -> throw IllegalStateException("Kotlin being fucking dumb")
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(bounded = true),
            onClick = onClick
        )
    ) {
        Text(
            text = stringResource(R.string.time_remaining, time.toString()),
            style = MaterialTheme.exerciseTypography.bottomBar,
            color = MaterialTheme.colors.onSurface
        )
        Image(
            painter = painterResource(
                if (paused) R.drawable.ic_baseline_play_arrow
                else R.drawable.ic_baseline_pause
            ),
            colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
            contentDescription = null,
            modifier = Modifier.fillMaxHeight().aspectRatio(1F)
        )
    }
}

sealed class TimerState(val timeRemaining: Time) {
    class Counting(
        timeRemaining: Time,
        val onPause: () -> Unit
    ) : TimerState(timeRemaining)
    class Paused(
        timeRemaining: Time,
        val onResume: () -> Unit
    ) : TimerState(timeRemaining)
}