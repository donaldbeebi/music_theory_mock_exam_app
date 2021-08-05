package com.donald.musictheoryapp.Utils;

public class OctaveNote
{
    public final LetterName letterName;
    public final Accidental accidental;

    public OctaveNote(LetterName letterName, Accidental accidental)
    {
        this.letterName = letterName;
        this.accidental = accidental;
    }

    public int getOctaveValue() { return (letterName.value + accidental.value) % 12; }
}
