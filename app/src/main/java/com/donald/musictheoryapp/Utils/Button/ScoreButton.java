package com.donald.musictheoryapp.Utils.Button;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import com.donald.musictheoryapp.Music.MusicXML.Score;
import com.donald.musictheoryapp.Music.ScoreView.ScoreView;
import com.donald.musictheoryapp.R;

@SuppressLint("ViewConstructor")
public class ScoreButton extends QuestionButton
{
	public ScoreButton(Context context, Score score)
	{
		super(
			context,
			ContextCompat.getDrawable(context, R.drawable.shape_score_view_background)
		);
		ScoreView scoreView = new ScoreView(context);
		scoreView.setScore(score);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
		);
		params.addRule(CENTER_IN_PARENT);
		scoreView.setLayoutParams(params);
		addView(scoreView);
		setElevation(
			context.getResources().getDimension(R.dimen.image_button_elevation)
		);
	}
}
