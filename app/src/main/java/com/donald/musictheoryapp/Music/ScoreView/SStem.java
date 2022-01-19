package com.donald.musictheoryapp.Music.ScoreView;

import android.graphics.Canvas;

import com.donald.musictheoryapp.Music.MusicXML.Note;
import com.donald.musictheoryapp.Music.MusicXML.Clef;

class SStem extends ScoreStamp
{
	private float horizontalOffsetOnStaff;
	private float startPosYOffset;
	private float endPosYOffset;

	protected SStem(ScoreView view)
	{
		super(view);
	}

	protected void setNote(Note note, Clef clef)
	{
		{
			int noteStaffPosition = Clef.noteStaffPosition(note, clef);
			startPosYOffset = -noteStaffPosition * staffStepHeight();
			if(noteStaffPosition >= ScoreView.MIDDLE_LINE_STAFF_POS)
			{
				// down
				horizontalOffsetOnStaff = -noteWidth() / 2;
				endPosYOffset = startPosYOffset + staffStepHeight() * 7;
			}
			else
			{
				// up
				horizontalOffsetOnStaff = noteWidth() / 2;
				endPosYOffset = startPosYOffset - staffStepHeight() * 7;
			}
		}

		/*
		int notePosition = note.pitch.step + note.pitch.octave * Note.Pitch.Step.NO_OF_STEPS;
		int noteStaffPosition = notePosition - Clef.Sign.BASE_NOTE_POS_BY_SIGN[clef.sign] + (clef.line - 1) * 2;
		m_VerticalOffsetOnStaff = noteStaffPosition * staffStepHeight();
		if(noteStaffPosition >= ScoreView.MIDDLE_LINE_STAFF_POS)
		{
			// down
			m_HorizontalOffsetOnStaff = -noteWidth() / 2;
			m_StartPosY = staffHeight() - m_VerticalOffsetOnStaff;
			m_EndPosY = staffHeight() - m_VerticalOffsetOnStaff + staffStepHeight() * Note.Pitch.Step.NO_OF_STEPS;
		}
		else
		{
			// up
			m_HorizontalOffsetOnStaff = noteWidth() / 2;
			m_StartPosY = -m_VerticalOffsetOnStaff;
			m_EndPosY = staffHeight() - m_VerticalOffsetOnStaff;
		}

		 */
	}

	@Override
	protected void onDraw(Canvas canvas, float posX, float posY)
	{
		canvas.drawLine(
			posX + horizontalOffsetOnStaff, posY + startPosYOffset,
			posX + horizontalOffsetOnStaff, posY + endPosYOffset,
			linePaint()
		);
	}
}
