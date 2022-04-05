package com.donald.musictheoryapp.screen

import android.annotation.SuppressLint
import android.app.Activity
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.question.Exercise
import com.donald.musictheoryapp.listener.ProgressBarOnTouchListener
import android.widget.SeekBar.OnSeekBarChangeListener
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import com.donald.musictheoryapp.pagedexercise.PagedExercise

@SuppressLint("ClickableViewAccessibility")
class QuestionReadingScreen(
    activity: Activity,
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
    private var currentPageIndex: Int
    private val displayHelper: QuestionDisplayHelper = QuestionDisplayHelper(this.activity, view, layoutInflater)
    private lateinit var pagedExercise: PagedExercise
    val exercise: Exercise; get() = pagedExercise.exercise
    private var inputPanelIsClosed = false

    // Views
    private val previousButton: Button
    private val nextButton: Button
    private val progressBar: SeekBar

    init {
        currentPageIndex = 0
        view.findViewById<View>(R.id.question_timer).visibility = View.INVISIBLE
        previousButton = view.findViewById(R.id.question_previous_button)
        nextButton = view.findViewById(R.id.question_next_button)
        progressBar = view.findViewById(R.id.question_progress_bar)
        progressBar.setOnTouchListener(ProgressBarOnTouchListener(progressBar))
        progressBar.setOnSeekBarChangeListener(
            object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    setPageIndex(progress)
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
            progressBar.progress = currentPageIndex
        }
        nextButton.setOnClickListener {
            if (currentPageIndex == exercise.questionCount - 1) {
                returnCallback()
            } else {
                changeQuestion(next = true)
            }
            updatePreviousNextButton()
            progressBar.progress = currentPageIndex
        }

        val collapsible = view.findViewById<LinearLayout>(R.id.question_input_panel_collapsible)
        view.findViewById<FrameLayout>(R.id.question_input_panel_close_button).apply {
            setOnClickListener {
                collapsible.visibility = if (inputPanelIsClosed) View.VISIBLE else View.GONE
                inputPanelIsClosed = !inputPanelIsClosed
            }
        }
    }

    fun readExercise(exercise: Exercise, sectionIndex: Int, localGroupIndex: Int) {
        pagedExercise = PagedExercise(exercise, activity.resources)
        currentPageIndex = pagedExercise.pageIndexOf(exercise.sections[sectionIndex].groups[localGroupIndex].questions[0])
        progressBar.progress = currentPageIndex
        progressBar.max = pagedExercise.pageCount - 1
        view.doOnLayout {
            displayHelper.displayPage(pagedExercise[currentPageIndex], readingMode = true)
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
    private fun setPageIndex(pageIndex: Int) {
        if (pageIndex < 0 || pageIndex > pagedExercise.pageCount - 1) throw IndexOutOfBoundsException()
        currentPageIndex = pageIndex
        displayHelper.displayPage(pagedExercise[pageIndex], readingMode = true)
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
        displayHelper.displayPage(pagedExercise[currentPageIndex], readingMode = true)
        updatePreviousNextButton()
    }

    private fun updatePreviousNextButton() {
        previousButton.isEnabled = currentPageIndex != 0
        if (currentPageIndex == pagedExercise.pageCount - 1) {
            nextButton.backgroundTintList = ColorStateList.valueOf(Color.RED)
        } else {
            nextButton.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    activity, R.color.design_default_color_primary
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