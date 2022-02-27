package com.donald.musictheoryapp.music.Utils;

public class Note
{
	public static final int TRUE_IDS_PER_OCT = 35;
	public static final int NATURAL_IDS_PER_OCT = 7;
	public static final int IDS_PER_OCT = TRUE_IDS_PER_OCT + NATURAL_IDS_PER_OCT;

	private int m_ID;
	private int m_Value;
	private int m_Type;

	public Note(int letter, int accidental, int octave, int value)
	{
		m_ID = idFromLetter(letter, accidental, octave);
		m_Value = value;
	}

	public int letter()
	{
		return letterFromID(m_ID);
	}

	public int accidental()
	{
		return accidentalFromID(m_ID);
	}

	public int octave()
	{
		return octaveFromID(m_ID);
	}

	public int value()
	{
		return m_Value;
	}

	public static int idFromLetter(int letter, int accidental, int octave)
	{
		return letter + accidental * Letter.NO_OF_LETTERS + octave * IDS_PER_OCT;
	}

	public static int letterFromID(int id)
	{
		return id % Letter.NO_OF_LETTERS;
	}

	public static int accidentalFromID(int id)
	{
		return Math.floorMod(id, IDS_PER_OCT) / Letter.NO_OF_LETTERS;
	}

	public static int octaveFromID(int id)
	{
		return id / IDS_PER_OCT;
	}
}
