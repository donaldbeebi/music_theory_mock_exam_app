package com.donald.musictheoryapp.customview.questionbutton

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import android.widget.TextView
import android.view.LayoutInflater
import com.donald.musictheoryapp.R
import android.util.TypedValue

@SuppressLint("ViewConstructor")
class ImageButton(context: Context, backgroundBase: RoundedBitmapDrawable) : QuestionButton(
    context,
    backgroundBase
) {

    fun setNumber(number: Int) {
        val numberView = LayoutInflater.from(context).inflate(
            R.layout.part_button_number, this, false
        ) as TextView
        numberView.text = number.toString()
        val numberParams = LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
        )
        numberParams.addRule(ALIGN_PARENT_TOP)
        numberParams.addRule(ALIGN_PARENT_START)
        numberView.layoutParams = numberParams
        addView(numberView)
    }

    init {
        val radius = TypedValue()
        resources.getValue(R.dimen.question_button_corner_radius, radius, true)
        backgroundBase.cornerRadius = radius.getDimension(resources.displayMetrics)
        elevation = context.resources.getDimension(R.dimen.image_button_elevation)
    }
}