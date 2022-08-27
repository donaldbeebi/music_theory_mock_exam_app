package com.donald.musictheoryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.composables.StandardScreen
import com.donald.musictheoryapp.composables.general.StandardTextButton
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.mainDimens
import com.donald.musictheoryapp.util.*

const val DOUBLE_BACK_PRESS_DELAY_FOR_EXIT = 2000L

class MainActivity : AppCompatActivity() {
    private var backPressCountForExit = 0

    private fun onBeginTest() {
        runNetworkIO {
            if (!this.internetIsAvailable()) {
                runMain { this.displayToast(R.string.toast_no_internet) }
            } else {
                val intent = Intent(this, ExerciseActivity::class.java).apply {
                    putExtra("action", "download")
                    putExtra("mode", "test")
                }
                startActivity(intent)
            }
        }
    }

    private fun onPracticeMode() {
        runNetworkIO {
            if (!this.internetIsAvailable()) {
                runMain { this.displayToast(R.string.toast_no_internet) }
            } else {
                val intent = Intent(this, PracticeOptionsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun onHistory() {
        startActivity(Intent(this, ExerciseHistoryActivity::class.java))
    }

    private fun onProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CustomTheme {
                MainScreen(
                    onBeginTest = this::onBeginTest,
                    onPracticeMode = this::onPracticeMode,
                    onHistory = this::onHistory,
                    onProfile = this::onProfile,
                )
            }
        }
    }

    /*
     * *********
     * CALLBACKS
     * *********
     */

    override fun onBackPressed() {
        onExit()
    }

    private fun onExit() {
        if (backPressCountForExit == 0) {
            backPressCountForExit = 1
            Toast.makeText(this, R.string.toast_exit_confirmation, Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed(
                { backPressCountForExit = 0 },
                DOUBLE_BACK_PRESS_DELAY_FOR_EXIT
            )
        }
        else {
            backPressCountForExit = 0
            finishAffinity()
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() = CustomTheme {
    MainScreen(
        {},
        {},
        {},
        {}
    )
}

@Composable
private fun MainScreen(
    onBeginTest: () -> Unit,
    onPracticeMode: () -> Unit,
    onHistory: () -> Unit,
    onProfile: () -> Unit,
) = StandardScreen(
    activityTitle = stringResource(R.string.main_activity_title)
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        MainScreenButton(
            text = stringResource(R.string.button_begin_test),
            onClick = onBeginTest,
            modifier = Modifier
                .weight(1F)
                .fillMaxWidth()
        )
        MainScreenButton(
            text = stringResource(R.string.button_practice_mode),
            onClick = onPracticeMode,
            modifier = Modifier
                .weight(1F)
                .fillMaxWidth()
        )
        MainScreenButton(
            text = stringResource(R.string.button_history),
            onClick = onHistory,
            modifier = Modifier
                .weight(1F)
                .fillMaxWidth()
        )
        MainScreenButton(
            text = stringResource(R.string.button_profile),
            onClick = onProfile,
            modifier = Modifier
                .weight(1F)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun MainScreenButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = StandardTextButton(
    text = text,
    fontSize = MaterialTheme.mainDimens.buttonFontSize,
    shape = MaterialTheme.shapes.medium,
    onClick = onClick,
    modifier = modifier
)