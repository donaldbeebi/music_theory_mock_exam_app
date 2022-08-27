package com.donald.musictheoryapp.customview.scoreview

import android.graphics.Canvas
import com.donald.musictheoryapp.music.musicxml.Clef
import com.donald.musictheoryapp.music.musicxml.Clef.Companion.noteStaffPosition
import com.donald.musictheoryapp.music.musicxml.Note
import com.donald.musictheoryapp.music.musicxml.Type
import java.lang.IllegalArgumentException

internal class  SNoteHead(view: ScoreView) : ScoreStamp(view) {

    private var glyph = U.NOTE_HEAD_WHOLE
    var note: Note? = null
    var clef: Clef? = null
    private var noteStaffPosition = 0
    override var relContentWidth = 0F
        private set

    fun set(note: Note, clef: Clef) {
        when (note.type) {
            Type.Breve -> {
                glyph = U.NOTE_HEAD_D_WHOLE
                relContentWidth = REL_HOLLOW_NOTE_WIDTH
            }
            Type.Whole -> {
                glyph = U.NOTE_HEAD_WHOLE
                relContentWidth = REL_HOLLOW_NOTE_WIDTH
            }
            Type.Half -> {
                glyph = U.NOTE_HEAD_HALF
                relContentWidth = REL_SOLID_NOTE_WIDTH
            }
            Type.Quarter, Type.Eighth, Type.Sixteenth, Type.ThirtySecond, Type.SixtyFourth -> {
                glyph = U.NOTE_HEAD_BLACK
                relContentWidth = REL_SOLID_NOTE_WIDTH
            }
            else -> {
                // this might be a kotlin bug
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