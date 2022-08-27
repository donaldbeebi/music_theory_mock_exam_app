package com.donald.musictheoryapp.customview.scoreview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import com.donald.musictheoryapp.music.musicxml.Clef.Companion.noteStaffPosition
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.res.ResourcesCompat
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.music.musicxml.*
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.math.abs

// TODO: POTENTIAL BUG WITH PANEL INPUT LISTENER, ACCIDENTALS ARE WORKING BECAUSE THERE ARE EXTRA SPACES, WHAT IF THERE AREN'T?
@SuppressLint("ViewConstructor")
class ScoreView constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    color: androidx.compose.ui.graphics.Color? = null
) : View(context, attrs) {

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
    // TODO: NO LONGER IN USE
    @Deprecated("The use of the graphics layer modifier makes this obsolete")
    var zoomedIn = false
    val intrinsicAspectRatio: Float
        get() {
            val minRelWidth = run {
                setStamps(this)
                var relWidth = 0F
                headStamps.forEach { relWidth += it.relWidth }
                noteStamps.forEach { relWidth += it.relWidth }
                REL_H_PADDING + relWidth + REL_H_PADDING
            }
            val minRelHeight = score?.let { calculateMinRelHeight(it, inputMode) } ?: 0F
            return minRelWidth / minRelHeight
        }

    // paints
    val textPaint = Paint().apply { this.color = color?.toArgb() ?: Color.BLACK }
    val glyphPaint = Paint().apply { this.color = color?.toArgb() ?: Color.BLACK }
    val linePaint = Paint().apply { this.color = color?.toArgb() ?: Color.BLACK }

    // DEBUG
    private val viewRect: RectF = RectF()
    private val debugPaint: Paint = Paint().apply { this.color = Color.RED; style = Paint.Style.STROKE; strokeWidth = 5F }

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

        //linePaint.color = Color.BLACK
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
                setStamps(this)
                var relWidth = 0F
                headStamps.forEach { relWidth += it.relWidth }
                noteStamps.forEach { relWidth += it.relWidth }
                REL_H_PADDING + relWidth + REL_H_PADDING
            }
            val minRelHeight = score?.let { calculateMinRelHeight(it, inputMode) } ?: 0F
            val calculatedGlyphSize = calculateGlyphSize(minRelWidth, minRelHeight, givenWidth, givenHeight)
            //if (zoomedIn) calculatedGlyphSize *= 2
            val maxGlyphSize = if (zoomedIn) MAX_GLYPH_SIZE_ZOOMED else MAX_GLYPH_SIZE
            if (calculatedGlyphSize > maxGlyphSize) {
                glyphSize = maxGlyphSize
                reqRatio = givenWidth / (minRelHeight * glyphSize)
            } else {
                glyphSize = calculatedGlyphSize
                reqRatio = minRelWidth / minRelHeight
            }
        }

        //if (zoomedIn) reqRatio /= 2

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
            stamp.onDraw(canvas, currentPosX + stamp.leftMargin, staffPosY)
            currentPosX += stamp.width
        }
        for (stamp in noteStamps) {
            currentPosX += extraNoteSpacing
            stamp.onDraw(canvas, currentPosX + stamp.leftMargin, staffPosY)
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

    companion object {
        // TODO: IF THE SCORE ONLY HAS A CLEF, IT IS NOT CENTERED
        // TODO: THIS IS BECAUSE IF THE WIDTH IS LONGER THAN THE MIN RATIO, THE BODY RECT IS STRETCHED

        // this function arranges the score stamps
        private fun calculateMinRelHeight(score: Score, inputMode: Boolean = false): Float {
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

        private fun calculateRelTopPadding(score: Score, inputMode: Boolean = false): Float {
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

        private fun scoreAssert(score: Score) {
            assert(
                score.parts.size == 1 && score.parts[0].measures.size == 1 &&
                        score.parts[0].measures[0].attributes!!.clefs.size == 1
            )
        }

        private fun calculateGlyphSize(minRelWidth: Float, minRelHeight: Float, givenWidth: Int, givenHeight: Int): Float {
            val reqRatio = minRelWidth / minRelHeight
            val actualRatio = givenWidth.toFloat() / givenHeight.toFloat()

            return if (actualRatio > reqRatio) {
                givenHeight / minRelHeight
            } else {
                givenWidth / minRelWidth
            }
        }

        private fun setStamps(scoreView: ScoreView) = with(scoreView) {
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

        // TODO: NOT COMPLYING THE SINGLE SOURCE OF TRUTH
        // this disregards input mode
        fun getAspectRatio(score: Score): Float {
            var relWidth = 0F
            val relHeight = calculateMinRelHeight(score, inputMode = false)
            //val headStamps = ArrayList<ScoreStamp>()
            //val noteStamps = ArrayList<ScoreStamp>()
            //val numberOfNotes = 0
            scoreAssert(score)

            val measure = score.parts[0].measures[0]
            val attributes = measure.attributes ?: throw IllegalStateException("No attributes found")
            val clef = attributes.clefs[0]
            val key = attributes.key

            // 1. clef
            if (clef.printObject) {
                relWidth += REL_H_SPACING
                relWidth += REL_CLEF_WIDTH
            }

            // 2. key signature
            if (key.fifths != 0) {
                relWidth += REL_H_SPACING
                relWidth += abs(key.fifths) * REL_SINGLE_ACC_WIDTH
            }

            // 3. notes
            if (measure.notes.isNotEmpty()) {
                measure.notes().forEachIndexed { index, note ->
                    relWidth += getNoteRelWidth(note)
                    if (index == 0) relWidth += REL_H_SPACING
                    if (index != measure.notes.size - 1) relWidth += REL_MARGIN_AFTER_NOTE
                }
                // barline
                relWidth += REL_H_SPACING
            }

            return relWidth / relHeight
        }

        private fun getNoteRelWidth(note: Note) = if (note.notations?.noteArrow == null) {
            getNoteAccRelWidth(note) + getNoteHeadRelWidth(note)
        } else {
            REL_NOTE_ARROW_ARROW_WIDTH + REL_NOTE_ARROW_SPACING + REL_NOTE_ARROW_LABEL_WIDTH + REL_MARGIN_AFTER_ARROW
        }

        private fun getNoteAccRelWidth(note: Note) = when (note.accidental) {
            Accidental.FlatFlat -> REL_DOUBLE_ACC_WIDTH + REL_MARGIN_AFTER_NOTE_ACC
            null -> 0F
            else -> REL_SINGLE_ACC_WIDTH + REL_MARGIN_AFTER_NOTE_ACC
        }

        private fun getNoteHeadRelWidth(note: Note) = when (note.type) {
            Type.Breve, Type.Whole -> REL_HOLLOW_NOTE_WIDTH
            else -> REL_SOLID_NOTE_WIDTH
        }
    }

}