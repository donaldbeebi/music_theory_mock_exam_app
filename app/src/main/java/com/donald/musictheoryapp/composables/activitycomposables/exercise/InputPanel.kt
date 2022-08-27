package com.donald.musictheoryapp.composables.activitycomposables.exercise

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.activitycomposables.exercise.input.button.ButtonInput
import com.donald.musictheoryapp.composables.activitycomposables.exercise.input.button.ButtonInputState
import com.donald.musictheoryapp.composables.activitycomposables.exercise.input.button.ButtonType
import com.donald.musictheoryapp.composables.activitycomposables.exercise.input.checkbox.CheckBoxInput
import com.donald.musictheoryapp.composables.activitycomposables.exercise.input.checkbox.CheckBoxInputState
import com.donald.musictheoryapp.composables.activitycomposables.exercise.input.interval.IntervalInput
import com.donald.musictheoryapp.composables.activitycomposables.exercise.input.interval.IntervalInputState
import com.donald.musictheoryapp.composables.activitycomposables.exercise.input.text.TextInput
import com.donald.musictheoryapp.composables.activitycomposables.exercise.input.text.TextInputState
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.exerciseTypography
import com.donald.musictheoryapp.composables.theme.moreShapes
import com.donald.musictheoryapp.music.musicxml.*
import com.donald.musictheoryapp.question.*
import com.donald.musictheoryapp.util.buildString
import com.donald.musictheoryapp.util.toggle

private val PanelHandleHeight = 32.dp
private val PanelHandleBarThickness = 4.dp
private const val PANEL_HANDLE_BAR_WIDTH_FRACTION = 0.13F

private const val PANEL_FADE_IN_DURATION_MILLIS = 100
private const val PANEL_EXPAND_DURATION_MILLIS = 200
private const val PANEL_FADE_OUT_DURATION_MILLIS = 100
private const val PANEL_SHRINK_DURATION_MILLIS = 200

private val MockMultipleChoiceQuestion = MultipleChoiceQuestion(
    number = 1,
    descriptions = emptyList(),
    inputHint = null,
    options = listOf("Cat", "Dog", "Sheep", "Giraffe"),
    optionType = MultipleChoiceQuestion.OptionType.Text,
    answer = MultipleChoiceQuestion.Answer(userAnswer = null, correctAnswers = listOf(1))
)

private val MockTruthQuestion = TruthQuestion(
    number = 1,
    descriptions = emptyList(),
    inputHint = null,
    answer = TruthQuestion.Answer(userAnswer = null, correctAnswer = true)
)

private val MockTextInputQuestion = TextInputQuestion(
    number = 1,
    descriptions = emptyList(),
    inputHint = "Your answer is:",
    inputType = TextInputQuestion.InputType.Number,
    answers = listOf(TextInputQuestion.Answer(userAnswer = null, correctAnswers = emptyList()))
)

private val MockCheckBoxQuestion = CheckBoxQuestion(
    number = 1,
    descriptions = emptyList(),
    inputHint = null,
    answers = listOf(
        CheckBoxQuestion.Answer(userAnswer = true, correctAnswer = true),
        CheckBoxQuestion.Answer(userAnswer = false, correctAnswer = false),
        CheckBoxQuestion.Answer(userAnswer = null, correctAnswer = true)
    )
)

private val MockScore = Score(
    parts = arrayOf(
        Part(
            id = "",
            measures = arrayOf(
                Measure(
                    attributes = Attributes(
                        divisions = 1,
                        key = Key(fifths = 0, mode = Key.Mode.MAJOR),
                        time = Time(4, 4),
                        staves = 1,
                        clefs = arrayOf(Clef(sign = Sign.G, line = 2, printObject = true))
                    ),
                    notes = ArrayList<Note>().apply {
                        add(
                            Note(
                                printObject = false,
                                pitch = Pitch(step = Step.C, alter = 0, octave = 4),
                                duration = 1,
                                type = Type.Whole,
                                accidental = null,
                                chord = false,
                                staff = 1,
                                notations = null,
                            )
                        )

                    },
                    barline = null
                )
            )
        )
    )
)

