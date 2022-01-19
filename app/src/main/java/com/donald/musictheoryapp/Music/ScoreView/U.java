package com.donald.musictheoryapp.Music.ScoreView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class U
{
	// staves
	public static final char STAFF_5_LINE       = (char) 0xE014;

	// barlines
	public static final char BAR_LINE_SINGLE	= (char) 0xE030;
	public static final char BAR_LINE_DOUBLE    = (char) 0xE031;
	public static final char BAR_LINE_FINAL 	= (char) 0xE032;

	// clefs
	public static final char G_CLEF             = (char) 0xE050;
	public static final char C_CLEF             = (char) 0xE05C;
	public static final char F_CLEF             = (char) 0xE062;

	// noteheads
	public static final char NOTE_HEAD_D_WHOLE 	= (char) 0xE0A0;
	public static final char NOTE_HEAD_WHOLE 	= (char) 0xE0A2;
	public static final char NOTE_HEAD_HALF 	= (char) 0xE0A3;
	public static final char NOTE_HEAD_BLACK 	= (char) 0xE0A4;

	// stems
	public static final char STEM               = (char) 0xE210;

	// flags
	public static final char FLAG_8TH_UP        = (char) 0xE240;
	public static final char FLAG_8TH_DOWN      = (char) 0xE241;
	public static final char FLAG_16TH_UP       = (char) 0xE242;
	public static final char FLAG_16TH_DOWN     = (char) 0xE243;
	public static final char FLAG_32TH_UP		= (char) 0xE244;
	public static final char FLAG_32TH_DOWN		= (char) 0xE245;
	public static final char FLAG_64TH_UP		= (char) 0xE246;
	public static final char FLAG_64TH_DOWN		= (char) 0xE247;

	// accidentals
	public static final char ACC_FLAT           = (char) 0xE260;
	public static final char ACC_NATURAL        = (char) 0xE261;
	public static final char ACC_SHARP          = (char) 0xE262;
	public static final char ACC_D_SHARP		= (char) 0xE263;
	public static final char ACC_D_FLAT			= (char) 0xE264;

	// arrows and arrowheads
	public static final char ARROW_BLACK_DOWN	= (char) 0xEB64;

	private static final Map<String, Character> STRING_MAP;
	static
	{
		Map<String, Character> map = new HashMap<>();
		map.put("arrowBlackDown", ARROW_BLACK_DOWN);
		STRING_MAP = Collections.unmodifiableMap(map);
	}

	public char fromString(String string)
	{
		return STRING_MAP.get(string);
	}
}
