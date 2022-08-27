package com.donald.musictheoryapp.composables.activitycomposables.exercise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.elevation
import com.donald.musictheoryapp.composables.general.ClickableIcon
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.exerciseTypography
import com.donald.musictheoryapp.composables.theme.moreColors
import com.donald.musictheoryapp.pagedexercise.Page
import com.donald.musictheoryapp.question.Description
import com.donald.musictheoryapp.question.MultipleChoiceQuestion
import com.donald.musictheoryapp.util.toggle

@Preview
@Composable
private fun QuestionInfoPanelPreview() = CustomTheme {
    var expanded by remember { mutableStateOf(true) }
    Box() {
        QuestionInfoPanel(
            page = MockPage,
            expanded = expanded,
            maxSectionPartWidth = 100.dp,
            onToggleExpand = { expanded = toggle(expanded) },
            modifier = Modifier.height(IntrinsicSize.Min).wrapContentWidth()
        )
    }
    /*Row(
        //horizontalArrangement = Arrangement.End,
        modifier = Modifier.height(IntrinsicSize.Min).width(200.dp).background(Color.White)
    ) {
        Text(
            text = "really fucking long text hey what's up",
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            Modifier
                .background(Color.Red)
                .fillMaxHeight()
                .width(50.dp)
                .weight(1F, fill = false)
        )
    }*/
}

@Composable
fun QuestionInfoPanel(
    page: Page,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier,
    maxSectionPartWidth: Dp = Dp.Infinity
) = Surface(
    shape = MaterialTheme.shapes.medium.copy(
        topEnd = ZeroCornerSize,
        bottomEnd = ZeroCornerSize
    ),
    elevation = elevation(level = 1),
    color = MaterialTheme.colors.surface,
    modifier = modifier
) {
    Box(modifier = Modifier.padding(start = 4.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)) {
        Row(
            modifier = Modifier
                .clickable(onClick = onToggleExpand)
                .height(IntrinsicSize.Min)
        ) {
            Image(
                painter = painterResource(
                    if (!expanded) R.drawable.ic_horizontal_expand_button_expanded
                    else R.drawable.ic_horizontal_expand_button_collapsed
                ),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(0.8F)
            )
            PanelText(
                text = "S 7",//page.sectionNumber,
                modifier = Modifier.padding(start = 8.dp)
            )
            AnimatedVisibility(
                visible = expanded,
                modifier = Modifier.widthIn(max = maxSectionPartWidth)
            ) {
                PanelText(
                    text = page.sectionString,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            PanelText(
                text = page.questionString,
                modifier = Modifier.padding(start = 8.dp)
            )
            ClickableIcon(
                painter = painterResource(R.drawable.ic_peek_button),
                color = MaterialTheme.moreColors.disabled,
                onClick = {},
                imageSizeFraction = 0.8F,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .fillMaxHeight()
                    .aspectRatio(1F)
            )
        }
    }
}

@Composable
private fun PanelText(
    text: String,
    modifier: Modifier = Modifier,
) = Text(
    text = text,
    style = MaterialTheme.exerciseTypography.questionInfoPanel,
    color = MaterialTheme.colors.onSurface,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis,
    modifier = modifier
)

private val MockMultipleChoiceQuestion = MultipleChoiceQuestion(
    number = 1,
    descriptions = emptyList(),
    inputHint = null,
    options = listOf("Cat", "Dog", "Sheep", "Giraffe"),
    optionType = MultipleChoiceQuestion.OptionType.Text,
    answer = MultipleChoiceQuestion.Answer(userAnswer = null, correctAnswers = listOf(1))
)


private val MockPage = Page(
    sectionString = "Musical Instrument And Something Very Important",
    pageToPeekIndex = null,
    questionString = "Q 1.2",
    descriptions = listOf(
        Description(Description.Type.Text, "Mock description"),
        Description(Description.Type.Image, "test_score_image"),
        Description(Description.Type.TextEmphasize, "Mock description emphasize")
    ),
    question = MockMultipleChoiceQuestion,
    images = emptyList()
)