package com.donald.musictheoryapp.Music.MusicXML;

import org.json.JSONException;
import org.json.JSONObject;

public class Clef
{
	public static Clef fromJSON(JSONObject clef) throws JSONException
	{
		return new Clef(
			Sign.fromString(clef.getString("sign")),
			clef.getInt("line"),
			clef.getBoolean("print_object")
		);
	}

	public static class Sign
	{
		public static final int F = 0;
		public static final int C = 1;
		public static final int G = 2;

		public static final String[] STRINGS =
		{
			"F",
			"C",
			"G"
		};

		public static final int[] BASE_NOTE_POS_BY_SIGN =
			{
				Note.Pitch.Step.F + 3 * Note.Pitch.Step.NO_OF_STEPS,
				Note.Pitch.Step.C + 4 * Note.Pitch.Step.NO_OF_STEPS,
				Note.Pitch.Step.G + 4 * Note.Pitch.Step.NO_OF_STEPS
			};

		public static int fromString(String signString)
		{
			for(int i = 0; i < STRINGS.length; i++)
			{
				if(signString.equals(STRINGS[i])) return i;
			}
			return -1;
		}
	}

	public final int sign;
	public final int line;
	public final boolean printObject;

	public Clef(int sign, int line, boolean printObject)
	{
		this.sign = sign;
		this.line = line;
		this.printObject = printObject;
	}

	public static int noteStaffPosition(Note note, Clef clef)
	{
		return Note.Pitch.absStep(note.pitch) - Clef.Sign.BASE_NOTE_POS_BY_SIGN[clef.sign]
			+ (clef.line - 1) * 2;
	}
}
