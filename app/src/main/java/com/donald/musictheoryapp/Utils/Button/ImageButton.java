package com.donald.musictheoryapp.Utils.Button;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.TypedValue;
import android.view.Gravity;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.donald.musictheoryapp.R;

@SuppressLint("ViewConstructor")
public class ImageButton extends QuestionButton
{
	public ImageButton(Context context, RoundedBitmapDrawable background)
	{
		super(context, background);
		TypedValue radius = new TypedValue();
		getResources().getValue(R.dimen.question_button_corner_radius, radius, true);
		background.setCornerRadius(radius.getDimension(getResources().getDisplayMetrics()));
		setElevation(context.getResources().getDimension(R.dimen.image_button_elevation));
	}
}
