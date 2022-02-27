package com.donald.musictheoryapp.music.scoreview

import android.graphics.Canvas
import com.donald.musictheoryapp.music.MusicXML.Note

internal class SNoteArrow(view: ScoreView) : ScoreStamp(view) {

    var note: Note? = null
    private var draws: Boolean = false
    private var label: String = ""
    private val glyph: Char = U.ARROW_BLACK_DOWN
    override val relContentWidth: Float // IMPLEMENT
        get() = if (draws) {
            REL_NOTE_ARROW_ARROW_WIDTH + REL_NOTE_ARROW_SPACING + REL_NOTE_ARROW_LABEL_WIDTH + REL_MARGIN_AFTER_ARROW
        } else {
            0F
        }

    fun set(note: Note) {
        val noteArrow = note.notations?.noteArrow
        if (noteArrow != null) {
            draws = true
            label = noteArrow.label
        } else {
            draws = false
            label = ""
        }
    }

    override fun onDraw(canvas: Canvas, posX: Float, posY: Float) {
        //val note = note ?: return
        //set(note)
        if (draws) {
            var currentPosX = posX

            val textSize = textPaint.textSize * 0.8F
            val defaultTextSize = textPaint.textSize
            textPaint.textSize = textSize
            canvas.drawText(
                label,
                currentPosX,
                posY - staffStepHeight * 3,
                textPaint
            )
            textPaint.textSize = defaultTextSize

            currentPosX += (REL_NOTE_ARROW_LABEL_WIDTH + REL_NOTE_ARROW_SPACING) * glyphSize

            canvas.drawText(
                glyph.toString(),
                currentPosX,
                posY - staffStepHeight,
                glyphPaint
            )
        }
    }

}