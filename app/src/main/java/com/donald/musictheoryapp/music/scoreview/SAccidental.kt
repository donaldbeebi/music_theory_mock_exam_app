package com.donald.musictheoryapp.music.scoreview

import android.graphics.Canvas
import com.donald.musictheoryapp.music.MusicXML.Clef
import com.donald.musictheoryapp.music.MusicXML.Clef.Companion.noteStaffPosition
import com.donald.musictheoryapp.music.MusicXML.Note
import java.lang.IllegalArgumentException

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
            Note.Accidental.FLAT_FLAT -> {
                glyph = U.ACC_D_FLAT
                accRelWidth = REL_DOUBLE_ACC_WIDTH //noteWidth() * 1.3f
                draws = true
            }
            Note.Accidental.FLAT -> {
                glyph = U.ACC_FLAT
                accRelWidth = REL_SINGLE_ACC_WIDTH //noteWidth() * 0.8f
                draws = true
            }
            Note.Accidental.NATURAL -> {
                glyph = U.ACC_NATURAL
                accRelWidth = REL_SINGLE_ACC_WIDTH //noteWidth() * 0.8f
                draws = true
            }
            Note.Accidental.SHARP -> {
                glyph = U.ACC_SHARP
                accRelWidth = REL_SINGLE_ACC_WIDTH //noteWidth() * 0.8f
                draws = true
            }
            Note.Accidental.SHARP_SHARP -> {
                glyph = U.ACC_D_SHARP
                accRelWidth = REL_SINGLE_ACC_WIDTH//noteWidth() * 0.8f
                draws = true
            }
            Note.Accidental.NULL -> {
                glyph = U.ACC_NATURAL
                accRelWidth = 0F
                draws = false
            }
            else -> {
                glyph = U.ACC_NATURAL
                accRelWidth = 0F
                draws = false
                throw IllegalArgumentException(note.pitch.alter.toString() + " + is not a valid accidental.")
            }
        }
        noteStaffPosition = noteStaffPosition(note, clef)
    }

    override fun onDraw(canvas: Canvas, posX: Float, posY: Float) {
        //val note = note ?: return
        //val clef = clef ?: return
        //set(note, clef)
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