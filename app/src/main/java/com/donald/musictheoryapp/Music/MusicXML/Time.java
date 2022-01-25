package com.donald.musictheoryapp.Music.MusicXML;

public class Time
{
	public final int beats;
	public final int beatType;

	public Time(int beats, int beatType)
	{
		this.beats = beats;
		this.beatType = beatType;
	}

	public Time clone()
	{
		return new Time(beats, beatType);
	}
}
