package com.donald.musictheoryapp.composables.general

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
private fun CircularLoadingIndicatorPreview() = CircularLoadingIndicator("Loading...")

@Composable
fun CircularLoadingIndicator(text: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .height(60.dp)
                .width(60.dp)
                .padding(bottom = 16.dp)
        )
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colors.onSurface
        )
    }
}