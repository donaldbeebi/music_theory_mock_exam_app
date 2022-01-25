package com.donald.musictheoryapp.Utils.Button;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import androidx.core.content.ContextCompat;

import com.donald.musictheoryapp.R;

public class ColorButton extends QuestionButton
{
	public ColorButton(Context context)
	{
		super(
			context,
			ContextCompat.getDrawable(context, R.drawable.background_question_button)
		);
	}

	public void setColor(int color)
	{
		background().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.ADD));
	}
}
