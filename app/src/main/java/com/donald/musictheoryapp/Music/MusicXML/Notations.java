package com.donald.musictheoryapp.Music.MusicXML;

import org.json.JSONException;
import org.json.JSONObject;

public class Notations
{
	public static class NoteArrow
	{
		public final String label;

		public NoteArrow(String label)
		{
			this.label = label;
		}

		public NoteArrow(NoteArrow that)
		{
			this.label = that.label;
		}

		public boolean equals(NoteArrow that)
		{
			return that != null &&
				this.label.equals(that.label);
		}

		public static NoteArrow fromJSON(JSONObject object) throws JSONException
		{
			return new NoteArrow(
				object.getString("label")
			);
		}
	}

	public final NoteArrow noteArrow;

	public Notations(NoteArrow noteArrow)
	{
		this.noteArrow = noteArrow;
	}

	public Notations(Notations that)
	{
		this.noteArrow = new NoteArrow(that.noteArrow);
	}

	public boolean equals(Notations that)
	{
		return that != null &&
			(this.noteArrow == null ? that.noteArrow == null : this.noteArrow.equals(that.noteArrow));
	}

	public static Notations fromJSON(JSONObject object) throws JSONException
	{
		return new Notations(
			NoteArrow.fromJSON(object)
		);
	}

	public Notations clone()
	{
		return new Notations(noteArrow);
	}
}
