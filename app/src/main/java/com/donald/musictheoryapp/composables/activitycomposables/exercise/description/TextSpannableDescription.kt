package com.donald.musictheoryapp.composables.activitycomposables.exercise.description

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.activitycomposables.exercise.ImageProvider
import com.donald.musictheoryapp.composables.elevation
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.exerciseDimens
import com.donald.musictheoryapp.composables.theme.exerciseTypography
import com.donald.musictheoryapp.composables.theme.moreShapes
import com.donald.musictheoryapp.util.forEachArg

@Preview
@Composable
private fun TextSpannableDescriptionPreview() = CustomTheme {
    val mockImage = painterResource(R.drawable.test_score_image)
    val imageProvider = ImageProvider { mockImage }
    Box(Modifier.size(400.dp).background(MaterialTheme.colors.surface)) {
        with(imageProvider) {
            TextSpannableDescription(spannable = "{this}testing{image}{oy}")
        }
    }
}

@Composable
fun ImageProvider.TextSpannableDescription(
    spannable: String,
    modifier: Modifier = Modifier
) = Surface(
    shape = MaterialTheme.moreShapes.textEmphasize,
    elevation = elevation(1),
    color = MaterialTheme.colors.secondary,
    modifier = modifier.padding(8.dp)
) {
    val annotatedString = convertSpannableToAnnotated(spannable)
    val map = contentMapFromSpannable(spannable)
    Text(
        annotatedString,
        inlineContent = map,
        style = MaterialTheme.exerciseTypography.textEmphasizeDescription,
        color = MaterialTheme.colors.onSecondary,
        modifier = Modifier.padding(MaterialTheme.exerciseDimens.textEmphasizeInnerPadding)
    )
}

private fun convertSpannableToAnnotated(spannable: String): AnnotatedString {
    return buildAnnotatedString {
        var startIndex = 0
        spannable.forEachIndexed { currentIndex, char ->
            when {
                char == '{' -> {
                    append(spannable.substring(startIndex until currentIndex))
                    startIndex = currentIndex + 1
                }
                char == '}' -> {
                    appendInlineContent(spannable.substring(startIndex until currentIndex))
                    startIndex = currentIndex + 1
                }
                currentIndex == spannable.lastIndex -> {
                    append(spannable.substring(startIndex..currentIndex))
                }
            }
        }
    }
}

@Composable
private fun ImageProvider.contentMapFromSpannable(
    spannable: String
): Map<String, InlineTextContent> {
    val map = HashMap<String, InlineTextContent>()
    forEachImageName(spannable) { name ->
        val painter = getImage(name)
        val aspectRatio = with(painter) { intrinsicSize.width / intrinsicSize.height }
        val height = MaterialTheme.exerciseTypography.textDescription.fontSize * 1.8F
        val width = height * aspectRatio
        map[name] = InlineTextContent(
            Placeholder(width = width, height = height, PlaceholderVerticalAlign.TextCenter)
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSecondary),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
    return map
}

private inline fun forEachImageName(spannable: String, block: (String) -> Unit) = spannable.forEachArg(
    { _, _, string -> block(string) }
)