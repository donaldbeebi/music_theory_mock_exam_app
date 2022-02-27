package com.donald.musictheoryapp.screen

import android.annotation.SuppressLint
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.question.QuestionDisplayHelper
import com.donald.musictheoryapp.question.Exercise
import com.donald.musictheoryapp.Utils.ProgressBarOnTouchListener
import android.widget.SeekBar.OnSeekBarChangeListener
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.*
import androidx.core.view.doOnLayout
import com.donald.musictheoryapp.MainActivity

@SuppressLint("ClickableViewAccessibility")
class QuestionReadingScreen(
    activity: MainActivity,
    private val returnCallback: () -> Unit,
) : Screen(activity, R.layout.screen_question) {

    /*
     * **********************
     * CLASSES AND INTERFACES
     * **********************
     */

    /*
     * ******
     * FIELDS
     * ******
     */
    private var currentQuestionIndex: Int
    private val displayHelper: QuestionDisplayHelper = QuestionDisplayHelper(context, view, layoutInflater)
    lateinit var exercise: Exercise
        private set
    private var inputPanelIsClosed = false

    // Views
    private val previousButton: Button
    private val nextButton: Button
    private val progressBar: SeekBar

    init {
        currentQuestionIndex = 0
        view.findViewById<View>(R.id.question_timer).visibility = View.INVISIBLE
        previousButton = view.findViewById(R.id.question_previous_button)
        nextButton = view.findViewById(R.id.question_next_button)
        progressBar = view.findViewById(R.id.question_progress_bar)
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

        /*
         *  SETTING UP LISTENERS
         */
        previousButton.setOnClickListener {
            changeQuestion(next = false)
            updatePreviousNextButton()
            progressBar.progress = currentQuestionIndex
        }
        nextButton.setOnClickListener {
            if (currentQuestionIndex == exercise.questionCount - 1) {
                returnCallback()
            } else {
                changeQuestion(next = true)
            }
            updatePreviousNextButton()
            progressBar.progress = currentQuestionIndex
        }

        val collapsible = view.findViewById<LinearLayout>(R.id.question_input_panel_collapsible)
        view.findViewById<FrameLayout>(R.id.question_input_panel_close_button).apply {
            setOnClickListener {
                collapsible.visibility = if (inputPanelIsClosed) View.VISIBLE else View.GONE
                inputPanelIsClosed = !inputPanelIsClosed
            }
        }
    }

    fun readExercise(exercise: Exercise, groupIndex: Int) {
        currentQuestionIndex = exercise.questionIndexOf(exercise.groupAt(groupIndex).questions[0])
        this.exercise = exercise
        progressBar.progress = currentQuestionIndex
        progressBar.max = exercise.questionCount - 1
        view.doOnLayout {
            displayHelper.displayQuestion(exercise, exercise.questionAt(groupIndex, 0), readingMode = true)
            // TODO: exerciseTimer.disable()
        }
        updatePreviousNextButton()
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
        displayHelper.displayQuestion(exercise, exercise.questionAt(currentQuestionIndex), readingMode = true)
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
        displayHelper.displayQuestion(exercise, exercise.questionAt(currentQuestionIndex), readingMode = true)
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
    }

    /*
     * *******
     * METHODS
     * *******
     */

}