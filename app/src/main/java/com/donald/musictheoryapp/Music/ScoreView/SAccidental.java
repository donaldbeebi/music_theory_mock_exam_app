package com.donald.musictheoryapp.Music.ScoreView;

import android.graphics.Canvas;

import com.donald.musictheoryapp.Music.MusicXML.Clef;
import com.donald.musictheoryapp.Music.MusicXML.Note;

public class SAccidental extends ScoreStamp
{
	private char glyph;
	private float glyphWidth;
	private float verticalOffsetOnStaff;
	//private final float horizontalOffsetOnStaff;

	SAccidental(ScoreView view)
	{
		super(view);
		glyph = U.ACC_NATURAL;
		verticalOffsetOnStaff = 0;
		//horizontalOffsetOnStaff = -(noteWidth() + noteWidth() * 0.5f + paddingFromNote);
	}

	protected void setNote(Note note, Clef clef)
	{
		verticalOffsetOnStaff = -Clef.noteStaffPosition(note, clef) * staffStepHeight() - staffHeight();
		switch(note.accidental)
		{
			case Note.Accidental.FLAT_FLAT:
				glyph = U.ACC_D_FLAT;
				glyphWidth = noteWidth() * 1.3f;
				break;
			case Note.Accidental.FLAT:
				glyph = U.ACC_FLAT;
				glyphWidth = noteWidth() * 0.8f;
				break;
			case Note.Accidental.NATURAL:
				glyph = U.ACC_NATURAL;
				glyphWidth = noteWidth() * 0.8f;
				break;
			case Note.Accidental.SHARP:
				glyph = U.ACC_SHARP;
				glyphWidth = noteWidth() * 0.8f;
				break;
			case Note.Accidental.SHARP_SHARP:
				glyph = U.ACC_D_SHARP;
				glyphWidth = noteWidth() * 0.8f;
				break;
			case Note.Accidental.NULL:
				return;
			default:
				throw new IllegalArgumentException(note.pitch.alter + " + is not a valid accidental.");
		}
	}

	@Override
	protected void onDraw(Canvas canvas, float posX, float posY)
	{
		float paddingFromNote = noteWidth() * 0.5f;

		canvas.drawText(String.valueOf(glyph),
			//posX + horizontalOffsetOnStaff,
			posX - glyphWidth - paddingFromNote - noteWidth() / 2,
			posY + glyphSize() + verticalOffsetOnStaff,
			glyphPaint()
		);
	}
}
