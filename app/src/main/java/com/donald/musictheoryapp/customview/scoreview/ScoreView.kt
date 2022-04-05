package com.donald.musictheoryapp.customview.scoreview

import android.content.Context
import android.graphics.*
import com.donald.musictheoryapp.music.musicxml.Clef.Companion.noteStaffPosition
import kotlin.jvm.JvmOverloads
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.music.musicxml.*
import java.util.*

// TODO: POTENTIAL BUG WITH PANEL INPUT LISTENER, ACCIDENTALS ARE WORKING BECAUSE THERE ARE EXTRA SPACES, WHAT IF THERE AREN'T?
class ScoreView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) : View(context, attrs) {

    var glyphSize = 0F
        private set

    private var fixedRatio = 1F
    private var hasFixedRatio = false
    private var topRelPadding = 0F
    private var headStamps = ArrayList<ScoreStamp>()
    private var noteStamps = ArrayList<ScoreStamp>()

    private var topPadding = 0F
    private var hPadding = 0F
    private var hSpacing = 0F
    private var extraNoteSpacing = 0F
    private var numberOfNotes = 0

    var inputMode = false
    var zoomedIn = false

    // paints
    val textPaint: Paint = Paint()
    val glyphPaint: Paint = Paint()
    val linePaint: Paint = Paint()

    // DEBUG
    private val viewRect: RectF = RectF()
    private val debugPaint: Paint = Paint().apply { color = Color.RED; style = Paint.Style.STROKE; strokeWidth = 5F }

    // data
    private var score: Score? = null

    // stamps
    private val sStaff = SStaff(this)


    private fun calculateSizes() {
        topPadding = if (hasFixedRatio) {
            val givenTopPadding = (height - REL_STAFF_HEIGHT * glyphSize) / 2
            (glyphSize * topRelPadding).coerceAtLeast(givenTopPadding)
        } else {
            glyphSize * topRelPadding
        }
        hPadding = REL_H_PADDING * glyphSize
        hSpacing = REL_H_SPACING * glyphSize
        extraNoteSpacing = run {
            var minReqWidth = 0F
            var headWidth = 0F
            headStamps.forEach { minReqWidth += it.width; headWidth += it.width }
            noteStamps.forEach { minReqWidth += it.width }
            minReqWidth += hPadding * 2
            (width - minReqWidth) / (numberOfNotes + 1)
        }
        //extraNoteSpacing = 0F

        sStaff.width = width - hPadding * 2

        /*
		 * PAINTS
		 */
        textPaint.textSize = glyphSize / 2F
        textPaint.typeface = ResourcesCompat.getFont(context, R.font.bravura)

        glyphPaint.style = Paint.Style.FILL
        glyphPaint.textSize = glyphSize
        glyphPaint.typeface = ResourcesCompat.getFont(context, R.font.bravura)

        linePaint.color = Color.BLACK
        linePaint.style = Paint.Style.FILL
        linePaint.strokeWidth = REL_LINE_WIDTH * glyphSize

        // DEBUG
        debugPaint.color = Color.RED
        debugPaint.style = Paint.Style.STROKE
        debugPaint.strokeWidth = 5F
    }

    fun setScore(score: Score) {
        this.score = score
        requireNotNull(score.parts[0].measures[0].attributes) {
            "The first measure of the score does not have attributes."
        }
        postInvalidate()
    }

    fun setFixedRatio(fixedRatio: Float) {
        this.fixedRatio = fixedRatio
        hasFixedRatio = true
    }

    fun removeFixedRatio() {
        hasFixedRatio = false
    }

