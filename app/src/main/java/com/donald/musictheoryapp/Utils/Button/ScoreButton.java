package com.donald.musictheoryapp.Utils.Button;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
		RelativeLayout.LayoutParams scoreParams = new RelativeLayout.LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
		);
		scoreParams.addRule(CENTER_IN_PARENT);
		scoreView.setLayoutParams(scoreParams);
		addView(scoreView);

		setElevation(
			context.getResources().getDimension(R.dimen.image_button_elevation)
		);
	}

	public void setNumber(int number)
	{
		TextView numberView = (TextView) LayoutInflater.from(getContext()).inflate(
			R.layout.part_button_number, this, false
		);
		numberView.setText(String.valueOf(number));
		RelativeLayout.LayoutParams numberParams = new RelativeLayout.LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
		);
		numberParams.addRule(ALIGN_PARENT_TOP);
		numberParams.addRule(ALIGN_PARENT_START);
		numberView.setLayoutParams(numberParams);
		addView(numberView);
	}
}
