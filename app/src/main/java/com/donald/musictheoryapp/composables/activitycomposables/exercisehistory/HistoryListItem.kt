package com.donald.musictheoryapp.composables.activitycomposables.exercisehistory

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.activitycomposables.exerciseoverview.MockExercise1
import com.donald.musictheoryapp.composables.general.ClickableIcon
import com.donald.musictheoryapp.composables.general.listitem.ListItem
import com.donald.musictheoryapp.composables.general.listitem.ListItemButtonState
import com.donald.musictheoryapp.composables.theme.dimens
import com.donald.musictheoryapp.util.ExerciseData
import com.donald.musictheoryapp.util.exerciseData
import com.donald.musictheoryapp.util.toggle
import java.text.DateFormat

//private val dateFormatter = SimpleDateFormat("dd/MM/yyyy")

private val HorizontalSpacing = 20.dp

@Preview
@Composable
private fun ListItemPreview() = CustomTheme {
    HistoryListItem(
        exerciseData = MockExercise1.exerciseData(),
        state = HistoryListItemState.Disabled,
        modifier = Modifier.width(500.dp)
    )
}

@Composable
fun HistoryListItem(
    exerciseData: ExerciseData,
    state: HistoryListItemState,
    modifier: Modifier = Modifier
) {
    val color: Color
    val buttonImageColor: Color
    val buttonPainter: Painter
    val buttonState: ListItemButtonState
    when (state) {
        is HistoryListItemState.Regular -> {
            color = MaterialTheme.colors.primary
            buttonImageColor = MaterialTheme.colors.onPrimary
            buttonPainter = painterResource(R.drawable.ic_next_button)
            buttonState = ListItemButtonState.Enabled(state.onNextButtonClicked)
        }
        is HistoryListItemState.Deleting -> {
            color = MaterialTheme.colors.secondary
            buttonImageColor = MaterialTheme.colors.onSecondary
            buttonPainter = painterResource(R.drawable.ic_delete)
            buttonState = ListItemButtonState.Enabled(state.onDelete)
        }
        HistoryListItemState.Disabled -> {
            color = MaterialTheme.colors.primary
            buttonImageColor = MaterialTheme.colors.onPrimary
            buttonPainter = painterResource(R.drawable.ic_next_button)
            buttonState = ListItemButtonState.Disabled
        }
    }
    ListItem(
        color = color,
        buttonImagePainter = buttonPainter,
        buttonImageColor = buttonImageColor,
        buttonState = buttonState,
        modifier = modifier
            .height(IntrinsicSize.Min)
            .background(MaterialTheme.colors.surface)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            //horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // 1. title + date
            Column(
                modifier = Modifier
                    .weight(1F)
            ) {
                Text(
                    text = stringResource(exerciseData.type.resId),
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = DateFormat.getDateInstance().format(exerciseData.date),
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 2. paused text
            if (!exerciseData.ended) Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.padding(end = HorizontalSpacing)
            ) {
                Text(
                    text = stringResource(R.string.paused),
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.onSecondary,
                    modifier = Modifier.padding(4.dp)
                )
            }

            /*// 3. delete button
            AnimatedVisibility(
                visible = editMode
            ) {
                ClickableIcon(
                    painter = painterResource(R.drawable.ic_delete),
                    color = MaterialTheme.colors.secondary,
                    onClick = onDelete,
                    modifier = Modifier
                        .size(52.dp)
                        .padding(end = HorizontalSpacing)
                )
            }*/
        }
    }
}

sealed class HistoryListItemState {
    class Regular(
        val onNextButtonClicked: () -> Unit,
    ) : HistoryListItemState()
    class Deleting(
        val onDelete: () -> Unit
    ) : HistoryListItemState()
    object Disabled : HistoryListItemState()
}