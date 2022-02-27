package com.donald.musictheoryapp.music.scoreview

import android.graphics.Canvas
import com.donald.musictheoryapp.music.MusicXML.Clef
import com.donald.musictheoryapp.music.MusicXML.Clef.Companion.noteStaffPosition
import com.donald.musictheoryapp.music.MusicXML.Note

internal class SStem(view: ScoreView) : ScoreStamp(view) {

    private var horizontalOffsetOnStaff = 0F
    private var startPosYOffset = 0F
    private var endPosYOffset = 0F
    override val relContentWidth = 0F

    fun setNote(note: Note?, clef: Clef?) {
        run {
            val noteStaffPosition = noteStaffPosition(note!!, clef!!)
            startPosYOffset = -noteStaffPosition * staffStepHeight
            if (noteStaffPosition >= MIDDLE_LINE_STAFF_POS) {
                // down
                horizontalOffsetOnStaff = -noteWidth / 2
                endPosYOffset = startPosYOffset + staffStepHeight * 7
            } else {
                // up
                horizontalOffsetOnStaff = noteWidth / 2
                endPosYOffset = startPosYOffset - staffStepHeight * 7
            }
        }
    }

    override fun onDraw(canvas: Canvas, posX: Float, posY: Float) {
        canvas.drawLine(
            posX + horizontalOffsetOnStaff, posY + startPosYOffset,
            posX + horizontalOffsetOnStaff, posY + endPosYOffset,
            linePaint
        )
    }
}