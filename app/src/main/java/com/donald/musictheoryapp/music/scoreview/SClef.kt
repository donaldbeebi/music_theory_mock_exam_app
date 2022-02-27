package com.donald.musictheoryapp.music.scoreview

import android.graphics.Canvas
import com.donald.musictheoryapp.music.MusicXML.Clef
import java.lang.IllegalArgumentException

internal class SClef(view: ScoreView) : ScoreStamp(view) {

    private var glyph = U.G_CLEF
    private var clefStaffPosition = 0
    override var relContentWidth: Float = 0F

    fun setClef(clef: Clef) {
        if (clef.printObject) {
            when (clef.sign) {
                Clef.Sign.F -> {
                    glyph = U.F_CLEF
                    clefStaffPosition = (clef.line - 1) * 2
                }
                Clef.Sign.C -> {
                    glyph = U.C_CLEF
                    clefStaffPosition = (clef.line - 1) * 2
                }
                Clef.Sign.G -> {
                    glyph = U.G_CLEF
                    clefStaffPosition = (clef.line - 1) * 2
                }
                else -> {
                    glyph = U.G_CLEF
                    clefStaffPosition = 0
                    throw IllegalArgumentException("$clef is not valid.")
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