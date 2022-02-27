package com.donald.musictheoryapp.music.MusicXML;

import android.graphics.Color;

import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;

public class Note
{

	public static class Type
	{
		public static final int NULL = -1;
		public static final int BREVE = 0;
		public static final int WHOLE = 1;
		public static final int HALF = 2;
		public static final int QUARTER = 3;
		public static final int EIGHTH = 4;
		public static final int SIXTEENTH = 5;
		public static final int THIRTY_SECOND = 6;
		public static final int SIXTY_FOURTH = 7;

		public static final String[] STRINGS =
			{
				"breve",
				"whole",
				"half",
				"quarter",
				"eighth",
				"16th",
				"32nd",
				"64th"
			};

		public static int fromString(String durationString)
		{
			for(int i = 0; i < STRINGS.length; i++)
			{
				String string = STRINGS[i];
				if(durationString.equals(string)) return i;
			}
			return NULL;
		}

		public static String stringOf(int type) {
			return STRINGS[type];
		}
	}

	public static class Accidental
	{
		public static final int NULL = -1;
		public static final int FLAT_FLAT = 0;
		public static final int FLAT = 1;
		public static final int NATURAL = 2;
		public static final int SHARP = 3;
		public static final int SHARP_SHARP = 4;

		public static final String[] STRINGS =
			{
				"flat-flat",
				"flat",
				"natural",
				"sharp",
				"sharp-sharp"
			};

		public static final int NO_OF_ACCIDENTALS = 5;

		public static int alter(int accidental)
		{
			if(!isValid(accidental))
			{
				throw new IllegalArgumentException("Invalid accidental.");
			}
			if(accidental == NULL) return 0;
			else
			{
				return accidental - NO_OF_ACCIDENTALS / 2;
			}
		}

		public static int fromString(String accidentalString)
		{
			for(int i = 0; i < STRINGS.length; i++)
			{
				String string = STRINGS[i];
				if(accidentalString.equals(string)) return i;
			}
			return NULL;
		}

		public static String stringOf(int accidental) {
			return STRINGS[accidental];
		}

		public static boolean isValid(int accidental)
		{
			return accidental >= -1 && accidental < NO_OF_ACCIDENTALS;
		}
	}

	private boolean printObject;
	public final Pitch pitch;
	public final int duration;
	public final int type;
	public int accidental;
	public final boolean chord;
	public final int staff;
	public final Notations notations;
	private int color;

	public Note(boolean printObject, Pitch pitch, int duration, int type,
				int accidental, boolean chord, int staff, Notations notations)
	{
		this.printObject = printObject;
		this.pitch = pitch;
		this.duration = duration;
		this.type = type;
		this.accidental = accidental;
		this.chord = chord;
		this.staff = staff;
		this.notations = notations;
		this.color = Color.BLACK;
	}

	public Note(Note that)
	{
		this.pitch = that.pitch == null ? null : new Pitch(that.pitch);
		this.duration = that.duration;
		this.type = that.type;
		this.accidental = that.accidental;
		this.chord = that.chord;
		this.staff = that.staff;
		this.notations = that.notations == null ? null : new Notations(that.notations);
	}

	public void setPrintObject(boolean printObject)
	{
		this.printObject = printObject;
	}

	public void setAccidental(int accidental)
	{
		if(!Accidental.isValid(accidental))
		{
			throw new IllegalArgumentException("Invalid accidental");
		}
		this.accidental = accidental;
	}

	public void setColor(int color)
	{
		this.color = color;
	}

	public boolean printObject()
	{
		return printObject;
	}

	public Pitch pitch()
	{
		return pitch;
	}

	public int accidental()
	{
		return accidental;
	}

	public int color()
	{
		return color;
	}

	public boolean equals(Note that)
	{
		return that != null &&
			(this.pitch == null ? that.pitch == null : this.pitch.equals(that.pitch)) &&
			this.duration == that.duration &&
			this.type == that.type &&
			this.accidental == that.accidental &&
			this.chord == that.chord &&
			this.staff == that.staff &&
			(this.notations == null ? that.notations == null : this.notations.equals(that.notations));
	}

	public static Note fromJson(JSONObject object) throws JSONException
	{
		int accidental = Accidental.NULL;
		if(object.has("accidental"))
		{
			accidental = Accidental.fromString(object.getString("accidental"));
		}
		Notations notations = null;
		if(object.has("notations"))
		{
			notations = Notations.fromJson(object.getJSONObject("notations"));
		}
		return new Note(
			// removed printObject = ...
			object.isNull("print-object") || object.getBoolean("print-object"),
			Pitch.fromJson(object.getJSONObject("pitch")),
			object.getInt("duration"),
			Type.fromString(object.getString("type")),
			accidental,
			false, // chord not implemented
			object.getInt("staff"),
			notations
		);
	}

	public JSONObject toJson() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("print-object", printObject);
		object.put("pitch", pitch.toJson());
		object.put("duration", duration);
		if (type != Type.NULL) object.put("type", Type.stringOf(type));
		if (accidental != Accidental.NULL) object.put("accidental", Accidental.stringOf(accidental));
		object.put("staff", staff);
		if (notations != null) object.put("notations", notations.toJson());
		return object;
	}

	public void addToXml(Element measure)
	{
		Element note = measure.addElement("note");
		if(pitch != null) pitch.addToXml(note);
		else if(type != Type.NULL) note.addElement("rest");
		note.addElement("duration").addText(String.valueOf(duration));
		if(type != Type.NULL) note.addElement("type").addText(Type.stringOf(type));
		if(chord) note.addElement("chord");
		if(accidental != Accidental.NULL) note.addElement("accidental").addText(Accidental.stringOf(accidental));
		note.addElement("staff").addText(String.valueOf(staff));
		note.addAttribute("print-object", printObject ? "yes" : "no");
		if(notations != null) notations.addToXml(note);
	}

	public Note clone()
	{
		return new Note(
			printObject,
			(pitch != null ? pitch.clone() : null),
			duration,
			type,
			accidental,
			chord,
			staff,
			(notations != null ? notations.clone() : null)
		);
	}
}
