package com.donald.musictheoryapp.customview.scoreview

import android.graphics.Canvas
import com.donald.musictheoryapp.music.musicxml.*
import com.google.android.material.math.MathUtils.floorMod
import java.lang.IllegalArgumentException
import kotlin.math.abs

internal class SSignature(view: ScoreView) : ScoreStamp(view) {

    internal class SSignatureAccidental(view: ScoreView) : ScoreStamp(view) {

        private var glyph: Char
        private var verticalOffsetOnStaff: Float
        private lateinit var currentAccidentalPositions: IntArray
        override val relContentWidth = REL_SINGLE_ACC_WIDTH // ignoring key signatures with double flats

        fun setKey(key: Key, clef: Clef) {
            val sharpSignature = key.fifths > 0
            if (clef.sign == Sign.F && clef.line == 4) {
                // bass
                currentAccidentalPositions = if (sharpSignature) BASS_SHARP_POS else BASS_FLAT_POS
            } else if (clef.sign == Sign.C && clef.line == 3) {
                // tenor
                currentAccidentalPositions = if (sharpSignature) TENOR_SHARP_POS else TENOR_FLAT_POS
            } else if (clef.sign == Sign.C && clef.line == 4) {
                // alto
                currentAccidentalPositions = if (sharpSignature) ALTO_SHARP_POS else ALTO_FLAT_POS
            } else if (clef.sign == Sign.G && clef.line == 2) {
                // treble
                currentAccidentalPositions = if (sharpSignature) TREBLE_SHARP_POS else TREBLE_FLAT_POS
            }
        }

        fun setAccidental(nthAccidental: Int, alter: Int) {
            verticalOffsetOnStaff = (currentAccidentalPositions[nthAccidental] * staffStepHeight)
            glyph = when (alter) {
                -2 -> U.ACC_D_FLAT
                -1 -> U.ACC_FLAT
                0 -> U.ACC_NATURAL
                1 -> U.ACC_SHARP
                2 -> U.ACC_D_SHARP
                else -> throw IllegalArgumentException("$alter + is not a valid alter.")
            }
        }

        override fun onDraw(canvas: Canvas, posX: Float, posY: Float) {
            canvas.drawText(
                glyph.toString(),
                posX,
                posY + glyphSize - verticalOffsetOnStaff,
                glyphPaint
            )
        }

        companion object {
            private val BASS_SHARP_POS = intArrayOf(6, 3, 7, 4, 1, 5, 2)
            private val BASS_FLAT_POS = intArrayOf(2, 5, 1, 4, 0, 3, -1)
            private val TENOR_SHARP_POS = intArrayOf(2, 6, 3, 7, 4, 8, 5)
            private val TENOR_FLAT_POS = intArrayOf(5, 8, 4, 7, 3, 6, 2)
            private val ALTO_SHARP_POS = intArrayOf(7, 4, 8, 5, 2, 6, 3)
            private val ALTO_FLAT_POS = intArrayOf(3, 6, 2, 5, 1, 4, 0)
            private val TREBLE_SHARP_POS = intArrayOf(8, 5, 9, 6, 3, 7, 4)
            private val TREBLE_FLAT_POS = intArrayOf(4, 7, 3, 6, 2, 5, 1)
        }

        init {
            glyph = U.ACC_NATURAL
            verticalOffsetOnStaff = 0f
        }
    }

    private val sAccidental: SSignatureAccidental = SSignatureAccidental(view)
    private lateinit var key: Key
    override val relContentWidth: Float
        // TODO: THIS WAS abs(key.fifths) * REL_SOLID_NOTE_WIDTH
        get() = abs(key.fifths) * REL_SINGLE_ACC_WIDTH

    fun setKey(key: Key, clef: Clef) {
        this.key = key
        sAccidental.setKey(key, clef)
    }

    override fun onDraw(canvas: Canvas, posX: Float, posY: Float) {
        if (key.fifths == 0) return
        val firstStep: Step
        val factor: Int
        if (key.fifths > 0) {
            // sharp signature
            firstStep = Step.valuesInFifths[0]
            factor = 1
        } else {
            // flat signature
            firstStep = Step.valuesInFifths[Step.values().size - 1]
            factor = -1
        }
        // for iterating through the scale
        val signatureStartFifths = key.fifths + 14
        // initializing it to step F (i.e. Fbb, Fb, F, F#, or F##)
        val nthNoteInSignature = floorMod(
            firstStep.fifths - signatureStartFifths, Step.values().size
        )

        // TODO: PERHAPS FIGURE OUT HOW TO PROCEDURALLY CALCULATE THE POSITIONS OF ACCIDENTALS
        var currentPosX = 0
        for (i in 0..6) {
            val currentFifths = floorMod(nthNoteInSignature + i * factor, Step.values().size) +
                    signatureStartFifths
            val currentAlter = currentFifths / Step.values().size - 2
            if (currentAlter == 0) break
            sAccidental.setAccidental(i, currentFifths / Step.values().size - 2)
            sAccidental.onDraw(canvas, posX + currentPosX, posY)
            currentPosX += noteWidth.toInt()
        }
    }

}