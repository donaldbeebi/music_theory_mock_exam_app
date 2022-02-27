package com.donald.musictheoryapp.music.scoreview

import android.graphics.Canvas
import com.donald.musictheoryapp.music.MusicXML.Barline.BarStyle.LIGHT_HEAVY
import com.donald.musictheoryapp.music.MusicXML.Barline.BarStyle.LIGHT_LIGHT
import com.donald.musictheoryapp.music.MusicXML.Barline.BarStyle.REGULAR

internal class SBarline(view: ScoreView) : ScoreStamp(view) {

    var style: Int = REGULAR
    override val relContentWidth: Float = 0F

    override fun onDraw(canvas: Canvas, posX: Float, posY: Float) {
        val staffLineHalfStrokeWidth = linePaint.strokeWidth / 2
        when (style) {
            REGULAR -> {
                canvas.drawLine(
                    posX, posY - staffLineHalfStrokeWidth,
                    posX, posY + staffHeight + staffLineHalfStrokeWidth,
                    linePaint
                )
            }
            LIGHT_HEAVY -> {
                run {
                    val originalThickness = linePaint.strokeWidth
                    linePaint.strokeWidth = originalThickness * 2
                    canvas.drawLine(
                        posX - linePaint.strokeWidth / 2, posY - staffLineHalfStrokeWidth,
                        posX - linePaint.strokeWidth / 2, posY + staffHeight + staffLineHalfStrokeWidth,
                        linePaint
                    )
                    linePaint.strokeWidth = originalThickness
                    canvas.drawLine(
                        posX - REL_SPACING_BETWEEN_DOUBLE_BARLINES, posY - staffLineHalfStrokeWidth,
                        posX - REL_SPACING_BETWEEN_DOUBLE_BARLINES, posY + staffHeight + staffLineHalfStrokeWidth,
                        linePaint
                    )
                }
                run {
                    canvas.drawLine(
                        posX - linePaint.strokeWidth / 2, posY - staffLineHalfStrokeWidth,
                        posX - linePaint.strokeWidth / 2, posY + staffHeight + staffLineHalfStrokeWidth,
                        linePaint
                    )
                    canvas.drawLine(
                        posX - REL_SPACING_BETWEEN_DOUBLE_BARLINES, posY - staffLineHalfStrokeWidth,
                        posX - REL_SPACING_BETWEEN_DOUBLE_BARLINES, posY + staffHeight + staffLineHalfStrokeWidth,
                        linePaint
                    )
                }
            }
            LIGHT_LIGHT -> {
                canvas.drawLine(
                    posX - linePaint.strokeWidth / 2, posY - staffLineHalfStrokeWidth,
                    posX - linePaint.strokeWidth / 2, posY + staffHeight + staffLineHalfStrokeWidth,
                    linePaint
                )
                canvas.drawLine(
                    posX - REL_SPACING_BETWEEN_DOUBLE_BARLINES, posY - staffLineHalfStrokeWidth,
                    posX - REL_SPACING_BETWEEN_DOUBLE_BARLINES, posY + staffHeight + staffLineHalfStrokeWidth,
                    linePaint
                )
            }
        }
    }

}