package com.donald.musictheoryapp.composables.activitycomposables.exercise

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import com.donald.musictheoryapp.composables.theme.moreColors
import kotlin.math.roundToInt

@Preview(showBackground = true)
@Composable
private fun NavigationPreview() = CustomTheme {
    var sliderValue by remember { mutableStateOf(0) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(sliderValue.toString())
        Navigation(
            navigationState = NavigationState.Enabled(
                sliderValue = sliderValue,
                sliderRange = 0..0,
                onNavigate = {},
                //onEndReached = {}
            ),
            modifier = Modifier.width(500.dp).height(50.dp)
        )
    }
}

@Composable
fun Navigation(
    navigationState: NavigationState,
    modifier: Modifier = Modifier
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(18.dp),
    modifier = modifier
) {
    val sliderValue = navigationState.sliderValue
    val sliderRange = navigationState.sliderRange
    ArrowButton(
        painter = painterResource(R.drawable.ic_slider_bar_back_button),
        buttonState = when (navigationState) {
            is NavigationState.Disabled -> ButtonState.Disabled
            is NavigationState.Enabled -> {
                if (sliderValue == sliderRange.first) ButtonState.Disabled
                else ButtonState.Enabled(
                    highlighted = false,
                    onClick = { navigationState.onNavigate(sliderValue - 1) }
                )
            }
        },
        modifier = Modifier
            .fillMaxHeight(0.8F)
            .aspectRatio(1F)
    )
    NavigationSlider(
        sliderValue = sliderValue,
        sliderRange = sliderRange,
        navigationState = navigationState,
        //onValueChange = { fraction: Float -> onSlide(((sliderRange.span - 1) * fraction).roundToInt() + sliderRange.first) },
        modifier = Modifier.weight(1F)
    )
    ArrowButton(
        painter = painterResource(R.drawable.ic_slider_bar_next_button),
        buttonState = when (navigationState) {
            is NavigationState.Disabled -> ButtonState.Disabled
            is NavigationState.Enabled -> {
                val endReached = sliderValue == sliderRange.last
                ButtonState.Enabled(
                    highlighted = endReached,
                    onClick = /*if (endReached) {
                        { navigationState.onEndReached }
                    } else {*/
                        { navigationState.onNavigate(sliderValue + 1) }
                    //}
                )
            }
        },
        modifier = Modifier
            .fillMaxHeight(0.8F)
            .aspectRatio(1F)
    )
}

@Composable
private fun NavigationSlider(
    sliderValue: Int,
    sliderRange: IntRange,
    navigationState: NavigationState,
    modifier: Modifier = Modifier
) {
    val enabled: Boolean
    val onValueChange: (Float) -> Unit
    val currentSliderValue = remember { IntReference(null) } // a variable to avoid onValueChange called when no value is updated
    when (navigationState) {
        is NavigationState.Disabled -> {
            enabled = false
            onValueChange = { Unit }
        }
        is NavigationState.Enabled -> {
            if (navigationState.sliderRange.span == 1) {
                enabled = false
                onValueChange = { Unit }
            } else {
                enabled = true
                onValueChange = { fraction ->
                    val newSliderValue = ((sliderRange.span - 1) * fraction).roundToInt() + sliderRange.first
                    if (currentSliderValue.value != newSliderValue) {
                        navigationState.onNavigate(newSliderValue)
                        currentSliderValue.value = newSliderValue
                    }
                }
            }
        }
    }
    Slider(
        value = if (sliderRange.span > 1) {
            (sliderValue - sliderRange.first).toFloat() / (sliderRange.span - 1)
        } else {
            1F
        },
        enabled = enabled,
        onValueChange = onValueChange,
        modifier = modifier,
        colors = SliderDefaults.colors(
            disabledThumbColor = MaterialTheme.moreColors.disabled,
            disabledActiveTickColor = MaterialTheme.moreColors.disabled,
            disabledActiveTrackColor = MaterialTheme.moreColors.disabled,
            disabledInactiveTickColor = MaterialTheme.moreColors.disabled,
            disabledInactiveTrackColor = MaterialTheme.moreColors.disabled
        )
    )
}

@Composable
private fun ArrowButton(
    painter: Painter,
    buttonState: ButtonState,
    modifier: Modifier = Modifier
) {
    val enabled: Boolean
    val background: Color
    val arrowColor: Color
    val onClick: () -> Unit
    when (buttonState) {
        ButtonState.Disabled -> {
            enabled = false
            background = MaterialTheme.colors.primary
            arrowColor = MaterialTheme.moreColors.onDisabled
            onClick = { Unit }
        }
        is ButtonState.Enabled -> {
            enabled = true
            background = if (buttonState.highlighted) MaterialTheme.colors.secondary else MaterialTheme.colors.primary
            arrowColor = if (buttonState.highlighted) MaterialTheme.colors.onSecondary else MaterialTheme.colors.onPrimary
            onClick = buttonState.onClick
        }
    }
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = background,
            disabledBackgroundColor = MaterialTheme.moreColors.disabled
        )
    ) {
        Image(
            painter = painter,
            colorFilter = ColorFilter.tint(arrowColor),
            contentDescription = null
        )
    }
}

private val IntRange.span get() = last - first + 1

private sealed class ButtonState {
    object Disabled : ButtonState()
    class Enabled(val highlighted: Boolean, val onClick: () -> Unit) : ButtonState()
}

// TODO: INCLUDE PAGE INDEX INTO NAVIGATION STATE
sealed class NavigationState(
    val sliderValue: Int,
    val sliderRange: IntRange
) {
    class Disabled(
        sliderValue: Int,
        sliderRange: IntRange
    ) : NavigationState(sliderValue, sliderRange)
    class Enabled(
        sliderValue: Int,
        sliderRange: IntRange,
        val onNavigate: (Int) -> Unit,
    ) : NavigationState(sliderValue, sliderRange)
}

private class IntReference(var value: Int?)