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

	public Part clone()
	{
		Measure[] otherMeasures = new Measure[measures.length];
		for(int i = 0; i < otherMeasures.length; i++)
		{
			otherMeasures[i] = measures[i].clone();
		}
		return new Part(id, otherMeasures);
	}
}
