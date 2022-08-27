package com.donald.musictheoryapp.composables.activitycomposables.practiceoptions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.donald.musictheoryapp.composables.activitycomposables.practiceoptions.viewmodel.QuestionGroupOptionViewModel
import com.donald.musictheoryapp.composables.activitycomposables.practiceoptions.viewmodel.SectionOptionViewModel
import com.donald.musictheoryapp.composables.general.ColorBarListDivider
import com.donald.musictheoryapp.composables.theme.CustomTheme

@Preview
@Composable
private fun OptionColumnPreview() = CustomTheme {
    OptionColumn(
        options = List(5) { index ->
            SectionOptionViewModel(
                number = index + 1,
                identifier = "",
                name = "Section Name",
                questionGroupOptionViewModels = List(3) { i -> QuestionGroupOptionViewModel(number = i + 1, "", "Group Name", 1) },
            )
        },
        expandedIndexDelegate = remember { mutableStateOf(-1) },
        onPlus = { _, _ -> },
        onMinus = { _, _ -> }
    )
}

@Composable
fun OptionColumn(
    options: List<SectionOptionViewModel>,
    expandedIndexDelegate: MutableState<Int>,
    onPlus: (Int, Int) -> Unit,
    onMinus: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier.verticalScroll(rememberScrollState())
) {
    var expandedIndex by expandedIndexDelegate
    options.forEachIndexed { optionIndex, option ->
        val expanded = optionIndex == expandedIndex
        SectionListItem(
            option = option,
            expanded = expanded,
            onToggleExpand = {
                expandedIndex = if (expandedIndex == optionIndex) { -1 } else { optionIndex }
            },
            onPlus = { questionGroupIndex -> onPlus(optionIndex, questionGroupIndex) },
            onMinus = { questionGroupIndex -> onMinus(optionIndex, questionGroupIndex) }
        )
        AnimatedVisibility(visible = optionIndex != options.lastIndex && !expanded) {
            ColorBarListDivider(color = MaterialTheme.colors.primary)
        }
    }
}