    override fun invalidate() {
        noteStamps.forEach {
            it.invalidate()
        }
        super.invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (score == null) {
            setMeasuredDimension(0, 0)
            return
        }

        // treating unspecified mode as max length
        // ignoring the difference between exactly and at most
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val givenWidth = if (widthMode != MeasureSpec.UNSPECIFIED) MeasureSpec.getSize(widthMeasureSpec) else Int.MAX_VALUE
        val givenHeight = if (heightMode != MeasureSpec.UNSPECIFIED) MeasureSpec.getSize(heightMeasureSpec) else Int.MAX_VALUE
        val givenRatio = givenWidth.toFloat() / givenHeight.toFloat()

        // THIS NEEDS TO BE CALCULATED AGAIN WHEN INVALIDATED WITHOUT onMeasure BEING CALLED
        // THE VIEW CHANGES SIZE WHEN THE SCORE CHANGES, THAT'S THE PROBLEM
        // TODO: NO NEED TO CALCULATE HERE?
        topRelPadding = score?.let { calculateRelTopPadding(it, inputMode) } ?: 0F
        var reqRatio: Float
        run {
            val minRelWidth = run {
                setStamps()
                var relWidth = 0F
                headStamps.forEach { relWidth += it.relWidth }
                noteStamps.forEach { relWidth += it.relWidth }
                REL_H_PADDING + relWidth + REL_H_PADDING
            }
            val minRelHeight = score?.let { calculateMinRelHeight(it, inputMode) } ?: 0F
            val calculatedGlyphSize = calculateGlyphSize(minRelWidth, minRelHeight, givenWidth, givenHeight)
            val maxGlyphSize = if (zoomedIn) MAX_GLYPH_SIZE_ZOOMED else MAX_GLYPH_SIZE
            if (calculatedGlyphSize > maxGlyphSize) {
                glyphSize = maxGlyphSize
                reqRatio = givenWidth / (minRelHeight * glyphSize)
            } else {
                glyphSize = calculatedGlyphSize
                reqRatio = minRelWidth / minRelHeight
            }
        }

        if (hasFixedRatio) {
            if (givenRatio > fixedRatio) {
                // wider than needed
                setMeasuredDimension(
                    (givenHeight.toFloat() * fixedRatio).toInt(),
                    givenHeight
                )
            }
            else {
                // narrower than needed
                setMeasuredDimension(
                    givenWidth,
                    (givenWidth.toFloat() / fixedRatio).toInt()
                )
            }
        }
        else {
            if (givenRatio > reqRatio) {// || (widthMode == MeasureSpec.UNSPECIFIED && heightMode != MeasureSpec.UNSPECIFIED)) {
                // wider than needed or width is unlimited
                setMeasuredDimension(
                    givenWidth,
                    givenHeight
                )
            } else if (givenRatio <= reqRatio) {// || (widthMode != MeasureSpec.UNSPECIFIED && heightMode == MeasureSpec.UNSPECIFIED)) {
                // narrower than needed or height is unlimited
                setMeasuredDimension(
                    givenWidth,
                    (givenWidth.toFloat() / reqRatio).toInt()
                )
            } else {
                TODO(
                    """
                        View measurement conditions not implemented:
                        widthMeasureSpec = ${MeasureSpec.toString(widthMeasureSpec)}
                        heightMeasureSpec = ${MeasureSpec.toString(heightMeasureSpec)}
                        givenRatio = $givenRatio
                        reqRatio = $reqRatio
                    """.trimIndent()
                )
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        calculateSizes()

        // only drawing one measure
        val staffPosY = topPadding

        // 1. drawing the staff line
        sStaff.onDraw(canvas, hPadding, staffPosY)

        // 2. everything on the line
        var currentPosX = hPadding
        for (stamp in headStamps) {
            stamp.onDraw(canvas, currentPosX, staffPosY)
            currentPosX += stamp.width
        }
        for (stamp in noteStamps) {
            //canvas.drawLine(currentPosX, staffPosY, currentPosX + extraNoteSpacing, staffPosY, Paint().apply { color = Color.RED; strokeWidth = 5F; style = Paint.Style.STROKE })
            currentPosX += extraNoteSpacing
            stamp.onDraw(canvas, currentPosX, staffPosY)
            currentPosX += stamp.width
        }

        val debug = false
        if (debug) {
            viewRect.left = 0F
            viewRect.top = 0F
            viewRect.right = width.toFloat()
            viewRect.bottom = height.toFloat()
            canvas.drawRect(viewRect, debugPaint)
        }
    }

    private fun setStamps() {
        headStamps.clear()
        noteStamps.clear()
        numberOfNotes = 0
        val score = this.score ?: throw IllegalStateException("Score is not set")
        scoreAssert(score)

        val measure = score.parts[0].measures[0]
        val attributes = measure.attributes ?: throw IllegalStateException("No attributes found")
        val clef = attributes.clefs[0]
        val key = attributes.key

        // 1. clef
        if (clef.printObject) {
            headStamps += SClef(this).apply {
                setClef(clef)
                relLeftMargin = REL_H_SPACING
            }
        }

        // 2. key signature
        if (key.fifths != 0) {
            headStamps += SSignature(this).apply {
                setKey(key, clef)
                relLeftMargin = REL_H_SPACING
            }
        }

        // 3. notes
        if (measure.notes.isNotEmpty()) {
            measure.notes().forEachIndexed { index, note ->
                noteStamps += CNote(this).apply {
                    setNote(note, clef)
                    if (index == 0) relLeftMargin = REL_H_SPACING
                    if (index != measure.notes.size - 1) relRightMargin = REL_MARGIN_AFTER_NOTE
                }
                numberOfNotes++
            }
            noteStamps += SBarline(this).apply {
                style = measure.barline?.barStyle ?: Barline.BarStyle.REGULAR
                relLeftMargin = REL_H_SPACING
            }
        }
    }

    private companion object {
        // TODO: IF THE SCORE ONLY HAS A CLEF, IT IS NOT CENTERED
        // TODO: THIS IS BECAUSE IF THE WIDTH IS LONGER THAN THE MIN RATIO, THE BODY RECT IS STRETCHED

        // this function arranges the score stamps
        fun calculateMinRelHeight(score: Score, inputMode: Boolean = false): Float {
            scoreAssert(score)

            var highestNotePos = if (inputMode) INPUT_MODE_TOP_PADDING_LINE_POS else TOP_PADDING_LINE_POS
            var lowestNotePos = if (inputMode) INPUT_MODE_BOT_PADDING_LINE_POS else BOT_PADDING_LINE_POS
            var hasNoteArrow = false

            val measure = score.parts[0].measures[0]
            val clef = measure.attributes?.clefs?.get(0) ?: TODO("CLEF CASCADING NOT IMPLEMENTED")

            measure.notes.forEach { note ->
                val notePosition = noteStaffPosition(note, clef)
                if (notePosition > highestNotePos) highestNotePos = notePosition
                else if (notePosition < lowestNotePos) lowestNotePos = notePosition
                if (!hasNoteArrow) hasNoteArrow = note.notations?.noteArrow != null
            }

            var topRelPadding = (highestNotePos.toFloat() + 4 - 8) * REL_STAFF_STEP_HEIGHT
            if (hasNoteArrow) topRelPadding = topRelPadding.coerceAtLeast(REL_NOTE_ARROW_REL_HEAD_ROOM)
            val bottomRelPadding = (-(lowestNotePos - 4)).toFloat() * REL_STAFF_STEP_HEIGHT

            return topRelPadding + REL_STAFF_HEIGHT + bottomRelPadding
        }

        fun calculateRelTopPadding(score: Score, inputMode: Boolean = false): Float {
            scoreAssert(score)

            val measure = score.parts[0].measures[0]
            val attributes = measure.attributes ?: throw IllegalStateException()
            val clef = attributes.clefs[0]
            var highestNotePos = if (inputMode) INPUT_MODE_TOP_PADDING_LINE_POS else TOP_PADDING_LINE_POS
            var hasNoteArrowNotation = false

            measure.notes.forEach { note ->
                val notePos = noteStaffPosition(note, clef)
                if (notePos > highestNotePos) highestNotePos = notePos
                if (!hasNoteArrowNotation) hasNoteArrowNotation = note.notations?.noteArrow != null
            }

            var relTopPadding = (highestNotePos.toFloat() + 4 - 8) * REL_STAFF_STEP_HEIGHT
            if (hasNoteArrowNotation) {
                relTopPadding = relTopPadding.coerceAtLeast(REL_NOTE_ARROW_REL_HEAD_ROOM)
            }
            return relTopPadding
        }

        fun scoreAssert(score: Score) {
            assert(
                score.parts.size == 1 && score.parts[0].measures.size == 1 &&
                        score.parts[0].measures[0].attributes!!.clefs.size == 1
            )
        }

        fun calculateGlyphSize(minRelWidth: Float, minRelHeight: Float, givenWidth: Int, givenHeight: Int): Float {
            val reqRatio = minRelWidth / minRelHeight
            val actualRatio = givenWidth.toFloat() / givenHeight.toFloat()

            return if (actualRatio > reqRatio) {
                givenHeight / minRelHeight
            } else {
                givenWidth / minRelWidth
            }
        }

    }

    /*
    fun onDrawOld(canvas: Canvas) {
        calculateSizes()

        if (score == null) return
        // TODO: CONSIDER MULTIPLE PARTS AND MULTIPLE CLEFS
        val currentPart = score!!.parts[0]!!
        val measures = currentPart.measures
        val currentClef = measures[0]!!.attributes!!.clefs[0]!!
        sClef.setClef(currentClef)
        val currentKey = measures[0]!!.attributes!!.key
        sSignature.setKey(currentKey, currentClef)
        setAccidentalMemory(measures[0]!!.attributes!!.key)

        // TODO: FIX WHEN MEASURES_PER_LINE IS MORE THAN 1, AND THERE IS ONLY 1 MEASURE
        val measuresPerLine = 1
        val numberOfLines = (measures.size + measuresPerLine - 1) / measuresPerLine
        var firstMeasureIndex = 0
        //canvas.drawColor(Color.WHITE);

        // TODO: WHAT IF DIVISION CHANGED MID-WAY
        for (lineIndex in 0 until numberOfLines) {
            // TODO: RIGHT NOW GLYPH SIZE IS THE SPACING BETWEEN STAVES, STORE IT AS A PROPER VARIABLE
            val currentStaffPosY = topPadding + (glyphSize + staffHeight()) * lineIndex

            //sStaff.setWidth(staffWidth)

            currentHeaderRect.apply {
                left = leftPadding
                top = currentStaffPosY
                right = left +
                        (if (currentClef.printObject || currentKey.fifths != 0) hSpacing else 0F) +
                        (if (currentClef.printObject) clefWidth + hSpacing else 0f) +
                        (if (currentKey.fifths != 0)  accWidth * abs(currentKey.fifths) + hSpacing else 0f)
                //sSignature.width
                bottom = currentHeaderRect.top + staffHeight()
            }

            currentBodyRect.apply {
                left = currentHeaderRect.right
                top = currentStaffPosY
                right = leftPadding + staffWidth
                bottom = currentBodyRect.top + staffHeight()
            }

            // 1. drawing the staff lines
            sStaff.onDraw(canvas, leftPadding, currentStaffPosY)

            // 2. drawing the clef
            if (currentClef.printObject) {
                sClef.onDraw(canvas, currentHeaderRect.left + hSpacing, currentHeaderRect.top)
            }

            // 3. drawing the signature
            sSignature.onDraw(
                canvas,
                currentHeaderRect.left +
                        (if (currentClef.printObject) hSpacing + clefWidth else 0F) + hSpacing,
                currentHeaderRect.top
            )

            val numberOfMeasuresThisLine = measuresPerLine.coerceAtMost(measures.size - firstMeasureIndex)
            var currentPosX = currentBodyRect.left
            val currentPosY = currentBodyRect.bottom

            for (measureIndex in 0 until numberOfMeasuresThisLine) {
                val measure = measures[firstMeasureIndex + measureIndex]
                if (measure!!.notes.isEmpty()) continue
                val notes = measure.notes
                val extraNoteSpacing = (width - (minRelWidth * glyphSize)) / (notes.size + 1) // TODO: DISREGARDING CHORD NOTES
                measureAlters.clear()
                /*
				 * VARIABLES
				 */
                // TODO: HANDLE WITHOUT NOTES
                // TODO: REDUCE THE SPACE AT THE BEGINNING AND END OF THE NOTES
                var totalDuration = 0
                var shortestDuration = Int.MAX_VALUE
                for (note in notes) {
                    if (note!!.staff == 1 && !note.chord) {
                        val duration = note.duration
                        if (duration < shortestDuration) shortestDuration = duration
                        totalDuration += duration
                    }
                }
                val spacingPerDuration = (currentBodyRect.width() / (totalDuration + shortestDuration)) / numberOfMeasuresThisLine

                /*
				 * DRAWING
				 */
                // 4. drawing the notes
                //currentPosX += shortestDuration * spacingPerDuration

                notes.indices.forEach { noteIndex ->
                    val note = notes[noteIndex]!!

                    if (noteIndex != 0 && !notes[noteIndex]!!.chord) {
                        currentPosX +=  noteWidth
                    }

                    currentPosX += extraNoteSpacing + glyphSize *
                            if (!note.printObject() && note.notations?.noteArrow != null) REL_NOTE_SINGLE_ACC_SPACING
                            else if (note.accidental() == Note.Accidental.NULL) REL_NOTE_NO_ACC_SPACING
                            else if (note.accidental == Note.Accidental.FLAT_FLAT) REL_NOTE_DOUBLE_ACC_SPACING
                            else REL_NOTE_SINGLE_ACC_SPACING

                    //currentPosX += accWidth(note, glyphSize)

                    if (note.staff == 1 && note.printObject()) {
                        // TODO: CHORD NOTES' LEDGER LINES ARE BEING DRAWN TWICE
                        glyphPaint.color = note.color()

                        // a. note head
                        if (note.pitch != null && note.type != Note.Type.NULL) {
                            sNoteHead.run {
                                set(note, currentClef)
                                onDraw(canvas, currentPosX, currentPosY)
                            }
                            if (note.type > Note.Type.QUARTER) {
                                sFlag.run {
                                    set(note, currentClef)
                                    onDraw(canvas, currentPosX, currentPosY)
                                }
                                sStem.run {
                                    setNote(note, currentClef)
                                    onDraw(canvas, currentPosX, currentPosY)
                                }
                            }
                            // b. accidental
                            if (note.accidental != Note.Accidental.NULL) {
                                sAccidental.run {
                                    set(note, currentClef)
                                    onDraw(canvas, currentPosX, currentPosY)
                                }
                            }
                            // c. ledger lines
                            sLedgerLines.run {
                                set(note, currentClef)
                                onDraw(canvas, currentPosX, currentPosY)
                            }
                        }
                    }
                    // d. notations
                    if (note.notations != null) {
                        if (note.notations.noteArrow != null) {
                            sNoteArrow.run {
                                setLabel(note.notations.noteArrow.label)
                                onDraw(canvas, currentPosX, currentPosY)
                            }
                        }
                    }
                }

                //glyphPaint.color = Color.BLACK

                // 5. draw the bar line
                // adjust for the last note
                currentPosX += noteWidth + hSpacing + extraNoteSpacing
                if (measure.barline != null) {
                    sBarline.style = measure.barline.barStyle
                } else {
                    sBarline.style = Barline.BarStyle.REGULAR
                }
                sBarline.onDraw(canvas, currentPosX, currentPosY)

                viewRect.left = 0F
                viewRect.top = 0F
                viewRect.right = width.toFloat()
                viewRect.bottom = height.toFloat()
                canvas.drawRect(viewRect, debugPaint)


                canvas.drawRect(currentHeaderRect, debugPaint)
                canvas.drawRect(currentBodyRect, debugPaint)
            }
            firstMeasureIndex += measuresPerLine
        }
    }

     */

}