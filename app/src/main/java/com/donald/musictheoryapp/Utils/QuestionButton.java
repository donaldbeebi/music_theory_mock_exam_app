package com.donald.musictheoryapp.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;

import com.donald.musictheoryapp.R;

public class QuestionButton extends RelativeLayout
{
	private static final int[] STATE_CORRECT = { R.attr.state_correct };

	private float ratio = 2f / 1f;
	private boolean isCorrect = false;

	public QuestionButton(Context context)
	{
		super(context);
	}

	public void setCorrect(boolean isCorrect)
	{
		this.isCorrect = isCorrect;
		refreshDrawableState();
	}

	public boolean isCorrect()
	{
		return isCorrect;
	}

	public void setRatio(float ratio)
	{
		this.ratio = ratio;
		invalidate();
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		if(isCorrect)
		{
			final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
			mergeDrawableStates(drawableState, STATE_CORRECT);
			return drawableState;
		}
		return super.onCreateDrawableState(extraSpace);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		float width = MeasureSpec.getSize(widthMeasureSpec);
		float height = MeasureSpec.getSize(heightMeasureSpec);
		float givenRatio = width / height;
		if(givenRatio < ratio)
		{
			height = width / ratio;
		}
		else
		{
			width = height * ratio;
		}
		super.onMeasure(
			MeasureSpec.makeMeasureSpec((int) width, MeasureSpec.EXACTLY),
			MeasureSpec.makeMeasureSpec((int) height, MeasureSpec.EXACTLY)
		);
	}

	/*
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		Log.d("inside onLayout", t + " " + b);
		int vPadding = (b - t) / 8;
		Log.d("inside onLayout", String.valueOf(vPadding));
		setPadding(vPadding, vPadding, vPadding, vPadding);
		super.onLayout(changed, l, t, r, b);
	}

	 */
}
