package edu.cuhk.csci3310.musictheoryapp.Utils;

public class MusicalScale
{
    public final OctaveNote m_StartingNote;
    public final ScaleType m_ScaleType;

    MusicalScale(OctaveNote startingNote, ScaleType scaleType)
    {
        m_StartingNote = startingNote;
        m_ScaleType = scaleType;
    }

    public OctaveNote getStartingNote() { return m_StartingNote; }

    public ScaleType getScaleType() { return m_ScaleType; }

    public OctaveNote getOctaveNote(int value)
    {
        return null;
    }
}
