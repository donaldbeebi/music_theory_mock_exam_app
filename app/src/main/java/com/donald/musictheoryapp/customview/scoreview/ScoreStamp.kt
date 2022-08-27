package com.donald.musictheoryapp.customview.scoreview

import android.graphics.Canvas
import android.graphics.Paint

abstract class ScoreStamp(private val view: ScoreView) {

    protected val glyphSize: Float
        get() = view.glyphSize

    protected val noteWidth: Float
        get() = view.glyphSize * REL_SOLID_NOTE_WIDTH

    protected val staffHeight: Float
        get() = view.glyphSize * REL_STAFF_HEIGHT

    protected val staffStepHeight: Float
        get() = view.glyphSize * REL_STAFF_STEP_HEIGHT

    protected val textPaint: Paint
        get() = view.textPaint

    protected val glyphPaint: Paint
        get() = view.glyphPaint

    protected val linePaint: Paint
        get() = view.linePaint

    abstract val relContentWidth: Float

    var relLeftMargin = 0F

    var relRightMargin = 0F

    val leftMargin
        get() = relLeftMargin * glyphSize

    val rightMargin
        get() = relRightMargin * glyphSize

    val relWidth: Float
        get() = relLeftMargin + relContentWidth + relRightMargin

    val width: Float
        get() = /*relContentWidth * glyphSize*/ relWidth * glyphSize

    open fun onDraw(canvas: Canvas, posX: Float, posY: Float) {}

    open fun invalidate() {}
}