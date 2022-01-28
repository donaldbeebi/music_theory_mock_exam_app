package com.donald.musictheoryapp.Screen

import android.widget.TextView
import android.os.CountDownTimer
import com.donald.musictheoryapp.Utils.TimeFormatter
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.Question.QuestionDisplayHelper
import com.donald.musictheoryapp.QuestionArray.QuestionArray
import android.widget.SeekBar
import android.annotation.SuppressLint
import android.content.Context
import com.donald.musictheoryapp.Utils.ProgressBarOnTouchListener
import android.widget.SeekBar.OnSeekBarChangeListener
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.Button
import com.donald.musictheoryapp.BuildConfig
import java.lang.AssertionError
import java.lang.IllegalStateException

class QuestionScreen(
    context: Context?,
    view: View,
    onFinishExerciseListener: OnFinishExerciseListener,
    onReturnToOverviewListener: OnReturnToOverviewListener
) : Screen(context, view) {
    /*
     * **********************
     * CLASSES AND INTERFACES
     * **********************
     */
    interface OnFinishExerciseListener {
        fun onFinishExercise(outOfTime: Boolean)
    }

    interface OnReturnToOverviewListener {
        fun onReturnToOverview()
    }

    private class ExerciseTimer(
        timeInSeconds: Int,
        private val questionTimerTextView: TextView,
        private val listener: OnFinishExerciseListener
        ) : CountDownTimer((timeInSeconds * 1000).toLong(), 1000) {
            override fun onTick(timeRemainingInMillis: Long) {
                questionTimerTextView.text = TimeFormatter.convert(timeRemainingInMillis)
        }
        override fun onFinish() {
            //questionTimerTextView.setText(R.string.times_up_text)
            listener.onFinishExercise(true)
        }
    }

    /*
     * ******
     * FIELDS
     * ******
     */
    var readingMode = false
        private set
    private var currentQuestion: Int
    private val displayHelper: QuestionDisplayHelper
    var questions: QuestionArray? = null
        private set
    private val onFinishExerciseListener: OnFinishExerciseListener
    private val onReturnToOverviewListener: OnReturnToOverviewListener
    private val exerciseTimer: ExerciseTimer

    // Views
    private val previousButton: Button
    private val nextButton: Button
    private val progressBar: SeekBar

    @SuppressLint("ClickableViewAccessibility")
    fun setQuestions(questions: QuestionArray) {
        this.questions = questions
        currentQuestion = 0
        progressBar.min = 0
        progressBar.max = questions.questionCount() - 1
        progressBar.setOnTouchListener(
            ProgressBarOnTouchListener(progressBar)
        )
        progressBar.setOnSeekBarChangeListener(
            object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    setCurrentQuestion(progress)
                }
                override fun onStartTrackingTouch(seekBar: SeekBar) { /* do nothing */ }
                override fun onStopTrackingTouch(seekBar: SeekBar) { /* do nothing */ }
            }
        )
    }

    fun startExercise() {
        if (BuildConfig.DEBUG && questions == null) throw AssertionError("No questions set. Make sure to call setQuestions(QuestionArray) first.")
        readingMode = false
        displayHelper.setReadingMode(readingMode)
        displayCurrentQuestion()
    }

    fun displayQuestion(questionIndex: Int) {
        if (BuildConfig.DEBUG && questions == null) throw AssertionError("No questions set. Make sure to call setQuestions(QuestionArray) first.")
        readingMode = true
        currentQuestion = questionIndex
        displayHelper.setReadingMode(true)
        progressBar.setProgress(questionIndex, false)
        displayCurrentQuestion()
    }

    fun startTimer() {
        exerciseTimer.start()
    }

    fun onBackPressed() {
        changeQuestion(false)
        updatePreviousNextButton()
    }

    /*
     * *****************
     * PRIVATE FUNCTIONS
     * *****************
     */
    private fun displayCurrentQuestion() {
        displayHelper.displayQuestion(questions!!.questionAt(currentQuestion))
        updatePreviousNextButton()
    }

    private fun changeQuestion(nextQuestion: Boolean) {
        if (nextQuestion && currentQuestion < questions!!.questionCount() - 1) currentQuestion++ else if (!nextQuestion && currentQuestion > 0) currentQuestion--
        displayCurrentQuestion()
    }

    private fun setCurrentQuestion(questionIndex: Int) {
        check(!(questionIndex < 0 || questionIndex > questions!!.questionCount() - 1))
        currentQuestion = questionIndex
        displayCurrentQuestion()
    }

    private fun updatePreviousNextButton() {
        if (questions != null) {
            previousButton.isEnabled = currentQuestion != 0
            if (currentQuestion == questions!!.questionCount() - 1) {
                nextButton.backgroundTintList = ColorStateList.valueOf(Color.RED)
            } else {
                nextButton.backgroundTintList = ColorStateList.valueOf(
                    getContext().resources.getColor(
                        R.color.design_default_color_primary, getContext().theme
                    )
                )
            }
            //nextButton.setEnabled(currentQuestion != questions.numberOfQuestions() - 1);
        } else throw IllegalStateException(
            "updatePreviousNextButton() called before questions are set!"
        )
    }

    /*
     * *******
     * METHODS
     * *******
     */
    init {
        displayHelper = QuestionDisplayHelper(this)
        this.onFinishExerciseListener = onFinishExerciseListener
        this.onReturnToOverviewListener = onReturnToOverviewListener
        exerciseTimer = ExerciseTimer(60 * 60 * 2, view.findViewById(R.id.question_timer_display), onFinishExerciseListener)
        currentQuestion = 0
        previousButton = view.findViewById(R.id.question_previous_button)
        nextButton = view.findViewById(R.id.question_next_button)
        progressBar = view.findViewById(R.id.question_progress_bar)

        /*
         *  SETTING UP LISTENERS
         */previousButton.setOnClickListener {
            changeQuestion(false)
            updatePreviousNextButton()
            progressBar.progress = currentQuestion
        }
        nextButton.setOnClickListener {
            if (currentQuestion == questions!!.questionCount() - 1) {
                if (!readingMode) this@QuestionScreen.onFinishExerciseListener.onFinishExercise(false) else this@QuestionScreen.onReturnToOverviewListener.onReturnToOverview()
            } else {
                changeQuestion(true)
            }
            updatePreviousNextButton()
            progressBar.progress = currentQuestion
        }
    }
}