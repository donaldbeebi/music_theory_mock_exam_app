package com.donald.musictheoryapp.Music.MusicXML;

import org.json.JSONException;
import org.json.JSONObject;

public class Key
{
	public static class Mode
	{
		public static final int MINOR = 0;
		public static final int MAJOR = 1;

		public static final String[] STRINGS =
			{
				"minor",
				"major"
			};

		public static int fromString(String modeString)
		{
			for(int i = 0; i < STRINGS.length; i++)
			{
				if(STRINGS[i].equals(modeString)) return i;
			}
			return -1;
		}
	}

	public final int fifths;
	public final int mode;

	public Key(int fifths, int mode)
	{
		this.fifths = fifths;
		this.mode = mode;
	}

	public static Key fromJSON(JSONObject key) throws JSONException
	{
		return new Key(
			key.getInt("fifths"),
			Mode.fromString(key.getString("mode"))
		);
	}
}
