package com.donald.musictheoryapp.composables.activitycomposables.exercise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.activitycomposables.exercise.input.TimerPanel
import com.donald.musictheoryapp.composables.activitycomposables.exercise.input.TimerState
import com.donald.musictheoryapp.composables.general.ClickableIcon
import com.donald.musictheoryapp.composables.theme.exerciseTypography
import com.donald.musictheoryapp.composables.theme.moreColors
import com.donald.musictheoryapp.util.Time

@Composable
fun BottomBar(
    sectionString: String,
    questionString: String,
    timerState: TimerState?,
    navigationState: NavigationState,
    peekState: PeekState,
    modifier: Modifier = Modifier
) = Surface(
    color = MaterialTheme.colors.surface,
    modifier = modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp)
    ) {
        key(sectionString) {
            SectionBar(
                modifier = Modifier.height(IntrinsicSize.Min).fillMaxWidth(),
            ) {
                BottomBarText(text = sectionString)
                // TODO: FIX THIS SHIT; THIS BUTTON IS PUSHED OUT OF THE ROW WHEN THE SECTION TEXT IS TOO LONG, NEED CUSTOM LAYOUT
                AnimatedVisibility(visible = peekState != PeekState.Disabled) {//page.sectionPageIndex == null) {
                    val onClick = when (peekState) {
                        PeekState.Disabled -> {
                            { Unit }
                        }
                        is PeekState.Idle -> {
                            peekState.onPeek
                        }
                        is PeekState.Peeking -> {
                            peekState.onReturn
                        }
                    }
                    val iconColor = when (peekState) {
                        is PeekState.Peeking -> {
                            MaterialTheme.colors.primary
                        }
                        else -> {
                            MaterialTheme.moreColors.disabled
                        }
                    }
                    ClickableIcon(
                        painter = painterResource(R.drawable.ic_peek_button),
                        color = iconColor,
                        onClick = onClick,
                        imageSizeFraction = 1F,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .fillMaxHeight()
                            .aspectRatio(1F)
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            BottomBarText(text = questionString)
            if (timerState != null) TimerPanel(
                timerState = timerState,
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .wrapContentWidth()
            )
        }
        Navigation(
            navigationState = navigationState,
            modifier = Modifier
                .height(46.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun BottomBarText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null
) = Text(
    text = text,
    modifier = modifier,
    textAlign = textAlign,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis,
    style = MaterialTheme.exerciseTypography.bottomBar,
    color = MaterialTheme.colors.onSurface
)

sealed class PeekState {
    object Disabled : PeekState()
    class Idle(val onPeek: () -> Unit) : PeekState()
    class Peeking(val onReturn: () -> Unit) : PeekState()
}