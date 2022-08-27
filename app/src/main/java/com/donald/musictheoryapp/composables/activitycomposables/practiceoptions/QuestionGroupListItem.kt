package com.donald.musictheoryapp.composables.activitycomposables.practiceoptions

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.activitycomposables.practiceoptions.viewmodel.QuestionGroupOptionViewModel
import com.donald.musictheoryapp.composables.general.listitem.ListItem
import com.donald.musictheoryapp.composables.general.listitem.ListItemTextMedium2Lines
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.listItemTypography
import com.donald.musictheoryapp.composables.theme.moreColors

@Preview
@Composable
private fun QuestionGroupListItemPreview() = CustomTheme {
    QuestionGroupListItem(
        option = QuestionGroupOptionViewModel(name = "Group Name", count = 1, identifier = "", number = 1),
        onPlus = null,//{},
        onMinus = {},
        modifier = Modifier
            .width(500.dp)
            .height(IntrinsicSize.Min)
    )
}

@Composable
fun QuestionGroupListItem(
    option: QuestionGroupOptionViewModel,
    onPlus: (() -> Unit)?,
    onMinus: (() -> Unit)?,
    modifier: Modifier = Modifier
) = ListItem(
    color = MaterialTheme.colors.primaryVariant,
    modifier = modifier
) {
    MainContent(
        option = option,
        onPlus = onPlus,
        onMinus = onMinus,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun MainContent(
    option: QuestionGroupOptionViewModel,
    onPlus: (() -> Unit)?,
    onMinus: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ListItemTextMedium2Lines(
            text = option.name,
            modifier = Modifier
                .weight(1F)
                .fillMaxWidth()
                .padding(end = 16.dp)
        )
        NumberButton(
            painter = painterResource(R.drawable.ic_minus_button),
            onClick = onMinus
        )
        /*ClickableIcon(
            painter = painterResource(R.drawable.ic_minus_button),
            color = MaterialTheme.colors.onSurface,
            onClick = onMinus,
            imageSizeFraction = 0.8F,
            modifier = Modifier.size(26.dp)
        )*/
        Text(
            text = option.count.toString(),
            style = MaterialTheme.listItemTypography.medium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.width(50.dp)
        )
        NumberButton(
            painter = painterResource(R.drawable.ic_plus_button),
            onClick = onPlus
        )
        /*ClickableIcon(
            painter = painterResource(R.drawable.ic_plus_button),
            color = MaterialTheme.colors.onSurface,
            onClick = onPlus,
            imageSizeFraction = 0.8F,
            modifier = Modifier.size(26.dp)
        )*/
    }
}

@Composable
private fun NumberButton(
    painter: Painter,
    onClick: (() -> Unit)?
) = Surface(
    shape = MaterialTheme.shapes.small,
    color = if (onClick != null) MaterialTheme.colors.primaryVariant
        else MaterialTheme.moreColors.disabled,
    modifier = run {
        val surfaceModifier = Modifier
            .size(26.dp)
        if (onClick != null) surfaceModifier.clickable(onClick = onClick)
        else surfaceModifier
    }
) {
    Image(
        painter = painter,
        contentDescription = null,
        colorFilter = ColorFilter.tint(
            if (onClick != null) MaterialTheme.colors.onPrimary
            else MaterialTheme.moreColors.onDisabled
        ),
        modifier = Modifier
            .padding(2.dp)
            .fillMaxSize()
    )
}