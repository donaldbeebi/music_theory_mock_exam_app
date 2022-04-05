package com.donald.musictheoryapp.customview.questionbutton

import android.content.Context
import androidx.core.content.ContextCompat
import com.donald.musictheoryapp.R
import android.graphics.PorterDuffColorFilter
import android.graphics.PorterDuff

class ColorButton(context: Context) : QuestionButton(
    context,
    ContextCompat.getDrawable(
        context, R.drawable.background_question_button
    ) ?: throw IllegalArgumentException("Unable to get background")
) {

    fun setColor(color: Int) {
        backgroundBase.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.ADD)
    }

}