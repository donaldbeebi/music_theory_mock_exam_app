package com.donald.musictheoryapp.customview.scoreview

import android.graphics.Canvas
import com.donald.musictheoryapp.music.musicxml.Clef
import com.donald.musictheoryapp.music.musicxml.Sign

internal class SClef(view: ScoreView) : ScoreStamp(view) {

    private var glyph = U.G_CLEF
    private var clefStaffPosition = 0
    override var relContentWidth: Float = 0F

    fun setClef(clef: Clef) {
        if (clef.printObject) {
            when (clef.sign) {
                Sign.F -> {
                    glyph = U.F_CLEF
                    clefStaffPosition = (clef.line - 1) * 2
                }
                Sign.C -> {
                    glyph = U.C_CLEF
                    clefStaffPosition = (clef.line - 1) * 2
                }
                Sign.G -> {
                    glyph = U.G_CLEF
                    clefStaffPosition = (clef.line - 1) * 2
                }
            }
            relContentWidth = REL_CLEF_WIDTH
        } else {
            relContentWidth = 0F
        }
    }

    override fun onDraw(canvas: Canvas, posX: Float, posY: Float) {
        val verticalOffOnStaff = -clefStaffPosition * staffStepHeight + staffHeight
        canvas.drawText(
            glyph.toString(),
            posX,
            posY + verticalOffOnStaff,
            glyphPaint
        )
    }

}