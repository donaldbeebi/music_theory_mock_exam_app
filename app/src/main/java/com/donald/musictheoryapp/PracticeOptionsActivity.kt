package com.donald.musictheoryapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.auth0.jwt.interfaces.DecodedJWT
import com.donald.musictheoryapp.composables.StandardScreen
import com.donald.musictheoryapp.composables.activitycomposables.exercisehistory.PointsBar
import com.donald.musictheoryapp.composables.activitycomposables.practiceoptions.OptionColumn
import com.donald.musictheoryapp.composables.activitycomposables.practiceoptions.ReviewPanel
import com.donald.musictheoryapp.composables.activitycomposables.practiceoptions.viewmodel.QuestionGroupOptionViewModel
import com.donald.musictheoryapp.composables.activitycomposables.practiceoptions.viewmodel.SectionOptionViewModel
import com.donald.musictheoryapp.composables.activitycomposables.practiceoptions.viewmodel.countCost
import com.donald.musictheoryapp.composables.activitycomposables.practiceoptions.viewmodel.toPracticeOptions
import com.donald.musictheoryapp.composables.decoration.TopShadow
import com.donald.musictheoryapp.composables.elevation
import com.donald.musictheoryapp.composables.general.StandardAlertDialog
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.exerciseHistoryTypography
import com.donald.musictheoryapp.composables.theme.moreColors
import com.donald.musictheoryapp.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.InputStreamReader

private val BottomButtonFontSize = 32.sp
private val BottomButtonPadding = 8.dp

class PracticeOptionsActivity : AppCompatActivity() {
    private var state: State by mutableStateOf(State.PracticeOptions)
    private lateinit var sectionOptionViewModels: List<SectionOptionViewModel>
    //private var showsReviewPanel by mutableStateOf(false)
    private lateinit var profile: Profile
    private lateinit var sharedPrefs: SharedPreferences

    private fun onConfirm() {
        state = State.ReviewPanel
        //showsReviewPanel = true
    }

    private fun onCloseReviewPanel() {
        state = State.PracticeOptions
        //showsReviewPanel = false
    }

    private fun onStartPractice() {
        runNetworkIO {
            val accessToken = TokenManager.tryGetAccessToken(this).otherwise {
                return@runNetworkIO runMain { handleTokenError(it) }
            }
            if (
                sectionOptionViewModels.all { sectionOptionViewModel ->
                    sectionOptionViewModel.questionGroupOptionViewModels.all { questionGroupOptionViewModel ->
                        questionGroupOptionViewModel.count == 0
                    }
                }
            ) {
                displayToastForZeroQuestionGroups(this)
            } else {
                confirmPractice(accessToken)
            }
        }
    }

    private fun onPlus(sectionIndex: Int, questionGroupIndex: Int) {
        sectionOptionViewModels[sectionIndex].questionGroupOptionViewModels[questionGroupIndex].count++
    }

    private fun onMinus(sectionIndex: Int, questionGroupIndex: Int) {
        val count = sectionOptionViewModels[sectionIndex].questionGroupOptionViewModels[questionGroupIndex].count
        if (count > 0) sectionOptionViewModels[sectionIndex].questionGroupOptionViewModels[questionGroupIndex].count--
    }

    private fun onHelpDialogRead() {
        sharedPrefs.edit().putBoolean("help_dialog_read", true).apply()
        state = State.PracticeOptions
    }

    private fun onHelpDialogRemindMeLater() {
        sharedPrefs.edit().putBoolean("help_dialog_read", false).apply()
        state = State.PracticeOptions
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // check if the help dialog is read
        val sharedPrefs = getSharedPreferences("practice_options_shared_prefs", MODE_PRIVATE)
        this.sharedPrefs = sharedPrefs
        with(sharedPrefs) {
            if (getBoolean("help_dialog_read", false) == false) {
                lifecycleScope.launch {
                    delay(1000)
                    state = State.HelpDialog(
                        this@PracticeOptionsActivity::onHelpDialogRead,
                        this@PracticeOptionsActivity::onHelpDialogRemindMeLater
                    )
                }
            }
        }

        // load profile
        val profile = CurrentProfile ?: run {
            displayToast(R.string.toast_no_profile_set)
            finish()
            return
        }
        this.profile = profile

        val options = loadMenu()
        this.sectionOptionViewModels = options

        setContent {
            CustomTheme {
                Screen(
                    points = profile.points,
                    options = options,
                    //showsReviewPanel = showsReviewPanel,
                    state = state,
                    onPlus = this::onPlus,
                    onMinus = this::onMinus,
                    onConfirm = this::onConfirm,
                    onStartPractice = this::onStartPractice,
                    onCloseReviewPanel = this::onCloseReviewPanel,
                    onBackPressed = { onBackPressed() }
                )
            }
        }
    }

