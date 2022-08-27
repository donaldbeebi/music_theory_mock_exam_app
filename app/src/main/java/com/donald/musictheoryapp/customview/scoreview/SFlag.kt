package com.donald.musictheoryapp.customview.scoreview

import android.graphics.Canvas
import com.donald.musictheoryapp.music.musicxml.*

internal class SFlag(view: ScoreView) : ScoreStamp(view) {

    private var downOffset = 0F
    private var glyph: Char? = null
    var note: Note? = null
    var clef: Clef? = null
    private var verticalOffsetOnStaff = 0F
    private var horizontalOffsetOnStaff = 0F
    override val relContentWidth: Float = 0F // TODO: IMPLEMENT THIS

    fun set(note: Note, clef: Clef) {
        val notePosition = note.pitch?.absStep ?: throw IllegalArgumentException("Note has not pitch")
        val noteStaffPosition = notePosition - clef.sign.baseAbsStep +
                (clef.line - 1) * 2
        if (noteStaffPosition >= MIDDLE_LINE_STAFF_POS) {
            // down
            horizontalOffsetOnStaff = -noteWidth / 2
            downOffset = staffStepHeight * 14
            glyph = when (note.type) {
                Type.Eighth -> U.FLAG_8TH_DOWN
                Type.Sixteenth -> U.FLAG_16TH_DOWN
                Type.ThirtySecond -> U.FLAG_32TH_DOWN
                Type.SixtyFourth -> U.FLAG_64TH_DOWN
                else -> null
            }
        } else {
            // up
            horizontalOffsetOnStaff = noteWidth / 2
            downOffset = 0f
            glyph = when (note.type) {
                Type.Eighth -> U.FLAG_8TH_UP
                Type.Sixteenth -> U.FLAG_16TH_UP
                Type.ThirtySecond -> U.FLAG_32TH_UP
                Type.SixtyFourth -> U.FLAG_64TH_UP
                else -> null
            }
        }
        verticalOffsetOnStaff = -noteStaffPosition * staffStepHeight - 7 * staffStepHeight
    }

    override fun onDraw(canvas: Canvas, posX: Float, posY: Float) {
        val glyph = glyph ?: return
        //val note = note ?: return
        //val clef = clef ?: return
        //set(note, clef)
        canvas.drawText(
            glyph.toString(),
            posX + horizontalOffsetOnStaff,  //posY + fontSize() - m_VerticalOffsetOnStaff + m_DownOffset,
            posY + verticalOffsetOnStaff + downOffset,
            glyphPaint
        )
    }
}