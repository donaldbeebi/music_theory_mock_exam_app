package com.donald.musictheoryapp.customview.scoreview

import android.graphics.Canvas
import com.donald.musictheoryapp.music.musicxml.Accidental
import com.donald.musictheoryapp.music.musicxml.Clef
import com.donald.musictheoryapp.music.musicxml.Clef.Companion.noteStaffPosition
import com.donald.musictheoryapp.music.musicxml.Note

internal class SAccidental(view: ScoreView) : ScoreStamp(view) {

    private var glyph = U.ACC_NATURAL
    private var accRelWidth = 0F
    private var noteStaffPosition = 0
    private var draws = false
    override val relContentWidth: Float
        get() = accRelWidth + if (draws) REL_MARGIN_AFTER_NOTE_ACC else 0F

    var note: Note? = null
    var clef: Clef? = null

    fun set(note: Note, clef: Clef) {
        when (note.accidental) {
            Accidental.FLAT_FLAT -> {
                glyph = U.ACC_D_FLAT
                accRelWidth = REL_DOUBLE_ACC_WIDTH //noteWidth() * 1.3f
                draws = true
            }
            Accidental.FLAT -> {
                glyph = U.ACC_FLAT
                accRelWidth = REL_SINGLE_ACC_WIDTH //noteWidth() * 0.8f
                draws = true
            }
            Accidental.NATURAL -> {
                glyph = U.ACC_NATURAL
                accRelWidth = REL_SINGLE_ACC_WIDTH //noteWidth() * 0.8f
                draws = true
            }
            Accidental.SHARP -> {
                glyph = U.ACC_SHARP
                accRelWidth = REL_SINGLE_ACC_WIDTH //noteWidth() * 0.8f
                draws = true
            }
            Accidental.SHARP_SHARP -> {
                glyph = U.ACC_D_SHARP
                accRelWidth = REL_SINGLE_ACC_WIDTH//noteWidth() * 0.8f
                draws = true
            }
            null -> {
                glyph = U.ACC_NATURAL
                accRelWidth = 0F
                draws = false
            }
        }
        noteStaffPosition = noteStaffPosition(note, clef)
    }

    override fun onDraw(canvas: Canvas, posX: Float, posY: Float) {
        if (draws) {
            val verticalOffsetOnStaff = -noteStaffPosition * staffStepHeight + staffHeight
            canvas.drawText(
                glyph.toString(),
                posX,
                posY + verticalOffsetOnStaff,
                glyphPaint
            )
        }
    }

}