package com.donald.musictheoryapp.customview.scoreview

const val BASE_LINE_STAFF_POS = 0
const val MIDDLE_LINE_STAFF_POS = 4
const val TOP_LINE_STAFF_BOS = 8
const val NO_OF_LINES_IN_STAFF = 5
const val TOP_PADDING_LINE_POS = 9
const val BOT_PADDING_LINE_POS = -1
const val INPUT_MODE_TOP_PADDING_LINE_POS = 18
const val INPUT_MODE_BOT_PADDING_LINE_POS = -10

const val REL_SOLID_NOTE_WIDTH = 0.29F // approximately 0.3
const val REL_HOLLOW_NOTE_WIDTH = 0.415F
const val REL_SINGLE_ACC_WIDTH = 0.3F
const val REL_DOUBLE_ACC_WIDTH = 0.55F
const val REL_NOTE_NO_ACC_SPACING = 0.3F
const val REL_NOTE_SINGLE_ACC_SPACING = 0.6F
const val REL_NOTE_DOUBLE_ACC_SPACING = 0.7F // this is accounting for the accidentals
const val REL_CLEF_WIDTH = 0.71F
const val REL_LEDGER_LINE_WIDTH = 0.53F

// note arrows
const val REL_NOTE_ARROW_LABEL_WIDTH = 0.25F
const val REL_NOTE_ARROW_ARROW_WIDTH = 0.25F
const val REL_NOTE_ARROW_SPACING = 0.1F
const val REL_MARGIN_AFTER_ARROW = 0.25F

const val REL_H_SPACING = 0.17F
const val REL_H_PADDING = REL_SOLID_NOTE_WIDTH * 1F
const val REL_MARGIN_AFTER_NOTE_ACC = 0F
const val REL_SPACING_BETWEEN_DOUBLE_BARLINES = 0.18F
const val REL_MARGIN_AFTER_NOTE = 0.14F

const val REL_STAFF_HEIGHT = 1F
const val REL_STAFF_STEP_HEIGHT = 1F / 8F
const val REL_NOTE_ARROW_REL_HEAD_ROOM = 1.5F

const val REL_LINE_WIDTH = 0.025F

const val MAX_GLYPH_SIZE = 150F
const val MAX_GLYPH_SIZE_ZOOMED = MAX_GLYPH_SIZE * 1.5F