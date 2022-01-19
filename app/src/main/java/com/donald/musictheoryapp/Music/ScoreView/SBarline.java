package com.donald.musictheoryapp.Music.ScoreView;

import android.graphics.Canvas;

import static com.donald.musictheoryapp.Music.MusicXML.Measure.Barline.BarStyle.LIGHT_HEAVY;
import static com.donald.musictheoryapp.Music.MusicXML.Measure.Barline.BarStyle.LIGHT_LIGHT;
import static com.donald.musictheoryapp.Music.MusicXML.Measure.Barline.BarStyle.REGULAR;

class SBarline extends ScoreStamp
{
	private int style;

	SBarline(ScoreView view)
	{
		super(view);
		style = REGULAR;
	}

	protected void setStyle(int style)
	{
		this.style = style;
	}

	@Override
	protected void onDraw(Canvas canvas, float posX, float posY)
	{
		float staffLineHalfStrokeWidth = linePaint().getStrokeWidth() / 2;
		switch(style)
		{
			case REGULAR:
			{
				canvas.drawLine(
					posX, posY + staffLineHalfStrokeWidth,
					posX, posY - staffHeight() - staffLineHalfStrokeWidth,
					linePaint()
				);
				break;
			}
			case LIGHT_HEAVY:
			{
				float originalThickness = linePaint().getStrokeWidth();
				linePaint().setStrokeWidth(originalThickness * 2);
				canvas.drawLine(
					posX - linePaint().getStrokeWidth() / 2, posY + staffLineHalfStrokeWidth,
					posX - linePaint().getStrokeWidth() / 2, posY - staffHeight() - staffLineHalfStrokeWidth,
					linePaint()
				);
				linePaint().setStrokeWidth(originalThickness);
				canvas.drawLine(
					posX - noteWidth() * 0.6f, posY + staffLineHalfStrokeWidth,
					posX - noteWidth() * 0.6f, posY - staffHeight() - staffLineHalfStrokeWidth,
					linePaint()
				);
			}
			case LIGHT_LIGHT:
			{
				canvas.drawLine(
					posX - linePaint().getStrokeWidth() / 2, posY + staffLineHalfStrokeWidth,
					posX - linePaint().getStrokeWidth() / 2, posY - staffHeight() - staffLineHalfStrokeWidth,
					linePaint()
				);
				canvas.drawLine(
					posX - noteWidth() * 0.6f, posY + staffLineHalfStrokeWidth,
					posX - noteWidth() * 0.6f, posY - staffHeight() - staffLineHalfStrokeWidth,
					linePaint()
				);
			}
		}
	}
}