    private fun loadMenu(): List<SectionOptionViewModel> {
        val menuJson = InputStreamReader(assets.open("question_menu.json")).use {
            JSONObject(it.readText())
        }

        val sectionJsonArray = menuJson.getJSONArray("sections")
        val sectionOptions = ArrayList<SectionOptionViewModel>(sectionJsonArray.length())

        for (sectionIndex in 0 until sectionJsonArray.length()) {
            val sectionJson = sectionJsonArray.getJSONObject(sectionIndex)
            val groupJsonArray = sectionJson.getJSONArray("groups")
            val groupOptions = ArrayList<QuestionGroupOptionViewModel>(groupJsonArray.length())
            // populating the groupOptions
            for (groupIndex in 0 until groupJsonArray.length()) {
                val groupJson = groupJsonArray.getJSONObject(groupIndex)
                val identifier = groupJson.getString("identifier")
                val groupName = getString(resources.getIdentifier("group_$identifier", "string", packageName))
                groupOptions += QuestionGroupOptionViewModel(
                    number = groupIndex + 1,
                    identifier = identifier,
                    name = groupName,
                    count = 0
                )
            }
            val identifier = sectionJson.getString("identifier")
            val sectionName = getString(resources.getIdentifier("section_$identifier", "string", packageName))
            sectionOptions += SectionOptionViewModel(
                number = sectionIndex + 1,
                identifier = identifier,
                name = sectionName,
                questionGroupOptionViewModels = groupOptions
            )
        }

        return sectionOptions
    }

    private fun confirmPractice(accessToken: DecodedJWT) = runMain {
        if (profile.points < sectionOptionViewModels.countCost(PRACTICE_COST) || false) { // TODO: POINTS DETECTION DISABLED
            displayToast(R.string.toast_insufficient_points)
            return@runMain
        }
        val intent = Intent(this, ExerciseActivity::class.java).apply {
            putExtra("action", "download")
            putExtra("mode", "practice")
            //putExtra("options_json", options.toString())
            putExtra("options", sectionOptionViewModels.toPracticeOptions())
            putExtra("access_token", accessToken.token)
        }
        startActivity(intent)
        finish()
    }

    private fun handleTokenError(tokenError: TokenError) {
        when (tokenError) {
            TokenError.BadResponse -> this.displayToast(R.string.toast_bad_response_from_server)
            TokenError.NoInternet -> this.displayToast(R.string.toast_no_internet)
            TokenError.Timeout -> this.displayToast(R.string.toast_timeout)
        }
    }

    override fun onBackPressed() {
        if (state == State.ReviewPanel) onCloseReviewPanel()
        else finish()
    }

    sealed class State {
        class HelpDialog(
            val onRead: () -> Unit,
            val onRemindMeLater: () -> Unit
        ) : State()
        object PracticeOptions : State()
        object ReviewPanel : State()
    }
}

@Preview
@Composable
private fun ScreenPreview() = CustomTheme {
    Screen(
        points = 0,
        options = MockOptions1,
        state = PracticeOptionsActivity.State.PracticeOptions,
        onPlus = { _, _ -> },
        onMinus = { _, _, -> },
        onConfirm = {},
        onStartPractice = {},
        onCloseReviewPanel = {},
        onBackPressed = {}
    )
}

@Composable
private fun Screen(
    points: Int,
    options: List<SectionOptionViewModel>,
    //showsReviewPanel: Boolean,
    state: PracticeOptionsActivity.State,
    onPlus: (Int, Int) -> Unit,
    onMinus: (Int, Int) -> Unit,
    onConfirm: () -> Unit,
    onStartPractice: () -> Unit,
    onCloseReviewPanel: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val expandedIndexDelegate = remember { mutableStateOf(-1) }

    StandardScreen(
        activityTitle = stringResource(R.string.practice_options_activity_title),
        onBackPressed = onBackPressed,
        focusContent = { FocusContent(state, options, onCloseReviewPanel, onStartPractice) },
        showsFocusBackdrop = state != PracticeOptionsActivity.State.PracticeOptions,
        onBackDropTap = onCloseReviewPanel
    ) {
        Column {
            Box(
                modifier = Modifier.weight(1F)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    if (false) PointsBar( // TODO: REMOVED
                        points = points,
                        modifier = Modifier.height(IntrinsicSize.Min)
                    )
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        elevation = elevation(1),
                    ) {
                        OptionColumn(
                            options = options,
                            expandedIndexDelegate = expandedIndexDelegate,
                            onPlus = onPlus,
                            onMinus = onMinus,
                        )
                    }
                }
                TopShadow(modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(4.dp)
                )
            }
            val onClick = if (
                options.any { sectionOption ->
                    sectionOption.questionGroupOptionViewModels.any { questionGroupOption -> questionGroupOption.count > 0 }
                }
            ) {
                { onConfirm(); expandedIndexDelegate.value = -1 }
            } else {
                null
            }
            BottomButton(
                text = stringResource(R.string.practice_options_confirm),
                onClick = onClick
            )
        }
    }
}

