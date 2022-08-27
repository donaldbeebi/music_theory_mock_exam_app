package com.donald.musictheoryapp.listener

import android.util.Log
import com.donald.musictheoryapp.customview.scoreview.ScoreView
import android.view.View.OnTouchListener
import android.view.MotionEvent
import android.view.View
import com.donald.musictheoryapp.music.musicxml.*
import com.donald.musictheoryapp.music.musicxml.Note
import com.google.android.material.math.MathUtils.floorMod
import java.util.*
import kotlin.math.abs

class PanelOnTouchListener(scoreView: ScoreView, score: Score) : OnTouchListener {

    private val inputNote: Note
    private val scoreView: ScoreView
    private var initialY = 0f
    private var initialAbsStep = 0
    private val firstNoteAbsStep: Int
    private val firstNoteAlter: Int
    private val measureAlters: IntArray
    private val clef: Clef

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        return when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                initialY = event.y
                initialAbsStep = inputNote.pitch!!.absStep
                true
            }
            MotionEvent.ACTION_UP -> {
                Log.d("inputNote alter before", inputNote.pitch!!.alter.toString())
                val currentY = event.y
                val distance = currentY - initialY
                if (event.eventTime - event.downTime < 200.0 && abs(distance.toInt() / 60) < 1) {
                    // next accidental
                    val accidental = inputNote.accidental.next()
                    inputNote.accidental = accidental
                    if (accidental == null) {
                        if (inputNote.pitch.absStep == firstNoteAbsStep) {
                            inputNote.pitch.alter = firstNoteAlter
                        } else {
                            inputNote.pitch.alter = measureAlters[inputNote.pitch.step.ordinal]
                        }
                    } else {
                        inputNote.pitch.alter = accidental.alter
                    }
                    scoreView.invalidate()
                    //scoreView.requestLayout();
                    Log.d("inputNote alter after", inputNote.pitch.alter.toString())
                }
                true
            }
            MotionEvent.ACTION_MOVE -> {
                val currentY = event.y
                val distance = currentY - initialY
                val absStepUpperLimit = clef.sign.baseAbsStep + NOTE_POS_UPPER_LIMIT - (clef.line - 1) * 2
                val absStepLowerLimit = clef.sign.baseAbsStep + NOTE_POS_LOWER_LIMIT - (clef.line - 1) * 2
                val newAbsStep = (initialAbsStep - distance.toInt() / 60).coerceAtLeast(absStepLowerLimit).coerceAtMost(absStepUpperLimit)
                inputNote.pitch?.step = Step.values()[floorMod(newAbsStep, Step.values().size)]
                inputNote.pitch?.octave = newAbsStep / Step.values().size
                scoreView.invalidate()
                true
            }
            else -> {
                view.performClick()
            }
        }
    }

    init {
        inputNote = score.parts()[0].measures()[0].notes()[1]
        this.scoreView = scoreView
        //question.answer.userAnswer = inputNote;
        val measure = score.parts()[0].measures()[0]
        firstNoteAbsStep = measure.notes()[0].pitch!!.absStep
        firstNoteAlter = measure.notes()[0].pitch!!.alter
        clef = measure.attributes!!.clefs[0]
        measureAlters = IntArray(Step.values().size)
        Arrays.fill(measureAlters, 0)
        if (measure.attributes.key.fifths != 0) {
            var i = 0
            var currentStep: Step
            val alter: Int
            val stepItDisplacement: Int
            if (measure.attributes.key.fifths > 0) {
                // sharping from F
                currentStep = Step.F
                alter = 1
                stepItDisplacement = 4
            } else {
                // flatting from B
                currentStep = Step.B
                alter = -1
                stepItDisplacement = -3
            }
            val fifths = abs(measure.attributes.key.fifths)
            while (i < fifths) {
                measureAlters[currentStep.ordinal] += alter
                i++
                currentStep = Step.values()[floorMod(currentStep.ordinal + stepItDisplacement, Step.values().size)]
            }
        }
    }

    companion object {

        private const val NOTE_POS_UPPER_LIMIT = 15
        private const val NOTE_POS_LOWER_LIMIT = -7

        val accidentalCycle = arrayOf(
            null,
            Accidental.FlatFlat,
            Accidental.Flat,
            Accidental.Natural,
            Accidental.Sharp,
            Accidental.SharpSharp
        )

    }

    fun Accidental?.next(step: Int = 1): Accidental? {
        val state = this?.let { ordinal + 1 } ?: 0
        return accidentalCycle[floorMod((state + step), accidentalCycle.size)]
    }

    fun Accidental?.prev(step: Int = 1): Accidental? {
        val state = this?.let { ordinal + 1 } ?: 0
        return accidentalCycle[floorMod((state - step), accidentalCycle.size)]
    }

}