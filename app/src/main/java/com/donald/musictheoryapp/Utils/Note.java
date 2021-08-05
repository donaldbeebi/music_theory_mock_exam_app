package com.donald.musictheoryapp.Utils;

/*
 * Note ID
 * Sorted by (in order) LetterName, Range, Accidental
 * Accidental order: double flat, flat, natural, plain, sharp, double sharp
 * ------------------------------------------------------------------
 *  0: C0bb  | 1: C0b   | 2: C0    | 3: C0#   | 4: C0##  | 5: C0n   |
 * ------------------------------------------------------------------
 *  6: D0bb  | 7: D0b   | 8: D0    | 9: D0#   | 10: D0## | 11: D0n  |
 * ------------------------------------------------------------------
 *
 * Pitch value (starting from 0)
 * C0, C#0, D0, D#0, E0, F0, F#0, G0, G#0, A0, A#0, B0, C1...
 *
 * useful constants:
 * 42 note IDs per range (6 * 7)
 *
 * all conversions:
 * note ID / 72 = range
 * note ID % 7  = letter name
 * note ID % 6  = accidental
 *
 * pitch value / 12 = range
 * pitch value % 12 = octave value (value of a note in the context of an octave)
 *
 * (note ID / 6) * 6 + n = same range, same letter name, n-th accidental
 */

// TODO: TAKE CARE OF CORNER CASES (WHAT IF THE NOTE IS VERY LOW?)

public class Note
{
    // public fields for question generations
    public static final String[] CLEFS =
        {
            "treble", // treble
            "alto",   // alto
            "tenor",  // tenor
            "bass"    // bass
        };
    //inclusive
    public static final int[] LOWEST_POSSIBLE_NOTE_IDS_GENERATED_BY_CLEF =
        {
            144, // treble
            108, // alto
            96,  // tenor
            72   // bass
        };
    // inclusive
    public static final int[] HIGHEST_POSSIBLE_NOTE_IDS_GENERATED_BY_CLEF =
        {
            269, // treble
            233, // alto
            221, // tenor
            197  // bass
        };
    public static final int NOTE_ID_RANGE_FOR_EVERY_CLEF = 126;
    public static final int NUMBER_OF_NOTE_IDS_PER_OCTAVE = 42;

    public static boolean isWhiteNote(int value)
    {
        int octaveValue = value % 12;
        return ((octaveValue <= 4) && (octaveValue % 2 == 0)) || // C - E
            ((octaveValue >= 5) && (octaveValue % 2 == 1)); // F - B
    }

    public static int getPitchValueFromNoteID(int noteID) { return new Note(noteID).getPitchValue(); }

    private int m_Range;
    private LetterName m_LetterName;
    private Accidental m_Accidental;

    public Note(int noteID)
    {
        if(noteID < 2) throw new IllegalArgumentException("Note ID cannot be smaller than 2.");
        m_Range = noteID / 42;
        m_LetterName = LetterName.values()[(noteID / 6) % 7];
        m_Accidental = Accidental.values()[noteID % 6];
    }
    /*
    public Note(int pitchValue, Accidental accidental)
    {
        m_Range = pitchValue / 12;
        pitchValue = pitchValue % 12;
        pitchValue -= accidental.value;
        m_LetterName = LetterName.values()[pitchValue];
        m_Accidental = accidental;
    }
     */

    // TODO: make cloneable!

    public Note(int range, LetterName letterName, Accidental accidental)
    {
        m_Range = range;
        m_LetterName = letterName;
        m_Accidental = accidental;
    }

    // setters
    public void changeRangeBy(int amount) { m_Range += amount; }
    public void setRange(int range) { m_Range = range; }
    public void changeLetterNameBy(int amount)
        { m_LetterName = LetterName.values()[(m_LetterName.ordinal() + amount) % LetterName.values().length]; }
    public void setLetterName(LetterName letterName) { m_LetterName = letterName; }
    public void setAccidental(Accidental accidental) { m_Accidental = accidental; }

    // getters
    public int getRange() { return m_Range; }
    public Accidental getAccidental() { return m_Accidental; }
    public LetterName getLetterName() { return m_LetterName; }
    public String getString() { return m_LetterName.string + m_Accidental.string; }
    public String getStringWithRange() { return m_Range + getString(); }
    public String getStringForImage() { return m_Range + m_LetterName.string_for_image + m_Accidental.string_for_image; }
    public int getPitchValue() { return m_Range * 12 + m_LetterName.value + m_Accidental.value; }
    public int getOctaveValue() { return m_LetterName.value + m_Accidental.value; }
    public int getNoteID()
    {
        return
            m_Range * LetterName.values().length * Accidental.values().length
            + m_LetterName.ordinal() * Accidental.values().length
            + m_Accidental.ordinal();
    }
}
