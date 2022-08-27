package com.donald.musictheoryapp.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.appBarDimens

@Preview
@Composable
private fun MyTopAppBarPreview() = CustomTheme {
    StandardTopAppBar(
        currentActivityTitle = "Main",
        onBackPressed = {},
        //icons = { Box(modifier = Modifier.size(40.dp).background(Color.Cyan)) }
    )
}

@Composable
fun StandardTopAppBar(
    currentActivityTitle: String,
    onBackPressed: (() -> Unit)?,
    modifier: Modifier = Modifier,
    extras: (@Composable () -> Unit)? = null
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (onBackPressed != null) BackButton(
                onBackPressed,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxHeight()
                    .width(50.dp)
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = currentActivityTitle,
                fontSize = 25.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colors.primary
            )
            if (extras != null) Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
            ) {
                extras()
            }
        }
    }
}

@Composable
private fun BackButton(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) = Box(
    modifier = modifier
) {
    Image(
        painter = painterResource(R.drawable.ic_back_button),
        colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = false,
                    radius = MaterialTheme.appBarDimens.clickableRippleRadius
                ),
                onClick = onBackPressed
            )
    )
    //Text(
    //    text = previousActivityTitle,
    //    fontSize = 16.sp,
    //    color = MaterialTheme.colors.primary
    //)
}