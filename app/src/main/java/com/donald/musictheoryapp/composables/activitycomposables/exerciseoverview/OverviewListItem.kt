package com.donald.musictheoryapp.composables.activitycomposables.exerciseoverview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.decoration.BottomShadow
import com.donald.musictheoryapp.composables.decoration.TopShadow
import com.donald.musictheoryapp.composables.general.ColorBarListDivider
import com.donald.musictheoryapp.composables.general.listitem.ListItem
import com.donald.musictheoryapp.composables.general.listitem.ListItemButtonState
import com.donald.musictheoryapp.composables.general.listitem.ListItemTextLarge2Line
import com.donald.musictheoryapp.composables.theme.*
import com.donald.musictheoryapp.question.QuestionGroup
import com.donald.musictheoryapp.question.Section
import com.donald.musictheoryapp.question.SectionGroup as SectionGroup

@Preview
@Composable
private fun SectionOverviewItemPreview() = CustomTheme(darkTheme = false) {
    Column {
        for (i in 1..3) OverviewListItem(
            sectionGroup = SectionGroup(
                1,
                "section name",
                listOf(
                    Section(
                        number = 1,
                        questionGroups = List(2) { i -> QuestionGroup(i, "Question group ${i + 1}", emptyList(), emptyList()) }
                    )/*,
                    Secton(
                        number = 2,
                        questionGroups = List(2) { i -> QuestionGroup(i, "Question group ${i + 1}", emptyList(), emptyList()) }
                    )*/
                )
            ),
            state = OverviewListItemState.Enabled(false, {}, { _, _ -> Unit }),
            showsTopShadow = true
        )
    }
}

@Composable
fun OverviewListItem(
    sectionGroup: SectionGroup,
    state: OverviewListItemState,
    showsTopShadow: Boolean,
    modifier: Modifier = Modifier
) {
    val buttonContentColor: Color
    val color: Color
    when (state) {
        /*is OverviewListItemState.Deleting -> {
            color = MaterialTheme.colors.secondary
            buttonContentColor = MaterialTheme.colors.onSecondary
        }*/
        else -> {
            color = MaterialTheme.colors.primary
            buttonContentColor = MaterialTheme.colors.onPrimary
        }
    }

    Column(
        modifier = modifier
    ) {
        ListItem(
            color = color,
            buttonImagePainter = painterResource(
                when (state) {
                    //is OverviewListItemState.Deleting -> R.drawable.ic_delete
                    is OverviewListItemState.Enabled -> if (state.expanded) {
                        R.drawable.ic_expand_button_expanded
                    } else {
                        R.drawable.ic_expand_button_collapsed
                    }
                    else -> R.drawable.ic_expand_button_collapsed
                }
            ),
            buttonImageColor = buttonContentColor,
            buttonState = when (state) {
                is OverviewListItemState.Enabled -> ListItemButtonState.Enabled(state.onExpand)
                //is OverviewListItemState.Deleting -> ListItemButtonState.Enabled(state.onDelete)
                else -> ListItemButtonState.Disabled
            },
            modifier = modifier.height(IntrinsicSize.Min)
        ) {
            SectionBody(
                sectionGroup,
                modifier = Modifier.fillMaxWidth()
            )
        }
        AnimatedVisibility(
            visible = when (state) {
                is OverviewListItemState.Enabled -> state.expanded
                else -> false
            },
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Box {
                Column {
                    sectionGroup.forEachIndexed { sectionIndex, section ->
                        if (sectionGroup.sections.size > 1) SectionDivider(
                            sectionGroup.number,
                            section.number,
                            Modifier.fillMaxWidth()
                        )

                        section.forEachIndexed { questionGroupIndex, questionGroup ->
                            OverviewSubListItem(
                                questionGroup,
                                buttonState = when (state) {
                                    is OverviewListItemState.Enabled -> ListItemButtonState.Enabled(
                                        { state.onViewQuestion(sectionIndex, questionGroupIndex) }
                                    )
                                    else -> ListItemButtonState.Disabled
                                },
                                modifier = Modifier.height(IntrinsicSize.Min)
                            )
                            if (questionGroupIndex != section.questionGroups.lastIndex) ColorBarListDivider(
                                color = MaterialTheme.colors.primaryVariant
                            )
                        }
                    }
                }
                BottomShadow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .align(Alignment.TopCenter)
                )
                if (showsTopShadow) TopShadow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
private fun SectionBody(
    sectionGroup: SectionGroup,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.weight(1F)
        ) {
            Text(
                text = "Section ${sectionGroup.number}",
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.listItemTypography.medium,
                modifier = Modifier.fillMaxWidth()
            )
            ListItemTextLarge2Line(
                text = sectionGroup.name,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Text(
            text = "${sectionGroup.points}/${sectionGroup.maxPoints}",
            fontSize = 22.sp,
            color = MaterialTheme.colors.onSurface,
        )
    }
}

@Composable
private fun SectionDivider(
    sectionGroupNumber: Int,
    sectionNumber: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .height(IntrinsicSize.Max)
            .background(MaterialTheme.moreColors.thickDivider)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(MaterialTheme.listItemDimens.colorBarWidth)
                .background(MaterialTheme.colors.primaryVariant)
        )
        Text(
            text = stringResource(R.string.section_divider, sectionGroupNumber, sectionNumber),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.moreColors.onThickDivider,
            modifier = Modifier.weight(1F)
        )
    }
}

sealed class OverviewListItemState {
    class Enabled(
        val expanded: Boolean,
        val onExpand: () -> Unit,
        val onViewQuestion: (Int, Int) -> Unit
    ) : OverviewListItemState()
    /*class Deleting(
        val onDelete: () -> Unit
    ) : OverviewListItemState()*/
    @Deprecated("Might not even need this?")
    object Disabled : OverviewListItemState()
}