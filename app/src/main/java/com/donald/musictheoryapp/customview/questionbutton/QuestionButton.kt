package com.donald.musictheoryapp.customview.questionbutton

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.RelativeLayout
import android.graphics.drawable.GradientDrawable
import com.donald.musictheoryapp.R
import androidx.core.content.ContextCompat
import android.graphics.drawable.LayerDrawable

abstract class QuestionButton(
    context: Context,
    protected val backgroundBase: Drawable
) : RelativeLayout(context) {

    private var fixedRatio = 2f / 1f
    private val selectionBorder: GradientDrawable = run {
        val drawable = ContextCompat.getDrawable(context, R.drawable.shape_selection_border)?: throw IllegalStateException("Unable to get drawable")
        drawable.mutate() as GradientDrawable
    }

    init {
        selectionBorder.alpha = 0
        val layerDrawable = LayerDrawable(arrayOf(backgroundBase, selectionBorder))
        super.setBackground(layerDrawable)
        super.setPadding(PADDING, PADDING, PADDING, PADDING)
    }

    fun setStrokeColor(color: Int) {
        selectionBorder.setStroke(
            context.resources.getDimension(R.dimen.question_button_stroke_width).toInt(),
            color,
            context.resources.getDimension(R.dimen.question_button_stroke_dash_width),
            context.resources.getDimension(R.dimen.question_button_stroke_dash_gap)
        )
    }

    override fun setSelected(isSelected: Boolean) {
        super.setSelected(isSelected)
        selectionBorder.alpha = if (isSelected) 255 else 0
    }

    fun setFixedRatio(ratio: Float) {
        fixedRatio = ratio
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        var height = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        val givenRatio = width / height
        if (givenRatio < fixedRatio) {
            height = width / fixedRatio
        } else {
            width = height * fixedRatio
        }
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(width.toInt(), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height.toInt(), MeasureSpec.EXACTLY)
        )
    }

    companion object {
        private const val PADDING = 16
    }

}