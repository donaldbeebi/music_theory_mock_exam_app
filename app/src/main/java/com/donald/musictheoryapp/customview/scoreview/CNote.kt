package com.donald.musictheoryapp.customview.scoreview

import android.graphics.Canvas
import android.util.Log
import com.donald.musictheoryapp.music.musicxml.Clef
import com.donald.musictheoryapp.music.musicxml.Note

internal class CNote(view: ScoreView) : ScoreStamp(view) {

    private var note: Note? = null
    private var clef: Clef? = null

    private var hasNoteArrow = false
    private val sAccidental = SAccidental(view)
    private val sNoteHead = SNoteHead(view)
    private val sFlag = SFlag(view)
    private val sLedgerLines = SLedgerLines(view)
    private val sNoteArrow = SNoteArrow(view)
    override val relContentWidth: Float
        get() = if (hasNoteArrow) {
            sNoteArrow.relContentWidth
        } else {
            sAccidental.relContentWidth + sNoteHead.relContentWidth + sFlag.relContentWidth
        }

    fun setNote(note: Note, clef: Clef) {
        val noteArrow = note.notations?.noteArrow

        this.note = note
        this.clef = clef

        if (noteArrow == null) {
            sAccidental.set(note, clef)
            sNoteHead.set(note, clef)
            sFlag.set(note, clef)
            sLedgerLines.set(note, clef)
            hasNoteArrow = false
        }
        else {
            sNoteArrow.set(note)
            hasNoteArrow = true
        }
    }

    override fun invalidate() {
        val note = note ?: return
        val clef = clef ?: return
        val noteArrow = note.notations?.noteArrow

        if (noteArrow == null) {
            sAccidental.set(note, clef)
            sNoteHead.set(note, clef)
            sFlag.set(note, clef)
            sLedgerLines.set(note, clef)
            hasNoteArrow = false
        }
        else {
            sNoteArrow.set(note)
            hasNoteArrow = true
        }
    }

    override fun onDraw(canvas: Canvas, posX: Float, posY: Float) {
        if (hasNoteArrow) {
            sNoteArrow.onDraw(canvas, posX, posY)
        } else {
            var currentPosX = posX
            sAccidental.onDraw(canvas, currentPosX, posY)
            currentPosX += sAccidental.width
            sNoteHead.onDraw(canvas, currentPosX, posY)
            currentPosX += sNoteHead.width / 2F
            sFlag.onDraw(canvas, currentPosX, posY)
            sLedgerLines.onDraw(canvas, currentPosX, posY)
        }
    }

}