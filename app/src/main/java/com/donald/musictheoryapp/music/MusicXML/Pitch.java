package com.donald.musictheoryapp.music.MusicXML;

import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;

public class Pitch
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

		public static final int[] STEPS_IN_FIFTHS = {F, C, G, D, A, E, B};

		public static int fromString(String stepString)
		{
			if(stepString.length() != 1)
			{
				throw new IllegalArgumentException("stepString must be 1 character long.");
			}
			return (stepString.charAt(0) - 'A' + 5) % NO_OF_STEPS;
		}

		public static String stringOf(int step)
		{
			if(!isValid(step)) throw new IllegalArgumentException();
			return String.valueOf((char) ((step + 2) % NO_OF_STEPS + 'A'));
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

	public static Pitch fromJson(JSONObject correctPitch) throws JSONException
	{
		return new Pitch(
			Step.fromString(correctPitch.getString("step")),
			correctPitch.getInt("alter"),
			correctPitch.getInt("octave")
		);
	}

	public JSONObject toJson() throws JSONException
	{
		JSONObject object = new JSONObject();
		object.put("step", Step.stringOf(step));
		object.put("alter", alter);
		object.put("octave", octave);
		return object;
	}

	public void addToXml(Element note) {
		Element pitch = note.addElement("pitch");
		pitch.addElement("step").addText(Step.stringOf(step));
		if(alter() != 0) pitch.addElement("alter").addText(String.valueOf(alter()));
		pitch.addElement("octave").addText(String.valueOf(octave()));

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
