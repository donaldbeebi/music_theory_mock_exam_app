package com.donald.musictheoryapp.Music.MusicXML;

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
}
