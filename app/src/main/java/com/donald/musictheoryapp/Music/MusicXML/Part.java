package com.donald.musictheoryapp.Music.MusicXML;

public class Part
{
	public final String id;
	public final Measure[] measures;

	public Part(String id, Measure[] measures)
	{
		this.id = id;
		this.measures = measures;
	}

	public Measure[] measures()
	{
		return measures;
	}
}
