package com.donald.musictheoryapp.composables.activitycomposables.exerciseoverview

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.animation.Pulsating
import com.donald.musictheoryapp.composables.general.ToggleIndicator
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.moreColors

private val CircleSize = 24.dp
private val LineThickness = 8.dp
private val TitleTextSize = 18.sp
private val LatestTextSize = 18.sp
private val OnlyOneAttemptHintTextSize = 14.sp
private val HorizontalSpacing = 15.dp
private val EditIconSize = 26.dp
private val EditButtonEndPadding = 12.dp

@Preview(showBackground = true)
@Composable
private fun RedoBarPreview() = CustomTheme {
    RedoBar(
        stepCount = 4, currentStep = 0, onStepPressed = {}, onEditButtonPressed = {}, onDeletePressed = {},
        editMode = true,
        modifier = Modifier
            .width(300.dp)
            .height(80.dp)
    )
}

@Composable
fun RedoBar(
    stepCount: Int,
    currentStep: Int,
    editMode: Boolean,
    onStepPressed: (Int) -> Unit,
    onEditButtonPressed: () -> Unit,
    onDeletePressed: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    RedoHistoryLine(
        modifier = modifier.background(MaterialTheme.colors.surface),
        stepCount = stepCount,
        currentStep = currentStep,
        editMode = editMode,
        onStepPressed = onStepPressed,
        onEditPressed = onEditButtonPressed,
        onDeletePressed = onDeletePressed,
    )
}


@Composable
private fun StepRow(
    modifier: Modifier = Modifier,
    stepCount: Int,
    currentStep: Int,
    editMode: Boolean,
    onStepPressed: (Int) -> Unit,
    onDeletePressed: (Int) -> Unit
) {
    if (stepCount < 1) return
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (step in 0 until stepCount) {
            val circleState = when {
                editMode == true -> CircleState.Edit
                step < currentStep -> CircleState.Left
                step == currentStep -> CircleState.Selected
                else -> CircleState.Right
            }
            StepCircle(
                circleState,
                onClick = {
                    if (editMode) onDeletePressed(step)
                    else onStepPressed(step)
                }
            )
        }
    }
}

@Composable
private fun StepCircle(circleState: CircleState, onClick: () -> Unit) = Pulsating(
    playing = circleState == CircleState.Edit
) {
    val innerColor: Color
    val outerColor: Color
    when (circleState) {
        CircleState.Left -> {
            innerColor = MaterialTheme.colors.primary
            outerColor = MaterialTheme.colors.primary
        }
        CircleState.Selected -> {
            innerColor = MaterialTheme.colors.onPrimary
            outerColor = MaterialTheme.colors.primary
        }
        CircleState.Right -> {
            innerColor = MaterialTheme.moreColors.onDisabled
            outerColor = MaterialTheme.moreColors.disabled
        }
        CircleState.Edit -> {
            innerColor = MaterialTheme.colors.onPrimary
            outerColor = MaterialTheme.colors.primary
        }
    }
    Box(
        modifier = Modifier.size(CircleSize)
    ) {
        Canvas(modifier = Modifier
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false)
            )
            .fillMaxSize(),
            //.border(
            //    shape = CircleShape,
            //    width = circleSize / 4,
            //    color = outerColor
            //),,
            onDraw = {
                val radius = CircleSize.toPx() / 2
                drawCircle(color = outerColor, radius = radius)
                if (circleState != CircleState.Edit) {
                    drawCircle(color = innerColor, radius = radius / 2F, center = Offset(radius, radius))
                } else {
                    val diameter = radius * 2
                    val padding = radius / 1.7F
                    val thickness = radius / 4
                    drawLine(
                        color = innerColor,
                        start = Offset(0F + padding, 0F + padding),
                        end = Offset(diameter - padding, diameter - padding),
                        strokeWidth = thickness,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = innerColor,
                        start = Offset(diameter - padding, 0F + padding),
                        end = Offset(0F + padding, diameter - padding),
                        strokeWidth = thickness,
                        cap = StrokeCap.Round
                    )
                }
            }
        )
        /*
        if (circleState == CircleState.Edit || false) Image(
            painter = painterResource(R.drawable.ic_redo_bar_delete_button),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary),
            modifier = Modifier.fillMaxSize()
        )

         */
    }
}

@Composable
private fun LineRow(modifier: Modifier = Modifier, stepCount: Int, currentStep: Int) {
    if (stepCount < 2) return
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for(step in 1 until stepCount) {
            val state = when {
                step < currentStep -> CircleState.Left
                step == currentStep -> CircleState.Selected
                else -> CircleState.Right
            }
            Line(state, LineThickness)
        }
    }
}

@Composable
private fun RowScope.Line(endCircleState: CircleState, thickness: Dp) {
    val color = when (endCircleState) {
        CircleState.Left, CircleState.Selected -> MaterialTheme.colors.primary
        else -> MaterialTheme.moreColors.disabled
    }

    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        thickness = thickness,
        color = color
    )
}

@Composable
private fun RedoHistoryLine(
    modifier: Modifier = Modifier,
    stepCount: Int,
    currentStep: Int,
    editMode: Boolean,
    onStepPressed: (Int) -> Unit,
    onEditPressed: () -> Unit,
    onDeletePressed: (Int) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        TopBar(
            editMode = editMode,
            displayEditButton = stepCount > 1,
            onEditPressed = onEditPressed,
            modifier = Modifier
                .fillMaxWidth()
        )
        // line with circles
        if (stepCount > 1) Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = HorizontalSpacing, end = HorizontalSpacing)
                .weight(1F)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
                //.horizontalScroll(state = rememberScrollState())
                //.width(width)
            ) {
                LineRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(start = CircleSize / 2, end = CircleSize / 2),
                    stepCount = stepCount,
                    currentStep = currentStep
                )
                StepRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    stepCount = stepCount,
                    currentStep = currentStep,
                    editMode = editMode,
                    onStepPressed = onStepPressed,
                    onDeletePressed = onDeletePressed
                )
            }
            Text(
                text = stringResource(R.string.redo_bar_latest_attempt),
                modifier = Modifier.padding(start = HorizontalSpacing),
                fontSize = LatestTextSize,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colors.primary
            )
        } else {
            Box(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxSize()
                    .background(MaterialTheme.moreColors.disabled)
            ) {
                Text(
                    text = stringResource(R.string.redo_bar_only_one_attempt_hint),
                    fontSize = OnlyOneAttemptHintTextSize,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.moreColors.onDisabled,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(start = 16.dp, end = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    editMode: Boolean,
    displayEditButton: Boolean,
    onEditPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(MaterialTheme.colors.primary)
    ) {
        Text(
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.Center),
            text = stringResource(R.string.redo_history_bar_title),
            fontSize = TitleTextSize,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colors.onPrimary,
            textAlign = TextAlign.Center
        )
        if (displayEditButton) EditButton(
            pressed = editMode,
            onClick = onEditPressed,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = EditButtonEndPadding)
                .size(EditIconSize)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun EditButton(pressed: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { onClick() }
        )
    ) {
        Image(
            painter = painterResource(R.drawable.ic_edit),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary),
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize()
        )
        AnimatedVisibility(
            visible = pressed,
            enter = scaleIn(initialScale = 0.5F, animationSpec = tween(durationMillis = 30, easing = LinearEasing)),
            exit = scaleOut(targetScale = 0.5F, animationSpec = tween(durationMillis = 30, easing = LinearEasing))
        ) {
            ToggleIndicator(modifier = Modifier.fillMaxSize())
        }
    }
}

private enum class CircleState { Left, Selected, Right, Edit }