package com.donald.musictheoryapp.Music.ScoreView;

import android.graphics.Canvas;

import com.donald.musictheoryapp.Music.MusicXML.Note;
import com.donald.musictheoryapp.Music.MusicXML.Clef;

class SNoteHead extends ScoreStamp
{
	private char m_Glyph;
	private float verticalOffsetOnStaff;
	private float horizontalOffsetOnStaff;

	SNoteHead(ScoreView view)
	{
		super(view);
		m_Glyph = U.NOTE_HEAD_WHOLE;
		verticalOffsetOnStaff = 0;
	}

	protected void setNote(Note note, Clef clef)
	{
		//int notePosition = note.pitch.step + note.pitch.octave * Note.Pitch.Step.NO_OF_STEPS;
		setType(note.type);
		int noteStaffPosition = Clef.noteStaffPosition(note, clef);
		verticalOffsetOnStaff = -noteStaffPosition * staffStepHeight() - staffHeight();
		//m_VerticalOffsetOnStaff =
		//	(notePosition - Clef.Sign.BASE_NOTE_POS_BY_SIGN[clef.sign] + (clef.line - 1) * 2) * staffStepHeight();
	}

	private void setType(int type)
	{
		switch(type)
		{
			case Note.Type.BREVE:
				m_Glyph = U.NOTE_HEAD_D_WHOLE;
				horizontalOffsetOnStaff = -noteWidth() * 1.4f / 2;
				break;
			case Note.Type.WHOLE:
				m_Glyph = U.NOTE_HEAD_WHOLE;
				horizontalOffsetOnStaff = -noteWidth() * 1.4f / 2;
				break;
			case Note.Type.HALF:
				m_Glyph = U.NOTE_HEAD_HALF;
				horizontalOffsetOnStaff = -noteWidth() / 2;
				break;
			case Note.Type.QUARTER:
			case Note.Type.EIGHTH:
			case Note.Type.SIXTEENTH:
			case Note.Type.THIRTY_SECOND:
			case Note.Type.SIXTY_FOURTH:
				m_Glyph = U.NOTE_HEAD_BLACK;
				horizontalOffsetOnStaff = -noteWidth() / 2;
				break;
			default:
				throw new IllegalArgumentException(type + " is not a valid type.");
		}
	}

	@Override
	protected void onDraw(Canvas canvas, float posX, float posY)
	{
		canvas.drawText(String.valueOf(m_Glyph),
			posX + horizontalOffsetOnStaff,
			posY + glyphSize() + verticalOffsetOnStaff,
			glyphPaint()
		);
	}
}
