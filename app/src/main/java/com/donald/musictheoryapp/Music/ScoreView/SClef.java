package com.donald.musictheoryapp.Music.ScoreView;

import android.graphics.Canvas;

import com.donald.musictheoryapp.Music.MusicXML.Clef;

class SClef extends ScoreStamp
{
	private char m_Glyph;
	private float m_VerticalOffsetOnStaff;

	protected SClef(ScoreView view)
	{
		super(view);
		m_Glyph = U.G_CLEF;
		m_VerticalOffsetOnStaff = 2 * staffStepHeight();
	}

	protected void setClef(Clef clef)
	{
		switch(clef.sign)
		{
			case Clef.Sign.F:
				m_Glyph = U.F_CLEF;
				m_VerticalOffsetOnStaff = (clef.line - 1) * staffStepHeight() * 2;
				break;
			case Clef.Sign.C:
				m_Glyph = U.C_CLEF;
				m_VerticalOffsetOnStaff = (clef.line - 1) * staffStepHeight() * 2;
				break;
			case Clef.Sign.G:
				m_Glyph = U.G_CLEF;
				m_VerticalOffsetOnStaff = (clef.line - 1) * staffStepHeight() * 2;
				break;
			default:
				throw new IllegalArgumentException(clef + " is not valid.");
		}
	}

	@Override
	protected void onDraw(Canvas canvas, float posX, float posY)
	{
		canvas.drawText(String.valueOf(m_Glyph),
			posX,
			posY + glyphSize() - m_VerticalOffsetOnStaff,
			glyphPaint()
		);
	}
}
