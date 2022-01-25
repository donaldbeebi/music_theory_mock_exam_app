package com.donald.musictheoryapp.Music.MusicXML;

import android.graphics.Color;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class Note
{
	public static class Pitch
	{
		public static class Step
		{
			public static final int C = 0;
			public static final int D = 1;
			public static final int E = 2;
			public static final int F = 3;
			public static final int G = 4;
			public static final int A = 5;
			public static final int B = 6;

			public static final int NO_OF_STEPS = 7;

			public static final int[] FIFTHS =
				{
					1,
					3,
					5,
					0,
					2,
					4,
					6
				};

			public static final int[] STEPS_IN_FIFTHS = { F, C, G, D, A, E, B };

			public static int fromString(String stepString)
			{
				if(stepString.length() != 1)
				{
					throw new IllegalArgumentException("stepString must be 1 character long.");
				}
				return (stepString.charAt(0) - 'A' + 5) % NO_OF_STEPS;
			}

			public static boolean isValid(int step)
			{
				return step >= 0 && step < NO_OF_STEPS;
			}
		}

		// TODO: PRIVATE
		public int step;
		public int alter;
		public int octave;

		public Pitch(int step, int alter, int octave)
		{
			this.step = step;
			this.alter = alter;
			this.octave = octave;
		}

		public Pitch(Pitch that)
		{
			this.step = that.step;
			this.alter = that.alter;
			this.octave = that.octave;
		}

		public void setStep(int step)
		{
			if(!Step.isValid(step))
			{
				throw new IllegalArgumentException("Invalid step");
			}
			this.step = step;
		}

		public void setAlter(int alter)
		{
			this.alter = alter;
		}

		public void setOctave(int octave)
		{
			this.octave = octave;
		}

		public int step()
		{
			return step;
		}

		public int alter()
		{
			return alter;
		}

		public int octave()
		{
			return octave;
		}

		public int absStep()
		{
			return step + octave * Step.NO_OF_STEPS;
		}

		@Deprecated
		public static int absStep(Pitch pitch)
		{
			return pitch.step + pitch.octave * Step.NO_OF_STEPS;
		}

		public static Pitch fromJSON(JSONObject correctPitch) throws JSONException
		{
			return new Pitch(
				Pitch.Step.fromString(correctPitch.getString("step")),
				correctPitch.getInt("alter"),
				correctPitch.getInt("octave")
			);
		}

		public boolean equals(Pitch that)
		{
			return
				that != null &&
				this.step == that.step &&
				this.alter == that.alter &&
				this.octave == that.octave;
		}

		public Pitch clone()
		{
			return new Pitch(step, alter, octave);
		}
	}

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

	public static Note fromJSON(JSONObject object) throws JSONException
	{
		int accidental = Accidental.NULL;
		if(object.has("accidental"))
		{
			accidental = Accidental.fromString(object.getString("accidental"));
		}
		Notations notations = null;
		if(object.has("notations"))
		{
			notations = Notations.fromJSON(object.getJSONObject("notations"));
		}
		return new Note(
			// removed printObject = ...
			object.isNull("print-object") || object.getBoolean("print-object"),
			Pitch.fromJSON(object.getJSONObject("pitch")),
			object.getInt("duration"),
			Type.fromString(object.getString("type")),
			accidental,
			false, // chord not implemented
			object.getInt("staff"),
			notations
		);
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
