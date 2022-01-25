package com.donald.musictheoryapp.Music.MusicXML;

import androidx.annotation.NonNull;

import com.donald.musictheoryapp.Music.ScoreView.MusicXMLParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Score
{
	public final Part[] parts;

	public Score(Part[] parts) { this.parts = parts; }

	public static Score fromXML(InputStream in) throws IOException, XmlPullParserException
	{
		return new Score(MusicXMLParser.parse(in));
	}

	public static Score fromXML(String xml) throws IOException, XmlPullParserException
	{
		return new Score(MusicXMLParser.parse(new ByteArrayInputStream(xml.getBytes())));
	}

	public Part[] parts() { return parts; }

	@NonNull
	public Score clone()
	{
		Part[] otherParts = new Part[parts.length];
		for(int i = 0; i < otherParts.length; i++)
		{
			otherParts[i] = parts[i].clone();
		}
		return new Score(otherParts);
	}
}
