package com.donald.musictheoryapp.customview.questionbutton

import android.annotation.SuppressLint
import android.content.Context
import com.donald.musictheoryapp.music.musicxml.Score
import androidx.core.content.ContextCompat
import com.donald.musictheoryapp.R
import android.widget.TextView
import android.view.LayoutInflater
import com.donald.musictheoryapp.customview.scoreview.ScoreView

@SuppressLint("ViewConstructor")
class ScoreButton(context: Context, score: Score) : QuestionButton(
    context,
    ContextCompat.getDrawable(
        context, R.drawable.shape_score_view_background
    ) ?: throw IllegalStateException("Unable to get background")
) {

    init {
        val scoreView = ScoreView(context)
        scoreView.setScore(score)
        val scoreParams = LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
        )
        scoreParams.addRule(CENTER_IN_PARENT)
        scoreView.layoutParams = scoreParams
        addView(scoreView)
        elevation = context.resources.getDimension(R.dimen.image_button_elevation)
    }

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

}