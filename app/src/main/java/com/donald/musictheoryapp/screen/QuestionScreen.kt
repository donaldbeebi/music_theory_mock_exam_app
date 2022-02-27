package com.donald.musictheoryapp.screen

import android.os.CountDownTimer
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.question.QuestionDisplayHelper
import com.donald.musictheoryapp.question.Exercise
import android.annotation.SuppressLint
import com.donald.musictheoryapp.Utils.ProgressBarOnTouchListener
import android.widget.SeekBar.OnSeekBarChangeListener
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.*
import androidx.core.view.doOnLayout
import com.donald.musictheoryapp.MainActivity
import com.donald.musictheoryapp.Utils.Time
import com.donald.musictheoryapp.Utils.Time.Companion.hr
import com.donald.musictheoryapp.Utils.Time.Companion.ms

@SuppressLint("ClickableViewAccessibility")
class QuestionScreen(
    activity: MainActivity,
    private val finishCallback: () -> Unit,
    private val timeOutCallback: () -> Unit
) : Screen(activity, R.layout.screen_question) {

    private var currentQuestionIndex: Int
    private val displayHelper: QuestionDisplayHelper = QuestionDisplayHelper(context, view, layoutInflater)
    lateinit var exercise: Exercise
        private set
    private val timer: ExerciseTimer = ExerciseTimer(2.hr, view.findViewById(R.id.question_timer_display))
    private var inputPanelIsClosed = false

    // Views
    private val previousButton: Button
    private val nextButton: Button
    private val progressBar: SeekBar
    private val mainBody: View
    private val answerPanel: View
    private val pauseOverlay: View
    private val pauseButton: Button
    private var paused = false

    init {
        currentQuestionIndex = 0
        previousButton = view.findViewById(R.id.question_previous_button)
        nextButton = view.findViewById(R.id.question_next_button)
        progressBar = view.findViewById(R.id.question_progress_bar)
        mainBody = view.findViewById(R.id.question_main_body)
        answerPanel = view.findViewById(R.id.question_answer_panel)
        pauseOverlay = view.findViewById(R.id.question_pause_overlay)

        progressBar.setOnTouchListener(ProgressBarOnTouchListener(progressBar))
        progressBar.setOnSeekBarChangeListener(
            object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    setCurrentQuestion(progress)
                }
                override fun onStartTrackingTouch(seekBar: SeekBar) { /* do nothing */ }
                override fun onStopTrackingTouch(seekBar: SeekBar) { /* do nothing */ }
            }
        )
        previousButton.setOnClickListener {
            changeQuestion(next = false)
            updatePreviousNextButton()
            progressBar.progress = currentQuestionIndex
        }
        nextButton.setOnClickListener {
            if (currentQuestionIndex == exercise.questionCount - 1) {
                finishCallback()
            } else {
                changeQuestion(next = true)
            }
            updatePreviousNextButton()
            progressBar.progress = currentQuestionIndex
        }

        pauseButton = view.findViewById<Button>(R.id.question_timer_pause_button).apply {
            setOnClickListener { pauseExercise() }
        }

        val collapsible = view.findViewById<LinearLayout>(R.id.question_input_panel_collapsible)
        view.findViewById<FrameLayout>(R.id.question_input_panel_close_button).apply {
            setOnClickListener {
                collapsible.visibility = if (inputPanelIsClosed) View.VISIBLE else View.GONE
                inputPanelIsClosed = !inputPanelIsClosed
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun startExercise(exercise: Exercise) {
        currentQuestionIndex = 0
        this.exercise = exercise
        progressBar.progress = 0
        progressBar.max = exercise.questionCount - 1
        view.doOnLayout {
            displayHelper.displayQuestion(exercise, exercise.questionAt(0), readingMode = false)
            timer.start()
        }
        updatePreviousNextButton()
    }

    private fun pauseExercise() {
        answerPanel.apply {
            visibility = if (paused) View.VISIBLE else View.INVISIBLE
        }
        mainBody.apply {
            visibility = if (paused) View.VISIBLE else View.INVISIBLE
        }
        pauseOverlay.apply {
            visibility = if (!paused) View.VISIBLE else View.INVISIBLE
        }
        if (paused) {
            timer.resume()
            pauseButton.setBackgroundResource(R.drawable.ic_baseline_pause_24)
        } else {
            timer.pause()
            pauseButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
        }
        paused = !paused
    }

    fun resumeExercise() {

    }

    /*
     * *****************
     * PRIVATE FUNCTIONS
     * *****************
     */

    // called by seekbar
    private fun setCurrentQuestion(questionIndex: Int) {
        if (questionIndex < 0 || questionIndex > exercise.questionCount - 1) throw IndexOutOfBoundsException()
        currentQuestionIndex = questionIndex
        displayHelper.displayQuestion(exercise, exercise.questionAt(currentQuestionIndex), readingMode = false)
        updatePreviousNextButton()
    }

    // called by left right buttons
    private fun changeQuestion(next: Boolean) {
        if (next && currentQuestionIndex < exercise.questionCount - 1) {
            currentQuestionIndex++
        } else if (!next && currentQuestionIndex > 0) {
            currentQuestionIndex--
        } else {
            throw IndexOutOfBoundsException()
        }
        displayHelper.displayQuestion(exercise, exercise.questionAt(currentQuestionIndex), readingMode = false)
        updatePreviousNextButton()
    }

    private fun updatePreviousNextButton() {
        previousButton.isEnabled = currentQuestionIndex != 0
        if (currentQuestionIndex == exercise.questionCount - 1) {
            nextButton.backgroundTintList = ColorStateList.valueOf(Color.RED)
        } else {
            nextButton.backgroundTintList = ColorStateList.valueOf(
                context.resources.getColor(
                    R.color.design_default_color_primary, context.theme
                )
            )
        }
        //nextButton.setEnabled(currentQuestion != questions.numberOfQuestions() - 1);
    }

    private inner class ExerciseTimer(
        var time: Time,
        private val questionTimerTextView: TextView,
    ) {

        private var timeRemaining = time.copy()
        private lateinit var timer: CountDownTimer

        fun start() {
            timer = object : CountDownTimer(time.millis, 1000) {

                override fun onTick(millisRemaining: Long) {
                    timeRemaining = millisRemaining.ms
                    questionTimerTextView.text = timeRemaining.toString()
                }

                override fun onFinish() {
                    timeOutCallback()
                }

            }
            timer.start()
        }

        fun pause() {
            timer.cancel()
        }

        fun resume() {
            timer = object : CountDownTimer(timeRemaining.millis, 1000) {

                override fun onTick(millisRemaining: Long) {
                    timeRemaining = millisRemaining.ms
                    questionTimerTextView.text = timeRemaining.toString()
                }

                override fun onFinish() {
                    timeOutCallback()
                }

            }
            timer.start()
        }

    }

}