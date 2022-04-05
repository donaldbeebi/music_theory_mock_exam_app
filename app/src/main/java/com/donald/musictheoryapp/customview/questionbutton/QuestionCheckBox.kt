package com.donald.musictheoryapp.customview.questionbutton

import android.content.Context
import androidx.appcompat.widget.AppCompatButton
import com.donald.musictheoryapp.R

class QuestionCheckBox(context: Context) : AppCompatButton(context) {

    var isChecked = false
        set(isChecked) {
            field = isChecked
            refreshDrawableState()
        }

    var isCorrect = false
        set(isCorrect) {
            field = isCorrect
            refreshDrawableState()
        }

    init {
        setBackgroundResource(R.drawable.background_check_box)
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        var space = 0
        if (isChecked) space++
        if (isCorrect) space++
        val drawableState = super.onCreateDrawableState(extraSpace + space)
        if (isChecked) {
            mergeDrawableStates(drawableState, STATE_CHECKED)
        }
        if (isCorrect) {
            mergeDrawableStates(drawableState, STATE_CORRECT)
        }
        return drawableState
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = width.coerceAtMost(height)
        setMeasuredDimension(size, size)
    }

    companion object {

        private val STATE_CHECKED = intArrayOf(R.attr.state_checked)
        private val STATE_CORRECT = intArrayOf(R.attr.state_correct)

    }

}