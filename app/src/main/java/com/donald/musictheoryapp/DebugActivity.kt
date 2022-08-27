package com.donald.musictheoryapp

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.tooling.preview.Preview
import com.donald.musictheoryapp.composables.theme.CustomTheme

class DebugActivity : ComponentActivity() {
    private var index by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val painters = listOf(
            List(2) { BitmapPainter(BitmapFactory.decodeResource(resources, R.drawable.red).asImageBitmap()) },
            List(4) { BitmapPainter(BitmapFactory.decodeResource(resources, R.drawable.blue).asImageBitmap()) }
        )
        setContent {
            CustomTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Column {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            for (painter in painters) {

                            }
                        }
                    }
                }
            }
        }
    }
}