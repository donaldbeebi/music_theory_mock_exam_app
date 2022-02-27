package com.donald.musictheoryapp.music.scoreview

import android.graphics.Canvas

// TODO: REMOVE STAFF HEIGHT
internal class SStaff(private val view: ScoreView) {

    var width = 0F

     fun onDraw(canvas: Canvas, posX: Float, posY: Float) {
        val staffStepHeight = REL_STAFF_STEP_HEIGHT * view.glyphSize
        val linePaint = view.linePaint
        for (i in 0 until NO_OF_LINES_IN_STAFF) {
            canvas.drawLine(
                posX,
                posY + staffStepHeight * 2 * i,
                posX + width,
                posY + staffStepHeight * 2 * i,
                linePaint
            )
        }
    }

}