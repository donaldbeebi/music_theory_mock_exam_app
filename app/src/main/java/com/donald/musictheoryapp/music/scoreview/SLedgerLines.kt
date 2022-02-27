package com.donald.musictheoryapp.music.scoreview

import android.graphics.Canvas
import com.donald.musictheoryapp.music.MusicXML.Clef
import com.donald.musictheoryapp.music.MusicXML.Clef.Companion.noteStaffPosition
import com.donald.musictheoryapp.music.MusicXML.Note

internal class SLedgerLines(view: ScoreView) : ScoreStamp(view) {

    var note: Note? = null
    var clef: Clef? = null
    private var numberOfLines = 0
    private var aboveStaff = false
    private var draws = false
    override val relContentWidth = REL_LEDGER_LINE_WIDTH

    fun set(note: Note, clef: Clef) {
        val staffPosition = noteStaffPosition(note, clef)
        if (staffPosition > TOP_LINE_STAFF_BOS + 1) {
            aboveStaff = true
            numberOfLines = (staffPosition - TOP_LINE_STAFF_BOS) / 2
            draws = true
        } else if (staffPosition < BASE_LINE_STAFF_POS - 1) {
            aboveStaff = false
            numberOfLines = (BASE_LINE_STAFF_POS - staffPosition) / 2
            draws = true
        } else {
            draws = false
        }
    }

    override fun onDraw(canvas: Canvas, posX: Float, posY: Float) {
        //val note = note ?: return
        //val clef = clef ?: return
        //set(note, clef)
        if (draws) {
            val factor: Float = if (aboveStaff) (-1).toFloat() else 1.toFloat()
            var currentPosY = ((if (aboveStaff) 0F else staffHeight) + posY + staffStepHeight * 2 * factor)
            for (i in 0 until numberOfLines) {
                // TODO: DRAWING ALIGNMENT IS CHANGED, UPDATE THIS
                canvas.drawLine(
                    posX - width / 2, currentPosY,
                    posX + width / 2, currentPosY,
                    linePaint
                )
                currentPosY += staffStepHeight * 2 * factor
            }
        }
    }
}