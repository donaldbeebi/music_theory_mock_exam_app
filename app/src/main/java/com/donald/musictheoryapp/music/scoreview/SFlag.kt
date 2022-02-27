package com.donald.musictheoryapp.music.scoreview

import android.graphics.Canvas
import com.donald.musictheoryapp.music.MusicXML.Clef
import com.donald.musictheoryapp.music.MusicXML.Note
import com.donald.musictheoryapp.music.MusicXML.Pitch

internal class SFlag(view: ScoreView) : ScoreStamp(view) {

    private var downOffset = 0F
    private var glyph: Char? = null
    var note: Note? = null
    var clef: Clef? = null
    private var verticalOffsetOnStaff = 0F
    private var horizontalOffsetOnStaff = 0F
    override val relContentWidth: Float = 0F // TODO: IMPLEMENT THIS

    fun set(note: Note, clef: Clef) {
        val notePosition = note.pitch.step + note.pitch.octave * Pitch.Step.NO_OF_STEPS
        val noteStaffPosition = notePosition - Clef.Sign.BASE_NOTE_POS_BY_SIGN[clef.sign] +
                (clef.line - 1) * 2
        if (noteStaffPosition >= MIDDLE_LINE_STAFF_POS) {
            // down
            horizontalOffsetOnStaff = -noteWidth / 2
            downOffset = staffStepHeight * 14
            glyph = when (note.type) {
                Note.Type.EIGHTH -> U.FLAG_8TH_DOWN
                Note.Type.SIXTEENTH -> U.FLAG_16TH_DOWN
                Note.Type.THIRTY_SECOND -> U.FLAG_32TH_DOWN
                Note.Type.SIXTY_FOURTH -> U.FLAG_64TH_DOWN
                else -> null
            }
        } else {
            // up
            horizontalOffsetOnStaff = noteWidth / 2
            downOffset = 0f
            glyph = when (note.type) {
                Note.Type.EIGHTH -> U.FLAG_8TH_UP
                Note.Type.SIXTEENTH -> U.FLAG_16TH_UP
                Note.Type.THIRTY_SECOND -> U.FLAG_32TH_UP
                Note.Type.SIXTY_FOURTH -> U.FLAG_64TH_UP
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