@Composable
private fun BoxScope.FocusContent(
    state: PracticeOptionsActivity.State,
    options: List<SectionOptionViewModel>,
    onClose: () -> Unit,
    onStart: () -> Unit
) {
    if (state is PracticeOptionsActivity.State.HelpDialog) {
        StandardAlertDialog(
            title = stringResource(R.string.practice_options_dialog_title),
            description = stringResource(R.string.practice_options_dialog_description, PRACTICE_COST),
            positiveText = stringResource(R.string.practice_options_dialog_ok),
            onPositive = state.onRead,
            negativeText = stringResource(R.string.practice_options_dialog_remind_me_later),
            onNegative = state.onRemindMeLater,
            onDismiss = state.onRemindMeLater
        )
    }
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .align(Alignment.BottomCenter)
    ) {
        AnimatedVisibility(
            visible = state == PracticeOptionsActivity.State.ReviewPanel,
            enter = slideInVertically(
                initialOffsetY = { height -> height },
                animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing) //spring(stiffness = Spring.StiffnessLow)
            ),
            exit = slideOutVertically(
                targetOffsetY = { height -> height },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing) //spring(stiffness = Spring.StiffnessLow)
            ),
            modifier = Modifier
                .fillMaxWidth()
                //.align(Alignment.BottomCenter)
        ) {
            ReviewPanel(
                sectionOptionViewModels = options,
                bottomInnerPadding = bottomInnerPadding(BottomButtonFontSize, BottomButtonPadding),
                onClose = onClose,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
            )
        }
    }
    AnimatedVisibility(
        visible = state == PracticeOptionsActivity.State.ReviewPanel,
        enter = slideInVertically(
            initialOffsetY = { height -> height },
            animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing)
        ),
        exit = slideOutVertically(
            targetOffsetY = { height -> height },
            animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            TopShadow(
                modifier = Modifier
                    .height(4.dp)
                    .fillMaxWidth()
            )
            BottomButton(
                text = stringResource(R.string.practice_options_start),
                onClick = onStart
            )
        }
    }
}

@Composable
private fun BottomButton(
    text: String,
    onClick: (() -> Unit)?
) {
    var boxModifier = Modifier
        .fillMaxWidth()
        .height(IntrinsicSize.Min)
        .background(
            if (onClick != null) MaterialTheme.colors.primary
            else MaterialTheme.moreColors.disabled
        )
    if (onClick != null) boxModifier = boxModifier.clickable(onClick = onClick)
    Box(
        modifier = boxModifier
    ) {
        Text(
            text = text,
            color = if (onClick != null) MaterialTheme.colors.onPrimary else MaterialTheme.moreColors.onDisabled,
            style = MaterialTheme.exerciseHistoryTypography.confirmButton,//MaterialTheme.typography.button,
            fontSize = BottomButtonFontSize,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(BottomButtonPadding)
        )
    }
}

@Composable
private fun bottomInnerPadding(fontSize: TextUnit, padding: Dp): Dp {
    return with(LocalDensity.current) {
        fontSize.toDp() * 1.5F + padding * 2 + 16.dp
    }
}

private val MockOptions1 = List(8) { index ->
    SectionOptionViewModel(
        number = index + 1,
        identifier = "section_identifier",
        name = "Section Name",
        questionGroupOptionViewModels = List(2) { i -> QuestionGroupOptionViewModel(number = i + 1, "", "Group Name", 1) },
    )
}

private val MockOptions2 = List(3) { sectionIndex ->
    SectionOptionViewModel(
        number = sectionIndex + 1,
        identifier = "",
        name = "",
        questionGroupOptionViewModels = List(2) { questionGroupIndex ->
            QuestionGroupOptionViewModel(
                number = questionGroupIndex + 1,
                identifier = "",
                name = "Note Naming",
                count = questionGroupIndex + 1
            )
        }
    )
}