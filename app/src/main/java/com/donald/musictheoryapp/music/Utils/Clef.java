package com.donald.musictheoryapp.music.Utils;

public class Clef
{
	public static final int BASS = 0;
	public static final int TENOR = 1;
	public static final int ALTO = 2;
	public static final int TREBLE = 3;

	public static final int[] BOTTOM_LINE_NOTE_POS_BY_CLEF =
		{
			Letter.KEY_POS[Letter.G] + 2 * Letter.NO_OF_LETTERS, // 18
			Letter.KEY_POS[Letter.D] + 3 * Letter.NO_OF_LETTERS,
			Letter.KEY_POS[Letter.F] + 3 * Letter.NO_OF_LETTERS,
			Letter.KEY_POS[Letter.E] + 4 * Letter.NO_OF_LETTERS
		};
}
