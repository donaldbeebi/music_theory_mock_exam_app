package com.donald.musictheoryapp.Music.ScoreView;

import android.util.Log;
import android.util.Xml;

import com.donald.musictheoryapp.Music.MusicXML.Clef;
import com.donald.musictheoryapp.Music.MusicXML.Key;
import com.donald.musictheoryapp.Music.MusicXML.Measure;
import com.donald.musictheoryapp.Music.MusicXML.Notations;
import com.donald.musictheoryapp.Music.MusicXML.Note;
import com.donald.musictheoryapp.Music.MusicXML.Part;
import com.donald.musictheoryapp.Music.MusicXML.Time;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MusicXMLParser
{
	public static Part[] parse(InputStream in) throws XmlPullParserException, IOException
	{
		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(in, null);
		parser.nextTag();
		ArrayList<Part> parts = new ArrayList<>();
		parser.require(XmlPullParser.START_TAG, null, "score-partwise");
		while(parser.next() != XmlPullParser.END_TAG)
		{
			if(parser.getEventType() == XmlPullParser.START_TAG)
			{
				String name = parser.getName();
				if(name.equals("part"))
				{
					parts.add(readPart(parser));
				}
				else
				{
					skip(parser);
				}
			}
		}

		Part[] array = new Part[parts.size()];
		array = parts.toArray(array);
		return array;
	}

	private static Part readPart(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		parser.require(XmlPullParser.START_TAG, null, "part");
		String id = parser.getAttributeValue(null, "id");
		ArrayList<Measure> measures = new ArrayList<>();
		while(parser.next() != XmlPullParser.END_TAG)
		{
			if(parser.getEventType() == XmlPullParser.START_TAG)
			{
				String name = parser.getName();
				if(name.equals("measure"))
				{
					measures.add(readMeasure(parser));
				}
				else
				{
					skip(parser);
				}
			}
		}

		Measure[] measureArray = new Measure[measures.size()];
		measureArray = measures.toArray(measureArray);
		return new Part(id, measureArray);
	}

	private static Measure readMeasure(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		parser.require(XmlPullParser.START_TAG, null, "measure");
		Measure.Attributes attributes = null;
		ArrayList<Note> notes = new ArrayList<>();
		Measure.Barline barline = null;
		while(parser.next() != XmlPullParser.END_TAG)
		{
			if(parser.getEventType() == XmlPullParser.START_TAG)
			{
				String name = parser.getName();
				if(name.equals("attributes"))
				{
					attributes = readAttributes(parser);
				}
				else if(name.equals("note"))
				{
					notes.add(readNote(parser));
				}
				else if(name.equals("barline"))
				{
					barline = readBarline(parser);
				}
				else
				{
					skip(parser);
				}
			}
		}

		Note[] noteArray = new Note[notes.size()];
		noteArray = notes.toArray(noteArray);
		return new Measure(attributes, noteArray, barline);
	}

	private static Measure.Attributes readAttributes(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		parser.require(XmlPullParser.START_TAG, null, "attributes");
		int divisions = 0;
		Key key = null;
		Time time = null;
		int staves = 0;
		ArrayList<Clef> clefs = new ArrayList<>();
		while(parser.next() != XmlPullParser.END_TAG)
		{
			if(parser.getEventType() == XmlPullParser.START_TAG)
			{
				String name = parser.getName();
				if(name.equals("divisions"))
				{
					parser.require(XmlPullParser.START_TAG, null, "divisions");
					divisions = Integer.parseInt(readText(parser));
					parser.require(XmlPullParser.END_TAG, null, "divisions");
				}
				else if(name.equals("key"))
				{
					key = readKey(parser);
				}
				else if(name.equals("time"))
				{
					time = readTime(parser);
				}
				else if(name.equals("staves"))
				{
					parser.require(XmlPullParser.START_TAG, null, "staves");
					staves = Integer.parseInt(readText(parser));
					parser.require(XmlPullParser.END_TAG, null, "staves");
				}
				else if(name.equals("clef"))
				{
					clefs.add(readClef(parser));
				}
				else
				{
					skip(parser);
				}
			}
		}

		Clef[] clefArray = new Clef[clefs.size()];
		clefArray = clefs.toArray(clefArray);
		return new Measure.Attributes(divisions, key, time, staves, clefArray);
	}

	private static Note readNote(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		parser.require(XmlPullParser.START_TAG, null, "note");
		boolean printObject = "yes".equals(parser.getAttributeValue(null, "print-object"));
		Note.Pitch pitch = null;
		int duration = 0;
		int type = Note.Type.NULL;
		int accidental = Note.Accidental.NULL;
		boolean chord = false;
		int staff = 1;
		Notations notations = null;
		while(parser.next() != XmlPullParser.END_TAG)
		{
			if(parser.getEventType() == XmlPullParser.START_TAG)
			{
				String name = parser.getName();
				if(name.equals("chord"))
				{
					parser.require(XmlPullParser.START_TAG, null, "chord");
					chord = true;
					parser.nextTag();
					parser.require(XmlPullParser.END_TAG, null, "chord");
				}
				else if(name.equals("pitch"))
				{
					pitch = readPitch(parser);
				}
				else if(name.equals("rest"))
				{
					pitch = null;
				}
				else if(name.equals("duration"))
				{
					parser.require(XmlPullParser.START_TAG, null, "duration");
					duration = Integer.parseInt(readText(parser));
					parser.require(XmlPullParser.END_TAG, null, "duration");
				}
				else if(name.equals("type"))
				{
					parser.require(XmlPullParser.START_TAG, null, "type");
					type = Note.Type.fromString(readText(parser));
					parser.require(XmlPullParser.END_TAG, null, "type");
				}
				else if(name.equals("accidental"))
				{
					parser.require(XmlPullParser.START_TAG, null, "accidental");
					accidental = Note.Accidental.fromString(readText(parser));
					parser.require(XmlPullParser.END_TAG, null, "accidental");
				}
				else if(name.equals("staff"))
				{
					parser.require(XmlPullParser.START_TAG, null, "staff");
					staff = Integer.parseInt(readText(parser));
					parser.require(XmlPullParser.END_TAG, null, "staff");
				}
				else if(name.equals("notations"))
				{
					parser.require(XmlPullParser.START_TAG, null, "notations");
					notations = readNotations(parser);
					parser.require(XmlPullParser.END_TAG, null, "notations");
				}
				else
				{
					skip(parser);
				}
			}
		}

		return new Note(printObject, pitch, duration, type,
			accidental, chord, staff, notations);
	}

	private static Measure.Barline readBarline(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		parser.require(XmlPullParser.START_TAG, null, "barline");
		int barStyle = Measure.Barline.BarStyle.REGULAR;
		while(parser.next() != XmlPullParser.END_TAG)
		{
			if(parser.getEventType() == XmlPullParser.START_TAG)
			{
				String name = parser.getName();
				if(name.equals("bar-style"))
				{
					barStyle = Measure.Barline.BarStyle.fromString(readText(parser));
				}
				else
				{
					skip(parser);
				}
			}
		}
		return new Measure.Barline(barStyle);
	}

	private static Key readKey(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		parser.require(XmlPullParser.START_TAG, null, "key");
		int fifths = 0;
		int mode = 0;
		while(parser.next() != XmlPullParser.END_TAG)
		{
			if(parser.getEventType() == XmlPullParser.START_TAG)
			{
				String name = parser.getName();
				if(name.equals("fifths"))
				{
					parser.require(XmlPullParser.START_TAG, null, "fifths");
					fifths = Integer.parseInt(readText(parser));
					parser.require(XmlPullParser.END_TAG, null, "fifths");
				}
				else if(name.equals("mode"))
				{
					parser.require(XmlPullParser.START_TAG, null, "mode");
					mode = Key.Mode.fromString(readText(parser));
					parser.require(XmlPullParser.END_TAG, null, "mode");
				}
				else
				{
					skip(parser);
				}
			}
		}

		return new Key(fifths, mode);
	}

	private static Time readTime(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		parser.require(XmlPullParser.START_TAG, null, "time");
		int beats = 0;
		int beatType = 0;
		while(parser.next() != XmlPullParser.END_TAG)
		{
			if(parser.getEventType() == XmlPullParser.START_TAG)
			{
				String name = parser.getName();
				if (name.equals("beats"))
				{
					parser.require(XmlPullParser.START_TAG, null, "beats");
					beats = Integer.parseInt(readText(parser));
					parser.require(XmlPullParser.END_TAG, null, "beats");
				}
				else if (name.equals("beatType"))
				{
					parser.require(XmlPullParser.START_TAG, null, "beat-type");
					beatType = Integer.parseInt(readText(parser));
					parser.require(XmlPullParser.END_TAG, null, "beat-type");
				}
				else
				{
					skip(parser);
				}
			}
		}

		return new Time(beats, beatType);
	}

	private static Note.Pitch readPitch(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		parser.require(XmlPullParser.START_TAG, null, "pitch");
		int step = 0;
		int alter = 0;
		int octave = 0;
		while(parser.next() != XmlPullParser.END_TAG)
		{
			if(parser.getEventType() == XmlPullParser.START_TAG)
			{
				String name = parser.getName();
				if(name.equals("step"))
				{
					parser.require(XmlPullParser.START_TAG, null, "step");
					step = Note.Pitch.Step.fromString(readText(parser));
					parser.require(XmlPullParser.END_TAG, null, "step");
				}
				else if(name.equals("alter"))
				{
					parser.require(XmlPullParser.START_TAG, null, "alter");
					alter = Integer.parseInt(readText(parser));
					parser.require(XmlPullParser.END_TAG, null, "alter");
				}
				else if(name.equals("octave"))
				{
					parser.require(XmlPullParser.START_TAG, null, "octave");
					octave = Integer.parseInt(readText(parser));
					parser.require(XmlPullParser.END_TAG, null, "octave");
				}
			}
		}

		return new Note.Pitch(step, alter, octave);
	}

	private static Notations readNotations(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		parser.require(XmlPullParser.START_TAG, null, "notations");
		Notations.NoteArrow noteArrow = null;
		while(parser.next() != XmlPullParser.END_TAG)
		{
			if(parser.getEventType() == XmlPullParser.START_TAG)
			{
				String name = parser.getName();
				if(name.equals("other-notation"))
				{
					String notationName = parser.getAttributeValue(null, "notation-name");
					if("note-arrow".equals(notationName))
					{
						noteArrow = readNoteArrow(parser);
					}
				}
				else
				{
					skip(parser);
				}
			}
		}

		return new Notations(noteArrow);
	}

	private static Clef readClef(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		parser.require(XmlPullParser.START_TAG, null, "clef");
		int sign = 0;
		int line = 0;
		boolean printObject = !("no".equals(parser.getAttributeValue(null, "print-object")));
		while(parser.next() != XmlPullParser.END_TAG)
		{
			if(parser.getEventType() == XmlPullParser.START_TAG)
			{
				String name = parser.getName();
				if(name.equals("sign"))
				{
					parser.require(XmlPullParser.START_TAG, null, "sign");
					sign = Clef.Sign.fromString(readText(parser));
					parser.require(XmlPullParser.END_TAG, null, "sign");
				}
				else if(name.equals("line"))
				{
					parser.require(XmlPullParser.START_TAG, null, "line");
					line = Integer.parseInt(readText(parser));
					parser.require(XmlPullParser.END_TAG, null, "line");
				}
				else
				{
					skip(parser);
				}
			}
		}

		return new Clef(sign, line, printObject);
	}

	private static Notations.NoteArrow readNoteArrow(XmlPullParser parser)
		throws IOException, XmlPullParserException
	{
		parser.require(XmlPullParser.START_TAG, null, "other-notation");
		String label = readText(parser);
		return new Notations.NoteArrow(label);
	}
	
	private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		if(parser.next() == XmlPullParser.TEXT)
		{
			String result = parser.getText();
			parser.nextTag();
			return result;
		}
		return "";
	}

	private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		if(parser.getEventType() != XmlPullParser.START_TAG)
		{
			throw new IllegalStateException();
		}
		int depth = 1;
		while(depth != 0)
		{
			switch(parser.next())
			{
				case XmlPullParser.END_TAG:
					depth--;
					break;
				case XmlPullParser.START_TAG:
					depth++;
					break;
			}
		}
	}
}


