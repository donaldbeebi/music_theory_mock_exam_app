package com.donald.musictheoryapp.Music.ScoreView;

import android.graphics.Canvas;

// TODO: REMOVE STAFF HEIGHT
class SStaff extends ScoreStamp
{
	public static final int NO_OF_LINES = 5;

	private float width;

	protected SStaff(ScoreView view)
	{
		super(view);
		width = 0;
	}

	void setWidth(float width) { this.width = width; }

	protected float width() { return width; }

	@Override
	protected void onDraw(Canvas canvas, float posX, float posY)
	{
		for(int i = 0; i < NO_OF_LINES; i++)
		{
			canvas.drawLine(
				posX,
				posY + staffStepHeight() * 2 * i,
				posX + width,
				posY + staffStepHeight() * 2 * i,
				linePaint());
		}
	}
}
