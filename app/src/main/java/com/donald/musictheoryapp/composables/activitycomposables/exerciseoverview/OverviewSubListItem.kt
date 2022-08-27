package com.donald.musictheoryapp.composables.activitycomposables.exerciseoverview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.general.listitem.ListItem
import com.donald.musictheoryapp.composables.general.listitem.ListItemButtonState
import com.donald.musictheoryapp.composables.general.listitem.ListItemTextMedium2Lines
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.listItemTypography
import com.donald.musictheoryapp.question.QuestionGroup

@Preview
@Composable
private fun QuestionGroupOverviewItemPreview() = CustomTheme(darkTheme = false) {
    OverviewSubListItem(
        questionGroup = QuestionGroup(1, "group test name that is really fucking long", emptyList(), emptyList()),
        //onViewQuestion = {},
        buttonState = ListItemButtonState.Disabled,
        modifier = Modifier.height(IntrinsicSize.Min)
    )
}

@Composable
fun OverviewSubListItem(
    questionGroup: QuestionGroup,
    //onViewQuestion: () -> Unit,
    buttonState: ListItemButtonState,
    modifier: Modifier = Modifier
) = ListItem(
    color = MaterialTheme.colors.primaryVariant,
    buttonImagePainter = painterResource(R.drawable.ic_next_button),
    buttonImageColor = MaterialTheme.colors.onPrimary,
    //buttonOnClick = onViewQuestion,
    buttonState = buttonState,
    modifier = modifier
) {
    GroupBody(
        questionGroup,
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Composable
private fun GroupBody(
    questionGroup: QuestionGroup,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        ListItemTextMedium2Lines(
            text = questionGroup.name,
            modifier = Modifier
                .weight(1F)
                .fillMaxWidth()
                .padding(end = 16.dp)
        )
        Text(
            text = "${questionGroup.points}/${questionGroup.maxPoints}",
            style = MaterialTheme.listItemTypography.medium,
            color = MaterialTheme.colors.onSurface,
        )
    }
}

