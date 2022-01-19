package com.donald.musictheoryapp.Music.MusicXML;

import java.util.ArrayList;

public class Measure
{
	public static class Attributes
	{
		public final int divisions;
		public final Key key;
		public final Time time;
		public final int staves;
		public final Clef[] clefs;

		public Attributes(int divisions, Key key, Time time, int staves, Clef[] clefs)
		{
			this.divisions = divisions;
			this.key = key;
			this.time = time;
			this.staves = staves;
			this.clefs = clefs;
		}
	}

	public static class Barline
	{
		public static class BarStyle
		{
			public static final int REGULAR = 0;
			public static final int LIGHT_HEAVY = 1;
			public static final int LIGHT_LIGHT = 2;

			public static final String[] STRINGS =
				{
					"regular",
					"light-heavy",
					"light-light"
				};

			public static int fromString(String barlineString)
			{
				for(int i = 0; i < STRINGS.length; i++)
				{
					if(barlineString.equals(STRINGS[i])) return i;
				}
				return -1;
			}
		}

		public final int barlineStyle;

		public Barline(int barlineStyle)
		{
			this.barlineStyle = barlineStyle;
		}
	}

	public final Attributes attributes;
	public final Note[] notes;
	public final Barline barline;

	public Measure(Attributes attributes, Note[] notes, Barline barline)
	{
		this.attributes = attributes;
		this.notes = notes;
		this.barline = barline;
	}

	public Note[] notes()
	{
		return notes;
	}
}
