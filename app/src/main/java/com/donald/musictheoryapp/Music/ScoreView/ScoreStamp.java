package com.donald.musictheoryapp.Music.ScoreView;

import android.graphics.Canvas;
import android.graphics.Paint;

abstract class ScoreStamp
{
	private final ScoreView m_View;

	ScoreStamp(ScoreView view)
	{
		m_View = view;
	}

	// TODO: MAKE ABSTRACT

	protected ScoreView view() { return m_View; }
	protected float glyphSize() { return m_View.glyphSize(); }
	protected float noteWidth() { return m_View.noteWidth(); }
	protected float staffHeight() { return m_View.staffHeight(); }
	protected float staffStepHeight() { return m_View.staffStepHeight(); }
	protected Paint textPaint() { return m_View.textPaint(); }
	protected Paint glyphPaint() { return m_View.glyphPaint(); }
	protected Paint linePaint() { return m_View.linePaint(); }

	protected abstract void onDraw(Canvas canvas, float posX, float posY);
}
