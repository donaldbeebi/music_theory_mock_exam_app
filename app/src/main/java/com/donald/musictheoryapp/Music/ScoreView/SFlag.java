package com.donald.musictheoryapp.Music.ScoreView;

import android.graphics.Canvas;

import com.donald.musictheoryapp.Music.MusicXML.Note;
import com.donald.musictheoryapp.Music.MusicXML.Clef;

class SFlag extends ScoreStamp
{
	private float m_DownOffset = 0;
	private char m_Glyph;
	private float m_VerticalOffsetOnStaff;
	private float m_HorizontalOffsetOnStaff;

	protected SFlag(ScoreView view)
	{
		super(view);
		m_VerticalOffsetOnStaff = 0;
		m_HorizontalOffsetOnStaff = 0;
	}

	protected void setNote(Note note, Clef clef)
	{
		int notePosition = note.pitch.step + note.pitch.octave * Note.Pitch.Step.NO_OF_STEPS;
		int noteStaffPosition = notePosition - Clef.Sign.BASE_NOTE_POS_BY_SIGN[clef.sign] +
			(clef.line - 1) * 2;
		if(noteStaffPosition >= ScoreView.MIDDLE_LINE_STAFF_POS)
		{
			// down
			m_HorizontalOffsetOnStaff = -noteWidth() / 2;
			m_DownOffset = staffStepHeight() * 14;
			switch(note.type)
			{
				case Note.Type.EIGHTH:
					m_Glyph = U.FLAG_8TH_DOWN;
					break;
				case Note.Type.SIXTEENTH:
					m_Glyph = U.FLAG_16TH_DOWN;
					break;
				case Note.Type.THIRTY_SECOND:
					m_Glyph = U.FLAG_32TH_DOWN;
					break;
				case Note.Type.SIXTY_FOURTH:
					m_Glyph = U.FLAG_64TH_DOWN;
					break;
				default:
					throw new IllegalArgumentException(note.type + " is not a valid type.");
			}
		}
		else
		{
			// up
			m_HorizontalOffsetOnStaff = noteWidth() / 2;
			m_DownOffset = 0;
			switch(note.type)
			{
				case Note.Type.EIGHTH:
					m_Glyph = U.FLAG_8TH_UP;
					break;
				case Note.Type.SIXTEENTH:
					m_Glyph = U.FLAG_16TH_UP;
					break;
				case Note.Type.THIRTY_SECOND:
					m_Glyph = U.FLAG_32TH_UP;
					break;
				case Note.Type.SIXTY_FOURTH:
					m_Glyph = U.FLAG_64TH_UP;
					break;
				default:
					throw new IllegalArgumentException(note.type + " is not a valid type.");
			}
		}
		//m_VerticalOffsetOnStaff = (noteStaffPosition + Note.Pitch.Step.NO_OF_STEPS) * staffStepHeight();
		m_VerticalOffsetOnStaff = -noteStaffPosition * staffStepHeight() - 7 * staffStepHeight();
	}

	@Override
	protected void onDraw(Canvas canvas, float posX, float posY)
	{
		canvas.drawText(String.valueOf(m_Glyph),
			posX + m_HorizontalOffsetOnStaff,
			//posY + fontSize() - m_VerticalOffsetOnStaff + m_DownOffset,
			posY + m_VerticalOffsetOnStaff + m_DownOffset,
			glyphPaint()
		);
	}
}