package com.donald.musictheoryapp.composables.activitycomposables.practiceoptions

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.activitycomposables.practiceoptions.viewmodel.QuestionGroupOptionViewModel
import com.donald.musictheoryapp.composables.activitycomposables.practiceoptions.viewmodel.SectionOptionViewModel
import com.donald.musictheoryapp.composables.elevation
import com.donald.musictheoryapp.composables.general.ClickableIcon
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.moreShapes
import com.donald.musictheoryapp.util.practiceoptions.QuestionGroupOption
import com.donald.musictheoryapp.util.practiceoptions.SectionOption

private val FontSize = 16.sp

@Preview
@Composable
private fun ReviewPanelPreview() = CustomTheme {
    ReviewPanel(
        sectionOptionViewModels = MockOptions,
        bottomInnerPadding = 48.dp,
        onClose = {},
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
    )
}

@Composable
fun ReviewPanel(
    sectionOptionViewModels: List<SectionOptionViewModel>,
    bottomInnerPadding: Dp,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) = Surface(
    shape = MaterialTheme.moreShapes.receipt,
    elevation = elevation(1),
    modifier = modifier
) {
    Box(
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = bottomInnerPadding)
    ) {
        ClickableIcon(
            painter = painterResource(R.drawable.ic_close_button),
            color = MaterialTheme.colors.onSurface,
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(24.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            var totalCost = 0
            Text(
                text = stringResource(R.string.practice_options_custom_exercise_spec),
                fontSize = FontSize,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface
            )
            sectionOptionViewModels.forEach outerLoop@{ sectionOptionViewModel ->
                if (sectionOptionViewModel.questionGroupOptionViewModels.sumOf { it.count } == 0) return@outerLoop
                // 1. section number
                Text(
                    text = stringResource(R.string.practice_options_section_number, sectionOptionViewModel.number),
                    fontSize = FontSize,
                    color = MaterialTheme.colors.onSurface
                )
                // 2. question group
                sectionOptionViewModel.questionGroupOptionViewModels.forEach innerLoop@{ questionGroupOptionViewModel ->
                    if (questionGroupOptionViewModel.count == 0) return@innerLoop
                    val cost = questionGroupOptionViewModel.count * 5 // TODO: STANDARDIZE
                    QuestionGroupRow(questionGroupOptionViewModel)
                    totalCost += cost
                }
            }
            Divider(
                color = MaterialTheme.colors.onSurface,
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.practice_options_total_cost),
                    fontSize = FontSize,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.weight(1F)
                )
                Text(
                    text = stringResource(R.string.practice_options_point_cost, totalCost),
                    fontSize = FontSize,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}

@Composable
private fun QuestionGroupRow(questionGroupOptionViewModel: QuestionGroupOptionViewModel) = Row(
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
        .fillMaxWidth()
) {
    // a. name
    Text(
        text = stringResource(
            R.string.practice_options_question_group_name,
            questionGroupOptionViewModel.name
        ),
        fontSize = FontSize,
        color = MaterialTheme.colors.onSurface,
        modifier = Modifier
            .padding(start = 16.dp)
            .weight(1F)
    )
    // b. count
    Text(
        text = "x ${questionGroupOptionViewModel.count}",
        fontSize = FontSize,
        color = MaterialTheme.colors.onSurface,
        textAlign = TextAlign.Start,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.width(with(LocalDensity.current) { 26.sp.toDp() })
    )
    // c. cost
    val cost = questionGroupOptionViewModel.count * 5 // TODO: STANDARDIZE
    Text(
        text = stringResource(R.string.practice_options_point_cost, cost),
        fontSize = FontSize,
        textAlign = TextAlign.End,
        color = MaterialTheme.colors.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.width(with(LocalDensity.current) { 80.sp.toDp() })
    )
}

private val MockOptions = List(3) { sectionIndex ->
    SectionOptionViewModel(
        number = sectionIndex + 1,
        identifier = "",
        name = "",
        questionGroupOptionViewModels = List(2) { questionGroupIndex ->
            QuestionGroupOptionViewModel(
                number = questionGroupIndex + 1,
                identifier = "",
                name = "Really Long",
                count = 5//questionGroupIndex + 1
            )
        }
    )
}