package com.donald.musictheoryapp.screen

import android.os.CountDownTimer
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.question.Exercise
import android.annotation.SuppressLint
import android.app.Activity
import com.donald.musictheoryapp.listener.ProgressBarOnTouchListener
import android.widget.SeekBar.OnSeekBarChangeListener
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import com.donald.musictheoryapp.pagedexercise.PagedExercise
import com.donald.musictheoryapp.util.Time.Companion.ms

// TODO: CRASHES WHEN BACKING FROM ACTIVITY AND RESTARTING THIS ACTIVITY

@SuppressLint("ClickableViewAccessibility")
class ExerciseScreen(
    activity: Activity,
    private val showExitExerciseDialog: (Exercise, Int) -> Unit,
    showTimeOutDialog: (Exercise) -> Unit
) : Screen(activity, R.layout.screen_question) {

    private var currentPageIndex: Int
    private val displayHelper: QuestionDisplayHelper = QuestionDisplayHelper(activity, view, layoutInflater)
    private lateinit var pagedExercise: PagedExercise
    val exercise: Exercise; get() = pagedExercise.exercise
    private val timer: ExerciseTimer = ExerciseTimer(view.findViewById(R.id.question_timer_display), showTimeOutDialog)
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
        currentPageIndex = 0
        previousButton = view.findViewById(R.id.question_previous_button)
        nextButton = view.findViewById(R.id.question_next_button)
        progressBar = view.findViewById(R.id.question_progress_bar)
        mainBody = view.findViewById(R.id.question_main_body)
        answerPanel = view.findViewById(R.id.question_input_panel)
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
            progressBar.progress = currentPageIndex
        }

        nextButton.setOnClickListener {
            if (currentPageIndex == exercise.questionCount - 1) {
                showExitExerciseDialog(exercise, currentPageIndex)
            } else {
                changeQuestion(next = true)
            }
            updatePreviousNextButton()
            progressBar.progress = currentPageIndex
        }

        pauseButton = view.findViewById<Button>(R.id.question_timer_pause_button).apply {
            setOnClickListener { togglePause() }
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
        currentPageIndex = exercise.savedPageIndex
        this.pagedExercise = PagedExercise(exercise, activity.resources)
        progressBar.max = pagedExercise.pageCount - 1
        progressBar.progress = exercise.savedPageIndex
        view.doOnLayout {
            displayHelper.displayPage(pagedExercise[exercise.savedPageIndex], readingMode = false)
            timer.start(exercise)
        }
        updatePreviousNextButton()
    }

    fun promptExitExercise() {
        showExitExerciseDialog(exercise, currentPageIndex)
    }

    /*
     * *****************
     * PRIVATE FUNCTIONS
     * *****************
     */

    private fun togglePause() {
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
            timer.start(exercise)
            pauseButton.setBackgroundResource(R.drawable.ic_baseline_pause_24)
        } else {
            timer.pause()
            pauseButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
        }
        paused = !paused
    }

    // called by seekbar
    private fun setCurrentQuestion(pageIndex: Int) {
        if (pageIndex < 0 || pageIndex > pagedExercise.pageCount - 1) throw IndexOutOfBoundsException()
        currentPageIndex = pageIndex
        displayHelper.displayPage(pagedExercise[currentPageIndex], readingMode = false)
        updatePreviousNextButton()
    }

    // called by left right buttons
    private fun changeQuestion(next: Boolean) {
        if (next && currentPageIndex < pagedExercise.pageCount - 1) {
            currentPageIndex++
        } else if (!next && currentPageIndex > 0) {
            currentPageIndex--
        } else {
            throw IndexOutOfBoundsException()
        }
        displayHelper.displayPage(pagedExercise[currentPageIndex], readingMode = false)
        updatePreviousNextButton()
    }

    private fun updatePreviousNextButton() {
        previousButton.isEnabled = currentPageIndex != 0
        if (currentPageIndex == pagedExercise.pageCount - 1) {
            nextButton.backgroundTintList = ColorStateList.valueOf(Color.RED)
        } else {
            nextButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.design_default_color_primary))
        }
    }

    private class ExerciseTimer(
        //private var time: Time,
        private val questionTimerTextView: TextView,
        private val showTimeOutDialog: (Exercise) -> Unit
    ) {

        //private var timeRemaining = time
        private lateinit var timer: CountDownTimer

        fun start(exercise: Exercise) {
            timer = object : CountDownTimer(exercise.timeRemaining.millis, 1000) {
                override fun onTick(millisRemaining: Long) {
                    val timeRemaining = millisRemaining.ms
                    exercise.timeRemaining = timeRemaining
                    questionTimerTextView.text = timeRemaining.toString()
                }
                override fun onFinish() {
                    showTimeOutDialog(exercise)
                }
            }
            timer.start()
        }

        fun pause() {
            timer.cancel()
        }

    }

}