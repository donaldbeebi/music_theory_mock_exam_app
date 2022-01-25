package com.donald.musictheoryapp.Utils.Button;

import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import com.donald.musictheoryapp.R;

public abstract class QuestionButton extends RelativeLayout
{
	private static final int PADDING = 16;
	private final GradientDrawable selectionBorder;
	private final Drawable background;
	private float ratio = 2f / 1f;

	protected QuestionButton(Context context, Drawable background)
	{
		super(context);
		this.background = background;
		selectionBorder = (GradientDrawable) ContextCompat.getDrawable(
			context, R.drawable.shape_selection_border
		);
		assert selectionBorder != null;
		selectionBorder.setAlpha(0);

		LayerDrawable layerDrawable = new LayerDrawable(new Drawable[2]);
		layerDrawable.setDrawable(0, background);
		layerDrawable.setDrawable(1, selectionBorder);
		setBackground(layerDrawable);
		setPadding(PADDING, PADDING, PADDING, PADDING);
	}

	public void setStrokeColor(int color)
	{
		selectionBorder.setStroke(
			(int) getContext().getResources().getDimension(R.dimen.question_button_stroke_width),
			color,
			getContext().getResources().getDimension(R.dimen.question_button_stroke_dash_width),
			getContext().getResources().getDimension(R.dimen.question_button_stroke_dash_gap)
		);
	}

	@Override
	public void setSelected(boolean isSelected)
	{
		super.setSelected(isSelected);
		selectionBorder.setAlpha(
			isSelected ? 255 : 0
		);
	}

	public void setRatio(float ratio)
	{
		this.ratio = ratio;
		invalidate();
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

	protected Drawable background()
	{
		return background;
	}
}
