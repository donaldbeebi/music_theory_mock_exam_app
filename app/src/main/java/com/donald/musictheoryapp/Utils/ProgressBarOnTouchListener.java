package com.donald.musictheoryapp.Utils;

import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import com.donald.musictheoryapp.Music.MusicXML.Clef;
import com.donald.musictheoryapp.Music.MusicXML.Note;

public class ProgressBarOnTouchListener implements View.OnTouchListener
{
	private final SeekBar seekBar;
	private float initialX;
	private int initialProgress;

	public ProgressBarOnTouchListener(SeekBar seekBar)
	{
		this.seekBar = seekBar;
	}

	@Override
	public boolean onTouch(View view, MotionEvent event)
	{
		// TODO: IMPLEMENT APPLE-STYLE DRAG, FURTHER UP SLOWS DOWN DRAG
		int action = event.getActionMasked();
		switch(action)
		{
			case MotionEvent.ACTION_DOWN:
			{
				initialX = event.getX();
				initialProgress = seekBar.getProgress();
				return true;
			}
			case MotionEvent.ACTION_MOVE:
			{
				float currentX = event.getX();
				float distance = currentX - initialX;
				int newProgress =
					Math.min(
						Math.max(
							initialProgress + (int) (distance * 2.5f / (seekBar.getMax() - seekBar.getMin())),
							seekBar.getMin()
						),
						seekBar.getMax()
					);
				seekBar.setProgress(newProgress);
				return true;
			}
			case MotionEvent.ACTION_UP:
			{
				return true;
			}
			default:
			{
				return view.performClick();
			}
		}
	}
}
