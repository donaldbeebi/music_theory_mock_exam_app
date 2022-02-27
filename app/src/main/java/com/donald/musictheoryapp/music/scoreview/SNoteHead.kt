package com.donald.musictheoryapp.music.scoreview

import android.graphics.Canvas
import com.donald.musictheoryapp.music.MusicXML.Clef
import com.donald.musictheoryapp.music.MusicXML.Clef.Companion.noteStaffPosition
import com.donald.musictheoryapp.music.MusicXML.Note
import java.lang.IllegalArgumentException

internal class SNoteHead(view: ScoreView) : ScoreStamp(view) {

    private var glyph = U.NOTE_HEAD_WHOLE
    var note: Note? = null
    var clef: Clef? = null
    private var noteStaffPosition = 0
    override var relContentWidth = 0F
        private set

    fun set(note: Note, clef: Clef) {
        when (note.type) {
            Note.Type.BREVE -> {
                glyph = U.NOTE_HEAD_D_WHOLE
                relContentWidth = REL_HOLLOW_NOTE_WIDTH
            }
            Note.Type.WHOLE -> {
                glyph = U.NOTE_HEAD_WHOLE
                relContentWidth = REL_HOLLOW_NOTE_WIDTH
            }
            Note.Type.HALF -> {
                glyph = U.NOTE_HEAD_HALF
                relContentWidth = REL_SOLID_NOTE_WIDTH
            }
            Note.Type.QUARTER, Note.Type.EIGHTH, Note.Type.SIXTEENTH, Note.Type.THIRTY_SECOND, Note.Type.SIXTY_FOURTH -> {
                glyph = U.NOTE_HEAD_BLACK
                relContentWidth = REL_SOLID_NOTE_WIDTH
            }
            else -> {
                glyph = U.NOTE_HEAD_WHOLE
                noteStaffPosition = 0
                relContentWidth = 0F
                throw IllegalArgumentException("${note.type} is not a valid type.")
            }
        }
        noteStaffPosition = noteStaffPosition(note, clef)
    }

    override fun onDraw(canvas: Canvas, posX: Float, posY: Float) {
        //val note = note ?: return
        //val clef = clef ?: return
        //set(note, clef)
        val verticalOffsetOnStaff = -noteStaffPosition * staffStepHeight + staffHeight
        canvas.drawText(
            glyph.toString(),
            posX,
            posY + verticalOffsetOnStaff,
            glyphPaint
        )
    }

}