private val MockIntervalInputQuestion = IntervalInputQuestion(
    number = 1,
    descriptions = emptyList(),
    inputHint = null,
    score = MockScore,
    requiredInterval = "",
    answer = IntervalInputQuestion.Answer(
        userAnswer = null,
        correctAnswer = Note(
            printObject = false,
            pitch = Pitch(step = Step.C, alter = 0, octave = 4),
            duration = 1,
            type = Type.Whole,
            accidental = null,
            chord = false,
            staff = 1,
            notations = null
        )
    )
)

@Preview
@Composable
private fun InputPanelPreview() = CustomTheme {
    val question = MockTextInputQuestion
    var savedAnswer by remember { mutableStateOf<String?>(null) }
    val mockImage = painterResource(R.drawable.test_time_signature)
    val imageProvider = ImageProvider { mockImage }
    var open by remember { mutableStateOf(true) }
    Column(modifier = Modifier.background(MaterialTheme.colors.surface)) {
        Button(onClick = { savedAnswer = getUserAnswer(question) }) { Text("Refresh") }
        Text(savedAnswer.toString())
        with(imageProvider) {
            InputPanel(
                question = question,
                readMode = true,
                open = open,
                onPanelHandleClick = { open = toggle(open) },
                focusManager = LocalFocusManager.current,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(12.dp),
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ImageProvider.InputPanel(
    question: ChildQuestion,
    readMode: Boolean,
    open: Boolean,
    onPanelHandleClick: () -> Unit,
    focusManager: FocusManager,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.moreShapes.answerPanel,
        color = MaterialTheme.colors.background,
        modifier = modifier//.animateContentSize()
    ) {
        Column {
            HandleBar(
                onClick = onPanelHandleClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PanelHandleHeight)
            )
            AnimatedVisibility(
                enter = expandVertically(
                    animationSpec = tween(durationMillis = PANEL_EXPAND_DURATION_MILLIS, easing = LinearOutSlowInEasing)
                ) + fadeIn(
                    animationSpec = tween(durationMillis = PANEL_FADE_IN_DURATION_MILLIS, delayMillis = PANEL_EXPAND_DURATION_MILLIS)
                ),
                exit = fadeOut(
                    animationSpec = tween(durationMillis = PANEL_FADE_OUT_DURATION_MILLIS)
                ) + shrinkVertically(
                    animationSpec = tween(durationMillis = PANEL_SHRINK_DURATION_MILLIS, easing = LinearOutSlowInEasing)
                ),
                visible = open
            ) {
                AnimatedContent(
                    targetState = question,
                    transitionSpec = { fadeIn(animationSpec = tween(durationMillis = 100)) with fadeOut(animationSpec = tween(durationMillis = 100)) }
                ) { targetQuestion ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        targetQuestion.inputHint?.let { inputHint ->
                            Text(
                                text = inputHint,
                                color = MaterialTheme.colors.onSurface,
                                style = MaterialTheme.exerciseTypography.inputHint
                            )
                        }
                        Input(
                            question = targetQuestion,
                            readMode = readMode,
                            focusManager = focusManager,
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageProvider.Input(
    question: ChildQuestion,
    readMode: Boolean,
    focusManager: FocusManager,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        when (question) {
            is MultipleChoiceQuestion -> MultipleChoiceQuestionInput(question, readMode)
            is TruthQuestion -> TruthQuestionInput(question, readMode)
            is TextInputQuestion -> TextInputQuestionInput(question, readMode, focusManager)
            is CheckBoxQuestion -> CheckBoxQuestionInput(question, readMode)
            is IntervalInputQuestion -> IntervalInputQuestionInput(question, readMode)
        }
    }
}

@Composable
private fun HandleBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = Box(
    modifier = modifier.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
) {
    val barColor = MaterialTheme.colors.onBackground
    Canvas(
        onDraw = {
            drawLine(
                color = barColor,
                start = Offset(0F, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = size.height,
                cap = StrokeCap.Round
            )
        },
        modifier = Modifier
            .height(PanelHandleBarThickness)
            .fillMaxWidth(PANEL_HANDLE_BAR_WIDTH_FRACTION)
            .align(Alignment.Center)
    )
}

private fun getUserAnswer(question: ChildQuestion): String {
    return when (question) {
        is MultipleChoiceQuestion -> {
            question.answer.userAnswer?.let { question.options[it] }.toString()
        }
        is TruthQuestion -> {
            question.answer.userAnswer.toString()
        }
        is TextInputQuestion -> {
            check(question.answers.size == 1)
            question.answers[0].userAnswer.toString()
        }
        is CheckBoxQuestion -> {
            question.answers.joinToString { it.userAnswer.toString() }
        }
        is IntervalInputQuestion -> {
            question.answer.userAnswer.toString()
        }
    }
}

@Composable
private fun ImageProvider.MultipleChoiceQuestionInput(
    question: MultipleChoiceQuestion,
    readMode: Boolean,
) : Unit = when (readMode) {
    false -> {
        var selectedIndex by remember { mutableStateOf(question.answer.userAnswer) }
        ButtonInput(
            options = question.options,
            selectedIndex = selectedIndex,
            buttonType = when (question.optionType) {
                MultipleChoiceQuestion.OptionType.Text -> ButtonType.Text
                MultipleChoiceQuestion.OptionType.Image -> ButtonType.Image
                MultipleChoiceQuestion.OptionType.Score -> ButtonType.Score
            },
            buttonInputState = ButtonInputState.InputMode(
                onSelect = { buttonIndex ->
                    question.answer.userAnswer = buttonIndex
                    selectedIndex = buttonIndex
                }
            )
        )
    }
    true -> {
        val selectedIndex = question.answer.userAnswer
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ButtonInput(
                options = question.options,
                selectedIndex = selectedIndex,
                buttonType = when (question.optionType) {
                    MultipleChoiceQuestion.OptionType.Text -> ButtonType.Text
                    MultipleChoiceQuestion.OptionType.Image -> ButtonType.Image
                    MultipleChoiceQuestion.OptionType.Score -> ButtonType.Score
                },
                buttonInputState = ButtonInputState.ReadMode
            )
            CorrectAnswerPanel(
                correctAnswerPanelState = when {
                    question.points == question.maxPoints && question.answer.correctAnswers.size > 1 -> {
                        // the question is correct but there are alternative correct answers
                        CorrectAnswerPanelState.Correct(
                            content = {
                                CorrectAnswerText(
                                    text = when (question.optionType) {
                                        MultipleChoiceQuestion.OptionType.Text -> buildString(
                                            items = question.answer.correctAnswers.filter { it != question.answer.userAnswer }.map { question.options[it] },
                                            word = stringResource(R.string.and_or_string_builder_or)
                                        )
                                        else -> stringResource(
                                            R.string.multiple_choice_question_option,
                                            buildString(
                                                items = question.answer.correctAnswers.filter { it != question.answer.userAnswer }.map { it + 1 },
                                                word = stringResource(R.string.and_or_string_builder_or)
                                            )
                                        )
                                    }
                                )
                            }
                        )
                    }
                    question.points == question.maxPoints && question.answer.correctAnswers.size == 1 -> {
                        // the question is correct and there are no other correct answers
                        CorrectAnswerPanelState.Correct()
                    }
                    else -> {
                        CorrectAnswerPanelState.Incorrect(
                            noAnswer = question.answer.userAnswer == null,
                            content = {
                                CorrectAnswerText(
                                    text = when (question.optionType) {
                                        MultipleChoiceQuestion.OptionType.Text -> buildString(
                                            items = question.answer.correctAnswers.map { question.options[it] },
                                            word = stringResource(R.string.and_or_string_builder_or)
                                        )
                                        else -> stringResource(
                                            R.string.multiple_choice_question_option,
                                            buildString(
                                                items = question.answer.correctAnswers.map { it + 1 },
                                                word = stringResource(R.string.and_or_string_builder_or)
                                            )
                                        ).also { Log.d("Fuck you in InputPanel", buildString(question.answer.correctAnswers, word = "or")) }
                                    }
                                )
                            }
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun ImageProvider.TruthQuestionInput(
    question: TruthQuestion,
    readMode: Boolean
): Unit = when (readMode) {
    false -> {
        var selectedAnswer by remember { mutableStateOf(question.answer.userAnswer) }
        ButtonInput(
            options = listOf(stringResource(R.string.true_button_text), stringResource(R.string.false_button_text)),
            selectedIndex = selectedAnswer?.let { if ( it == true) 0 else 1 },
            buttonType = ButtonType.Text,
            buttonInputState = ButtonInputState.InputMode(
                onSelect = { buttonIndex ->
                    when (buttonIndex) {
                        null -> {
                            question.answer.userAnswer = null; selectedAnswer = null
                        }
                        0 -> {
                            question.answer.userAnswer = true; selectedAnswer = true
                        }
                        1 -> {
                            question.answer.userAnswer = false; selectedAnswer = false
                        }
                        else -> throw IllegalStateException("Truth question with button index $buttonIndex")
                    }
                }
            )
        )
    }
    true -> {
        val selectedAnswer = question.answer.userAnswer
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ButtonInput(
                options = listOf(stringResource(R.string.true_button_text), stringResource(R.string.false_button_text)),
                selectedIndex = selectedAnswer?.let { if (it == true) 0 else 1 },
                buttonType = ButtonType.Text,
                buttonInputState = ButtonInputState.ReadMode
            )
            CorrectAnswerPanel(
                correctAnswerPanelState = if (question.answer.userAnswer == question.answer.correctAnswer) {
                    CorrectAnswerPanelState.Correct()
                } else {
                    CorrectAnswerPanelState.Incorrect(
                        noAnswer = question.answer.userAnswer == null,
                        content = {
                            CorrectAnswerText(
                                if (question.answer.correctAnswer == true) {
                                    stringResource(R.string.truth_question_true)
                                } else {
                                    stringResource(R.string.truth_question_false)
                                }
                            )
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun TextInputQuestionInput(
    question: TextInputQuestion,
    readMode: Boolean,
    focusManager: FocusManager
) = when (readMode) {
    false -> {
        check(question.answers.size == 1)
        var text by remember { mutableStateOf(question.answers[0].userAnswer ?: "") }
        TextInput(
            label = stringResource(R.string.text_input_hint),
            text = text,
            textInputState = TextInputState.InputMode(
                inputType = question.inputType,
                onInput = { newValue -> text = newValue },
                onDone = {
                    check(question.answers.size == 1)
                    focusManager.clearFocus()
                    question.answers[0].userAnswer = text.takeIf { it.isNotBlank() }
                }
            ),
            modifier = Modifier
                .fillMaxWidth(0.5F)
                .wrapContentHeight()
        )
    }
    true -> {
        check(question.answers.size == 1)
        val text = question.answers[0].userAnswer ?: ""
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextInput(
                label = stringResource(R.string.text_input_hint),
                text = text,
                textInputState = TextInputState.ReadMode,
                modifier = Modifier
                    .fillMaxWidth(0.5F)
                    .wrapContentHeight()
            )
            CorrectAnswerPanel(
                correctAnswerPanelState = when {
                    question.points == question.maxPoints && question.answers[0].correctAnswers.size > 1 -> {
                        // the question has other alternative answers
                        CorrectAnswerPanelState.Correct(
                            content = {
                                CorrectAnswerText(
                                    buildString(
                                        items = question.answers[0].correctAnswers.filter { it != question.answers[0].userAnswer },
                                        word = stringResource(R.string.and_or_string_builder_or)
                                    )
                                )
                            }
                        )
                    }
                    question.points == question.maxPoints && question.answers[0].correctAnswers.size == 1 -> {
                        CorrectAnswerPanelState.Correct()
                    }
                    else -> {
                        CorrectAnswerPanelState.Incorrect(
                            noAnswer = question.answers[0].userAnswer == null,
                            content = {
                                CorrectAnswerText(
                                    buildString(
                                        items = question.answers[0].correctAnswers,
                                        word = stringResource(R.string.and_or_string_builder_or)
                                    )
                                )
                            }
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun CheckBoxQuestionInput(
    question: CheckBoxQuestion,
    readMode: Boolean
): Unit = when (readMode) {
    false -> {
        val answers = question.answers.map { it.userAnswer }.toMutableStateList()
        CheckBoxInput(
            answers = answers,
            checkBoxInputState = CheckBoxInputState.InputMode(
                onInput = { answer, answerIndex ->
                    answers[answerIndex] = answer
                    question.answers[answerIndex].userAnswer = answer
                }
            ),
        )
    }
    true -> {
        val answers = question.answers.map { it.userAnswer }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CheckBoxInput(
                answers = answers,
                checkBoxInputState = CheckBoxInputState.ReadMode
            )
            CorrectAnswerPanel(
                correctAnswerPanelState = if (question.points == question.maxPoints) {
                    CorrectAnswerPanelState.Correct()
                } else {
                    CorrectAnswerPanelState.Incorrect(
                        noAnswer = question.answers.all { it.userAnswer == null },
                        content = {
                            CheckBoxInput(
                                answers = question.answers.map { it.correctAnswer },
                                checkBoxInputState = CheckBoxInputState.ReadMode
                            )
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun IntervalInputQuestionInput(
    question: IntervalInputQuestion,
    readMode: Boolean
) = when (readMode) {
    false -> {
        IntervalInput(
            question = question,
            intervalInputState = IntervalInputState.InputMode,
            modifier = Modifier.fillMaxWidth()
        )
    }
    true -> {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IntervalInput(
                question = question,
                intervalInputState = IntervalInputState.ReadMode,
                modifier = Modifier.fillMaxWidth()
            )
            CorrectAnswerPanel(
                correctAnswerPanelState = if (question.answer.userAnswer == question.answer.correctAnswer) {
                    CorrectAnswerPanelState.Correct()
                } else {
                    CorrectAnswerPanelState.Incorrect(
                        noAnswer = question.answer.userAnswer == null,
                        content = {
                            IntervalInput(
                                question = question,
                                intervalInputState = IntervalInputState.CorrectAnswerMode,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun CorrectAnswerPanel(
    correctAnswerPanelState: CorrectAnswerPanelState
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(top = 16.dp)
    ) {
        when (correctAnswerPanelState) {
            is CorrectAnswerPanelState.Correct -> {
                Text(
                    // ADD NULL USER ANSWER
                    text = if (correctAnswerPanelState.content == null) {
                        stringResource(R.string.question_correct_string)
                    } else {
                        stringResource(R.string.question_correct_also_correct_string)
                    },
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.exerciseTypography.correctAnswerPanel,
                )
                correctAnswerPanelState.content?.invoke()

            }
            is CorrectAnswerPanelState.Incorrect -> {
                Text(
                    text = if (correctAnswerPanelState.noAnswer) {
                        stringResource(R.string.question_no_answer_string)
                    } else {
                        stringResource(R.string.question_incorrect_string)
                    },
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.exerciseTypography.correctAnswerPanel
                )
                correctAnswerPanelState.content.invoke()
            }
        }
    }
}

sealed class CorrectAnswerPanelState {
    class Correct(
        val content: (@Composable () -> Unit)? = null
    ) : CorrectAnswerPanelState()
    class Incorrect(
        val noAnswer: Boolean,
        val content: @Composable () -> Unit
    ) : CorrectAnswerPanelState()
}

@Composable
private fun CorrectAnswerText(
    text: String
) = Text(
    text = text,
    color = MaterialTheme.colors.onSurface,
    style = MaterialTheme.exerciseTypography.correctAnswerPanel
)

// TODO: STANDARDIZE TYPOGRAPHY