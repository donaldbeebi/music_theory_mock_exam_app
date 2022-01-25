package com.donald.musictheoryapp;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.donald.musictheoryapp.Music.MusicXML.Clef;
import com.donald.musictheoryapp.Music.MusicXML.Measure;
import com.donald.musictheoryapp.Music.MusicXML.Note;
import com.donald.musictheoryapp.Music.MusicXML.Score;
import com.donald.musictheoryapp.Music.ScoreView.ScoreView;
import com.donald.musictheoryapp.Question.IntervalInputQuestion;

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

	public PanelOnTouchListener(ScoreView scoreView, IntervalInputQuestion question)
	{
		Score score = question.score();
		this.inputNote = score.parts()[0].measures()[0].notes()[1];
		this.scoreView = scoreView;
		question.answer.userAnswer = inputNote;
		Measure measure = score.parts()[0].measures()[0];
		firstNoteAbsStep = measure.notes()[0].pitch().absStep();
		//firstNoteAbsStep = measure.notes()[0].pitch().alter();
		firstNoteAlter = measure.notes()[0].pitch().alter();
		clef = measure.attributes.clefs[0];

		measureAlters = new int[Note.Pitch.Step.NO_OF_STEPS];
		Arrays.fill(measureAlters, 0);

		if(measure.attributes.key.fifths != 0)
		{
			int i = 0;
			int currentStep;
			int alter;
			int stepItDisplacement;
			if(measure.attributes.key.fifths > 0)
			{
				// sharping from F
				currentStep = Note.Pitch.Step.F;
				alter = 1;
				stepItDisplacement = 4;
			}
			else
			{
				// flatting from B
				currentStep = Note.Pitch.Step.B;
				alter = -1;
				stepItDisplacement = -3;
			}
			int fifths = Math.abs(measure.attributes.key.fifths);
			while(i < fifths)
			{
				measureAlters[currentStep] += alter;
				i++;
				currentStep = Math.floorMod(currentStep + stepItDisplacement, Note.Pitch.Step.NO_OF_STEPS);
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
					scoreView.postInvalidate();
				}
				return true;
			}
			case MotionEvent.ACTION_MOVE:
			{
				float currentY = event.getY();
				float distance = currentY - initialY;
				int absStepUpperLimit = Clef.Sign.BASE_NOTE_POS_BY_SIGN[clef.sign] + NOTE_POS_UPPER_LIMIT - (clef.line - 1) * 2;
				int absStepLowerLimit = Clef.Sign.BASE_NOTE_POS_BY_SIGN[clef.sign] + NOTE_POS_LOWER_LIMIT - (clef.line - 1) * 2;
				int newAbsStep =
					Math.min(
						Math.max(
							initialAbsStep - (int) distance / 60,
							absStepLowerLimit
						),
						absStepUpperLimit
					);
				inputNote.pitch().setStep(Math.floorMod(newAbsStep, Note.Pitch.Step.NO_OF_STEPS));
				inputNote.pitch().setOctave(newAbsStep / Note.Pitch.Step.NO_OF_STEPS);
				scoreView.postInvalidate();
				return true;
			}
			default:
			{
				return view.performClick();
			}
		}
	}
}
