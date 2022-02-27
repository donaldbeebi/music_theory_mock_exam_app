package com.donald.musictheoryapp.Utils;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.donald.musictheoryapp.music.MusicXML.Clef;
import com.donald.musictheoryapp.music.MusicXML.Measure;
import com.donald.musictheoryapp.music.MusicXML.Note;
import com.donald.musictheoryapp.music.MusicXML.Pitch;
import com.donald.musictheoryapp.music.MusicXML.Score;
import com.donald.musictheoryapp.music.scoreview.ScoreView;
import com.google.android.material.math.MathUtils;

import java.util.Arrays;

public class PanelOnTouchListener implements View.OnTouchListener
{
	private final Note inputNote;
	private final ScoreView scoreView;
	private float initialY;
	private int initialAbsStep;
	private final int firstNoteAbsStep;
	private final int firstNoteAlter;
	private final int[] measureAlters;
	private final Clef clef;
	private static final int NOTE_POS_UPPER_LIMIT = 15;
	private static final int NOTE_POS_LOWER_LIMIT = -7;

	public PanelOnTouchListener(ScoreView scoreView, Score score)
	{
		inputNote = score.parts()[0].measures()[0].notes().get(1);
		this.scoreView = scoreView;
		//question.answer.userAnswer = inputNote;
		Measure measure = score.parts()[0].measures()[0];
		firstNoteAbsStep = measure.notes().get(0).pitch().absStep();
		firstNoteAlter = measure.notes().get(0).pitch().alter();
		clef = measure.getAttributes().getClefs()[0];

		measureAlters = new int[Pitch.Step.NO_OF_STEPS];
		Arrays.fill(measureAlters, 0);

		if(measure.getAttributes().getKey().getFifths() != 0)
		{
			int i = 0;
			int currentStep;
			int alter;
			int stepItDisplacement;
			if(measure.getAttributes().getKey().getFifths() > 0)
			{
				// sharping from F
				currentStep = Pitch.Step.F;
				alter = 1;
				stepItDisplacement = 4;
			}
			else
			{
				// flatting from B
				currentStep = Pitch.Step.B;
				alter = -1;
				stepItDisplacement = -3;
			}
			int fifths = Math.abs(measure.getAttributes().getKey().getFifths());
			while(i < fifths)
			{
				measureAlters[currentStep] += alter;
				i++;
				currentStep = MathUtils.floorMod(currentStep + stepItDisplacement, Pitch.Step.NO_OF_STEPS);
			}
		}
	}

	@Override
	public boolean onTouch(View view, MotionEvent event)
	{
		int action = event.getActionMasked();
		switch(action)
		{
			// TODO: WHAT IF ACTION_MOVE HAPPENS BEFORE ACTION_DOWN
			case MotionEvent.ACTION_DOWN:
			{
				initialY = event.getY();
				initialAbsStep = inputNote.pitch().absStep();
				return true;
			}
			case MotionEvent.ACTION_UP:
			{
				Log.d("inputNote alter before", String.valueOf(inputNote.pitch().alter()));
				float currentY = event.getY();
				float distance = currentY - initialY;
				if(event.getEventTime() - event.getDownTime() < 200d && Math.abs((int) distance / 60) < 1)
				{
					int accidental =
						((inputNote.accidental() + 1 + 1) % (Note.Accidental.NO_OF_ACCIDENTALS + 1)) - 1;
					inputNote.setAccidental(accidental);
					if(accidental == Note.Accidental.NULL)
					{
						if(inputNote.pitch().absStep() == firstNoteAbsStep)
						{
							inputNote.pitch().setAlter(firstNoteAlter);
						}
						else
						{
							inputNote.pitch().setAlter(measureAlters[inputNote.pitch().step()]);
						}
					}
					else
					{
						inputNote.pitch().setAlter(Note.Accidental.alter(accidental));
					}
					scoreView.invalidate();
					//scoreView.requestLayout();
					Log.d("inputNote alter after", String.valueOf(inputNote.pitch().alter()));
				}
				return true;
			}
			case MotionEvent.ACTION_MOVE:
			{
				float currentY = event.getY();
				float distance = currentY - initialY;
				int absStepUpperLimit = Clef.Sign.BASE_NOTE_POS_BY_SIGN[clef.getSign()] + NOTE_POS_UPPER_LIMIT - (clef.getLine() - 1) * 2;
				int absStepLowerLimit = Clef.Sign.BASE_NOTE_POS_BY_SIGN[clef.getSign()] + NOTE_POS_LOWER_LIMIT - (clef.getLine() - 1) * 2;
				int newAbsStep =
					Math.min(
						Math.max(
							initialAbsStep - (int) distance / 60,
							absStepLowerLimit
						),
						absStepUpperLimit
					);
				inputNote.pitch().setStep(MathUtils.floorMod(newAbsStep, Pitch.Step.NO_OF_STEPS));
				inputNote.pitch().setOctave(newAbsStep / Pitch.Step.NO_OF_STEPS);
				//scoreView.calculateReqDimen();
				scoreView.invalidate();
				return true;
			}
			default:
			{
				return view.performClick();
			}
		}
	}
}
