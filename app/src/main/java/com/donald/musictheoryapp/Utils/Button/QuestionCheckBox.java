package com.donald.musictheoryapp.Utils.Button;

import android.content.Context;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.donald.musictheoryapp.R;

public class QuestionCheckBox extends androidx.appcompat.widget.AppCompatButton
{
	private static final int[] STATE_CHECKED = { R.attr.state_checked };
	private static final int[] STATE_CORRECT = { R.attr.state_correct };

	private boolean isChecked = false;
	private boolean isCorrect = false;

	public QuestionCheckBox(Context context)
	{
		super(context);
		setBackgroundResource(R.drawable.background_check_box);
	}

	public void setChecked(boolean isChecked)
	{
		this.isChecked = isChecked;
		refreshDrawableState();
	}

	public void setCorrect(boolean isCorrect)
	{
		this.isCorrect = isCorrect;
		refreshDrawableState();
	}

	public boolean isChecked()
	{
		return isChecked;
	}

	public boolean isCorrect()
	{
		return isCorrect;
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace)
	{
		int space = 0;
		if(isChecked) space++;
		if(isCorrect) space++;
		final int[] drawableState = super.onCreateDrawableState(extraSpace + space);
		if(isChecked)
		{
			mergeDrawableStates(drawableState, STATE_CHECKED);
		}
		if(isCorrect)
		{
			mergeDrawableStates(drawableState, STATE_CORRECT);
		}
		return drawableState;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int size = Math.min(width, height);
		setMeasuredDimension(size, size);
	}
}
