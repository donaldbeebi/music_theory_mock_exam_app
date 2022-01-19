package com.donald.musictheoryapp.Music.ScoreView;

import android.graphics.Canvas;

public class SNoteArrow extends ScoreStamp
{
	private String label;
	private final char glyph;

	SNoteArrow(ScoreView view)
	{
		super(view);
		glyph = U.ARROW_BLACK_DOWN;
	}

	protected void setLabel(String label)
	{
		this.label = label;
	}

	@Override
	protected void onDraw(Canvas canvas, float posX, float posY)
	{
		float glyphSize = glyphSize() * 1.2f;
		float vOffsetOnStaff = -staffStepHeight() * 1 - staffHeight();
		float hOffsetOnStaff = -noteWidth() / 2;

		posX += hOffsetOnStaff;
		posY += vOffsetOnStaff;

		float defaultGlyphSize = glyphPaint().getTextSize();
		glyphPaint().setTextSize(glyphSize);
		canvas.drawText(String.valueOf(glyph),
			posX,
			posY,
			glyphPaint());
		glyphPaint().setTextSize(defaultGlyphSize);

		if(label != null)
		{
			canvas.drawText(label,
				posX - noteWidth() * 1.5f,
				posY - staffStepHeight() * 2,
				textPaint());
		}
	}
}
