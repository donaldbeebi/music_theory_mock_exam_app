package com.donald.musictheoryapp.composables.activitycomposables.practiceoptions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.activitycomposables.practiceoptions.viewmodel.QuestionGroupOptionViewModel
import com.donald.musictheoryapp.composables.activitycomposables.practiceoptions.viewmodel.SectionOptionViewModel
import com.donald.musictheoryapp.composables.decoration.BottomShadow
import com.donald.musictheoryapp.composables.decoration.TopShadow
import com.donald.musictheoryapp.composables.general.ColorBarListDivider
import com.donald.musictheoryapp.composables.general.listitem.ListItem
import com.donald.musictheoryapp.composables.general.listitem.ListItemButtonState
import com.donald.musictheoryapp.composables.general.listitem.ListItemTextLarge2Line
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.listItemTypography

private const val MIN_COUNT = 0
private const val MAX_COUNT = 5

@Preview
@Composable
private fun SectionListItemPreview() = CustomTheme {
    SectionListItem(
        option = SectionOptionViewModel(
            number = 1,
            identifier = "",
            name = "Section Name",
            questionGroupOptionViewModels = List(3) { i -> QuestionGroupOptionViewModel(number = i + 1, "", "Group Name", 1) }
        ),
        expanded = false,
        onToggleExpand = {},
        onPlus = {},
        onMinus = {},
        modifier = Modifier
            .width(400.dp)
            .height(IntrinsicSize.Min)
    )
}

@Composable
fun SectionListItem(
    option: SectionOptionViewModel,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onPlus: (Int) -> Unit,
    onMinus: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ListItem(
            color = MaterialTheme.colors.primary,
            buttonImagePainter = painterResource(
                if (expanded) R.drawable.ic_expand_button_expanded
                else R.drawable.ic_expand_button_collapsed
            ),
            buttonImageColor = MaterialTheme.colors.onPrimary,
            buttonState = ListItemButtonState.Enabled(onToggleExpand),
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            MainContent(option, Modifier.fillMaxSize())
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            QuestionGroupOptionColumn(
                options = option.questionGroupOptionViewModels,
                onPlus = onPlus,
                onMinus = onMinus,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun MainContent(
    option: SectionOptionViewModel,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier
) {
    Text(
        text = stringResource(R.string.practice_options_section_number, option.number),
        style = MaterialTheme.listItemTypography.medium,
        color = MaterialTheme.colors.onSurface
    )
    ListItemTextLarge2Line(
        text = option.name,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun QuestionGroupOptionColumn(
    options: List<QuestionGroupOptionViewModel>,
    onPlus: (Int) -> Unit,
    onMinus: (Int) -> Unit,
    modifier: Modifier = Modifier
) = Box(modifier = modifier) {
    Column {
        options.forEachIndexed { optionIndex, option ->
            QuestionGroupListItem(
                option = option,
                onPlus = if (option.count < MAX_COUNT) {
                    { onPlus(optionIndex) }
                } else {
                    null
                },
                onMinus = if (option.count > MIN_COUNT) {
                    { onMinus(optionIndex) }
                } else {
                    null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            )
            if (optionIndex != options.lastIndex) ColorBarListDivider(
                color = MaterialTheme.colors.primaryVariant
            )
        }
    }
    BottomShadow(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .align(Alignment.TopCenter)
    )
    TopShadow(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .align(Alignment.BottomCenter)
    )
}