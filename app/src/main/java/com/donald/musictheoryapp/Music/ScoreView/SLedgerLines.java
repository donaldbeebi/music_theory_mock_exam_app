package com.donald.musictheoryapp.Music.ScoreView;

import android.graphics.Canvas;
import android.util.Log;

import com.donald.musictheoryapp.Music.MusicXML.Clef;
import com.donald.musictheoryapp.Music.MusicXML.Note;

public class SLedgerLines extends ScoreStamp
{
	private int numberOfLines;
	private boolean aboveStaff;
	private boolean draws;

	protected SLedgerLines(ScoreView view)
	{
		super(view);
	}

	protected void setNote(Note note, Clef clef)
	{
		int staffPosition = Clef.noteStaffPosition(note, clef);
		if(staffPosition > ScoreView.TOP_LINE_STAFF_BOS + 1)
		{
			aboveStaff = true;
			numberOfLines = (staffPosition - ScoreView.TOP_LINE_STAFF_BOS) / 2;
			draws = true;
		}
		else if(staffPosition < ScoreView.BASE_LINE_STAFF_POS - 1)
		{
			aboveStaff = false;
			numberOfLines = (ScoreView.BASE_LINE_STAFF_POS - staffPosition) / 2;
			draws = true;
		}
		else
		{
			draws = false;
		}
	}

	@Override
	protected void onDraw(Canvas canvas, float posX, float posY)
	{
		if(draws)
		{
			float lineWidth = noteWidth() * 1.8f;
			float factor = aboveStaff ? -1 : 1;
			float currentPosY = (aboveStaff ? -staffHeight() : 0f) + posY
				+ staffStepHeight() * 2 * factor;

			for(int i = 0; i < numberOfLines; i++)
			{
				canvas.drawLine(
					posX - lineWidth / 2, currentPosY,
					posX + lineWidth / 2, currentPosY,
					linePaint()
				);
				currentPosY += staffStepHeight() * 2 * factor;
			}
		}
	}
}
