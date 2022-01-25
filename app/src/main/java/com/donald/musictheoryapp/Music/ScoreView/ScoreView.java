package com.donald.musictheoryapp.Music.ScoreView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;

import com.donald.musictheoryapp.Music.MusicXML.Key;
import com.donald.musictheoryapp.Music.MusicXML.Measure;
import com.donald.musictheoryapp.Music.MusicXML.Note;
import com.donald.musictheoryapp.Music.MusicXML.Part;
import com.donald.musictheoryapp.Music.MusicXML.Clef;
import com.donald.musictheoryapp.Music.MusicXML.Score;
import com.donald.musictheoryapp.R;

import java.util.Arrays;
import java.util.HashMap;

public class ScoreView extends View
{
	/*
	 * *****************
	 * INNER CLASS START
	 * *****************
	 */

	private static class ScoreViewOutline extends ViewOutlineProvider
	{
		int width;
		int height;

		private ScoreViewOutline(int width, int height) {
			this.width = width;
			this.height = height;
		}

		@Override
		public void getOutline(View view, Outline outline) {
			outline.setRect(0, 0, width, height);
		}
	}

	/*
	 * ***************
	 * INNER CLASS END
	 * ***************
	 */

	// TODO: IF THE SCORE ONLY HAS A CLEF, IT IS NOT CENTERED
	// TODO: THIS IS BECAUSE IF THE WIDTH IS LONGER THAN THE MIN RATIO, THE BODY RECT IS STRETCHED
	public static final int BASE_LINE_STAFF_POS = 0;
	public static final int MIDDLE_LINE_STAFF_POS = 4;
	public static final int TOP_LINE_STAFF_BOS = 8;

	private static final float NOTE_REL_WIDTH = 1f / 3.4f;
	private static final float NOTE_REL_MIN_SPACING = 1.2f;
	private static final float CLEF_REL_WIDTH = 1f / 1.4f;
	private static final float H_SPACING_REL_SIZE = 1f / 6f;
	private static final float H_PADDING_REL_SIZE = NOTE_REL_WIDTH * 1f;
	private static final float STAFF_REL_HEIGHT = 1f;
	private static final float STAFF_STEP_HALF_REL_HEIGHT = 1f / 8f;
	private static final float NOTE_HEIGHT = 1f / 4f;
	private static final float NOTE_ARROW_REL_HEAD_ROOM = 1f;

	private float fixedRatio = 1f;
	private boolean hasFixedRatio = false;

	private float glyphSize;
	private float topPadding;
	private float leftPadding;
	private float hSpacing;
	private float staffWidth;
	private float clefStaffWidth;
	private float measuresStaffWidth;
	private int reqWidth;
	private int reqHeight;

	// staves
	private final RectF currentHeaderRect;
	private final RectF currentBodyRect;

	// glyph widths
	private float clefWidth;
	private float noteWidth;

	// paints
	private final Paint textPaint;
	private final Paint glyphPaint;
	private final Paint linePaint;
	// DEBUG
	private final RectF viewRect;
	private final Paint debugPaint;

	// data
	private Score score;
	private final int[] signatureAlters;
	private final HashMap<Integer, Integer> measureAlters;

	// stamps
	private final SStaff sStaff;
	private final SClef sClef;
	private final SSignature sSignature;
	private final SNoteHead sNoteHead;
	private final SStem sStem;
	private final SFlag sFlag;
	private final SAccidental sAccidental;
	private final SBarline sBarline;
	private final SNoteArrow sNoteArrow;
	private final SLedgerLines sLedgerLines;

	public ScoreView(Context context)
	{
		this(context, null);
	}

	public ScoreView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		//setBackgroundResource(R.drawable.shape_score_view_background);
		//setElevation(
		//	context.getResources().getDimension(
		//		R.dimen.image_button_elevation
		//	)
		//);
		currentHeaderRect = new RectF();
		currentBodyRect = new RectF();

		/*
		 * PAINTS
		 */
		textPaint = new Paint();
		glyphPaint = new Paint();
		linePaint = new Paint();

		signatureAlters = new int[Note.Pitch.Step.NO_OF_STEPS];
		measureAlters = new HashMap<>();

		// DEBUG
		debugPaint = new Paint();
		debugPaint.setColor(Color.RED);
		debugPaint.setStyle(Paint.Style.STROKE);
		debugPaint.setStrokeWidth(5f);
		viewRect = new RectF();

		/*
		 * STAMPS
		 */
		sStaff = new SStaff(this);
		sClef = new SClef(this);
		sSignature = new SSignature(this);
		sNoteHead = new SNoteHead(this);
		sStem = new SStem(this);
		sFlag = new SFlag(this);
		sAccidental = new SAccidental(this);
		sBarline = new SBarline(this);
		sNoteArrow = new SNoteArrow(this);
		sLedgerLines = new SLedgerLines(this);
	}

	private void calculateSizes()
	{
		clefWidth = glyphSize * CLEF_REL_WIDTH;
		noteWidth = glyphSize * NOTE_REL_WIDTH;
		hSpacing = glyphSize * H_SPACING_REL_SIZE;
		staffWidth = reqWidth - leftPadding * 2;
		clefStaffWidth = glyphSize * CLEF_REL_WIDTH;
		measuresStaffWidth = reqWidth - leftPadding - hSpacing - clefStaffWidth - hSpacing - leftPadding;

		/*
		 * PAINTS
		 */
		textPaint.setTextSize(glyphSize / 2f);
		textPaint.setTypeface(getResources().getFont(R.font.bravura));

		glyphPaint.setStyle(Paint.Style.FILL);
		glyphPaint.setTextSize(glyphSize);
		glyphPaint.setTypeface(getResources().getFont(R.font.bravura));

		linePaint.setColor(Color.BLACK);
		linePaint.setStyle(Paint.Style.FILL);
		linePaint.setStrokeWidth(glyphSize / 30);

		// DEBUG
		debugPaint.setColor(Color.RED);
		debugPaint.setStyle(Paint.Style.STROKE);
		debugPaint.setStrokeWidth(5f);
	}

	private void calculateReqDimen(int givenWidth, int givenHeight, int widthMode)
	{
		int numberOfNotes = 0;
		int highestNotePos = 9;
		int lowestNotePos = -1;
		boolean hasNoteArrowNotation = false;
		{
			if (score != null)
			{
				for (Part part : score.parts)
				{
					for (Measure measure : part.measures)
					{
						for (Note note : measure.notes)
						{
							if(note.printObject())
							{
								numberOfNotes++;
								// ASSUMING ONLY ONE CLEF AND ONLY ONE LINE
								Clef clef = score.parts[0].measures[0].attributes.clefs[0];
								int staffPos = Clef.noteStaffPosition(note, clef);
								if (staffPos > highestNotePos)
								{
									highestNotePos = staffPos;
								} else if (staffPos < lowestNotePos)
								{
									lowestNotePos = staffPos;
								}
							}
							if(note.notations != null && note.notations.noteArrow != null)
							{
								Log.d("inside score view", "note arrow detected!");
								hasNoteArrowNotation = true;
							}
						}
					}
				}
			}
		}
		float topRelPadding = ((float) highestNotePos + 4 - 8) * STAFF_STEP_HALF_REL_HEIGHT;
		if(hasNoteArrowNotation) topRelPadding = Math.max(topRelPadding, NOTE_ARROW_REL_HEAD_ROOM);
		float bottomRelPadding = ((float) -(lowestNotePos - 4)) * STAFF_STEP_HALF_REL_HEIGHT;

		float minRelWidth;
		if(numberOfNotes == 0)
		{
			minRelWidth = H_PADDING_REL_SIZE +
				H_SPACING_REL_SIZE +
				CLEF_REL_WIDTH +
				H_SPACING_REL_SIZE +
				H_PADDING_REL_SIZE;
		}
		else
		{
			minRelWidth = H_PADDING_REL_SIZE +
				H_SPACING_REL_SIZE +
				CLEF_REL_WIDTH +
				H_SPACING_REL_SIZE +
				NOTE_REL_MIN_SPACING +
				NOTE_REL_WIDTH * numberOfNotes +
				NOTE_REL_MIN_SPACING * numberOfNotes +
				H_SPACING_REL_SIZE +
				H_PADDING_REL_SIZE;
		}
		float minRelHeight = topRelPadding + STAFF_REL_HEIGHT + bottomRelPadding ;
		float minReqRatio = minRelWidth / minRelHeight;
		float givenRatio = (float) givenWidth / (float) givenHeight;
		if(givenRatio > minReqRatio && widthMode == MeasureSpec.EXACTLY)
		{
			reqWidth = givenWidth;
			reqHeight = givenHeight;
			glyphSize = givenHeight / minRelHeight;
		}
		else
		{
			reqWidth = givenWidth;
			reqHeight = (int) ((float) givenWidth / minReqRatio);
			glyphSize = givenWidth / minRelWidth;
		}
		// TODO: BOTTOM PADDING ISN'T TAKEN CARE OF (WHAT IF BOTTOM NEEDS EXTRA SPACE IN FIXED RATIO?)
		// TODO: IF THAT'S THE CASE, THE TOP PADDING DOESN'T DECREASE
		if(hasFixedRatio)
		{
			float givenTopPadding = (givenHeight - STAFF_REL_HEIGHT * glyphSize) / 2;
			topPadding = Math.max(glyphSize * topRelPadding, givenTopPadding);
		}
		else
		{
			topPadding = glyphSize * topRelPadding;
		}
		leftPadding = glyphSize * H_PADDING_REL_SIZE;
	}

	public void setScore(Score score)
	{
		this.score = score;
		if(score.parts[0].measures[0].attributes == null)
			throw new IllegalArgumentException("The first measure of the score does not have " +
				"attributes.");
		postInvalidate();
	}

	public void setFixedRatio(float fixedRatio)
	{
		this.fixedRatio = fixedRatio;
		hasFixedRatio = true;
	}

	public void removeFixedRatio()
	{
		hasFixedRatio = false;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		float width = MeasureSpec.getSize(widthMeasureSpec);
		float height = MeasureSpec.getSize(heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if(hasFixedRatio)
		{
			float givenRatio = width / height;
			if(givenRatio < fixedRatio)
			{
				height = width / fixedRatio;
			}
			else
			{
				width = height * fixedRatio;
			}
			calculateReqDimen((int) width, (int) height, widthMode);
			calculateSizes();
			super.onMeasure(
				MeasureSpec.makeMeasureSpec((int) width, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec((int) height, MeasureSpec.EXACTLY)
			);
		}
		else if(widthMode == MeasureSpec.AT_MOST)
		{
			calculateReqDimen((int) width, (int) height, widthMode);
			calculateSizes();
			super.onMeasure(
				MeasureSpec.makeMeasureSpec((int) reqWidth, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec((int) reqHeight, MeasureSpec.EXACTLY)
			);
		}
		else
		{
			// calculate the needed ratio
			calculateReqDimen((int) width, (int) height, widthMode);
			calculateSizes();
			super.onMeasure(
				MeasureSpec.makeMeasureSpec((int) reqWidth, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec((int) reqHeight, MeasureSpec.EXACTLY)
			);
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if(score == null) return;
		// TODO: CONSIDER MULTIPLE PARTS AND MULTIPLE CLEFS
		Part currentPart = score.parts[0];
		Measure[] measures = currentPart.measures;
		Clef currentClef = measures[0].attributes.clefs[0];
		sClef.setClef(currentClef);
		Key currentKey = measures[0].attributes.key;
		sSignature.setKey(currentKey, currentClef);
		setAccidentalMemory(measures[0].attributes.key, signatureAlters);

		// TODO: FIX WHEN MEASURES_PER_LINE IS MORE THAN 1, AND THERE IS ONLY 1 MEASURE
		int measuresPerLine = 1;
		int numberOfLines = (measures.length + measuresPerLine - 1) / measuresPerLine;
		int firstMeasureIndex = 0;
		canvas.drawColor(Color.WHITE);

		// TODO: WHAT IF DIVISION CHANGED MID-WAY
		for(int lineIndex = 0; lineIndex < numberOfLines; lineIndex++)
		{
			// TODO: RIGHT NOW GLYPH SIZE IS THE SPACING BETWEEN STAVES, STORE IT AS A PROPER VARIABLE
			float currentStaffPosY =
				topPadding + (glyphSize + staffHeight()) * lineIndex;

			//sStaff.setWidth(width - hPadding * 2);
			sStaff.setWidth(staffWidth);
			currentHeaderRect.left = leftPadding + hSpacing;
			currentHeaderRect.top = currentStaffPosY;
			currentHeaderRect.right = currentHeaderRect.left +
				(currentClef.printObject ? clefWidth : 0f) +
				(currentKey.fifths == 0 ? 0f : hSpacing) +
				sSignature.getWidth();
			currentHeaderRect.bottom = currentHeaderRect.top + staffHeight();

			currentBodyRect.left = currentHeaderRect.right + hSpacing;
			currentBodyRect.top = currentStaffPosY;
			currentBodyRect.right = leftPadding + staffWidth;
			////currentBodyRect.right = currentBodyRect.left + measuresStaffWidth;
			currentBodyRect.bottom = currentBodyRect.top + staffHeight();

			// 1. drawing the staff lines
			sStaff.onDraw(canvas, leftPadding, currentStaffPosY);

			// 2. drawing the clef
			if(currentClef.printObject)
			{
				sClef.onDraw(canvas, currentHeaderRect.left, currentHeaderRect.top);
			}

			// 3. drawing the signature
			sSignature.onDraw(canvas, currentHeaderRect.left + clefWidth + noteWidth / 2,
				currentHeaderRect.top);

			int numberOfMeasuresThisLine =
				Math.min(measuresPerLine, measures.length - firstMeasureIndex);

			float currentPosX = currentBodyRect.left;
			float currentPosY = currentBodyRect.bottom;
			for(int measureIndex = 0; measureIndex < numberOfMeasuresThisLine; measureIndex++)
			{
				Measure measure = measures[firstMeasureIndex + measureIndex];
				if(measure.notes.length == 0) continue;
				Note[] notes = measure.notes;
				measureAlters.clear();
				/*
				 * VARIABLES
				 */
				// TODO: HANDLE WITHOUT NOTES
				// TODO: REDUCE THE SPACE AT THE BEGINNING AND END OF THE NOTES
				int totalDuration = 0;
				int shortestDuration = Integer.MAX_VALUE;
				for(Note note : notes)
				{
					if(note.staff == 1 && !note.chord)
					{
						int duration = note.duration;
						if (duration < shortestDuration) shortestDuration = duration;
						totalDuration += duration;
					}
				}
				float spacingPerDuration =
					(currentBodyRect.width() / (totalDuration + shortestDuration)) /
						numberOfMeasuresThisLine;

				/*
				 * DRAWING
				 */
				// 4. drawing the notes
				currentPosX += shortestDuration * spacingPerDuration;
				for(int noteIndex = 0; noteIndex < notes.length; noteIndex++)
				{
					Note note = notes[noteIndex];
					if(note.staff == 1 && note.printObject())
					{
						// TODO: CHORD NOTES' LEDGER LINES ARE BEING DRAWN TWICE
						glyphPaint.setColor(note.color());

						// a. note head
						if (note.pitch != null && note.type != Note.Type.NULL)
						{
							sNoteHead.setNote(note, currentClef);
							sNoteHead.onDraw(canvas, currentPosX, currentPosY);
							if (note.type > Note.Type.QUARTER)
							{
								sFlag.setNote(note, currentClef);
								sFlag.onDraw(canvas, currentPosX, currentPosY);

								sStem.setNote(note, currentClef);
								sStem.onDraw(canvas, currentPosX, currentPosY);
							}

							/*
							// b. accidental
							//if (note.pitch.alter != m_MeasureAccidentals[Note.Pitch.Step.FIFTHS[note.pitch.step]])
							Integer measureAlter = measureAlters
								.get(Note.Pitch.absoluteStep(note.pitch));
							//if (note.pitch.alter != m_SignatureAlters[Note.Pitch.Step.FIFTHS[note.pitch.step]] &&
							//	((measureAlter == null && note.pitch.alter != 0) ||
							//		(measureAlter != null && note.pitch.alter != measureAlter)))
							int signatureAlter = signatureAlters[Note.Pitch.Step.FIFTHS[note.pitch.step]];
							if(!((measureAlter != null && note.pitch.alter == measureAlter) ||
								(signatureAlter == note.pitch.alter && measureAlter == null)))
							{
								sAccidental.setNote(note, currentClef);
								sAccidental.onDraw(canvas, currentPosX, currentPosY);
								//m_MeasureAccidentals[Note.Pitch.Step.FIFTHS[note.pitch.step]] = note.pitch.alter;
								measureAlters.put(Note.Pitch.absoluteStep(note.pitch), note.pitch.alter);
							}

							 */

							// b. accidental
							if(note.accidental != Note.Accidental.NULL)
							{
								sAccidental.setNote(note, currentClef);
								sAccidental.onDraw(canvas, currentPosX, currentPosY);
							}

							// c. ledger lines
							sLedgerLines.setNote(note, currentClef);
							sLedgerLines.onDraw(canvas, currentPosX, currentPosY);
						}
					}
					// d. notations
					if(note.notations != null)
					{
						if(note.notations.noteArrow != null)
						{
							sNoteArrow.setLabel(note.notations.noteArrow.label);
							sNoteArrow.onDraw(canvas, currentPosX, currentPosY);
						}
					}
					if(noteIndex + 1 == notes.length ||
						(noteIndex + 1 < notes.length && !notes[noteIndex + 1].chord))
					{
						currentPosX += spacingPerDuration * note.duration;
					}
				}
				glyphPaint.setColor(Color.BLACK);

				// 5. draw the bar line
				if(measure.barline != null) sBarline.setStyle(measure.barline.barlineStyle);
				else sBarline.setStyle(Measure.Barline.BarStyle.REGULAR);
				sBarline.onDraw(canvas, currentPosX, currentPosY);

				// DEBUG
				//canvas.drawRect(m_CurrentHeaderRect, m_DebugPaint);
				//canvas.drawRect(m_CurrentBodyRect, m_DebugPaint);
				/*
				viewRect.left = 0;
				viewRect.top = 0;
				viewRect.right = getWidth();
				viewRect.bottom = getHeight();
				canvas.drawRect(viewRect, debugPaint);
				 */
			}
			firstMeasureIndex += measuresPerLine;
		}
	}

	protected float glyphSize() { return glyphSize; }
	protected float noteWidth() { return noteWidth; }
	protected float staffStepHeight() { return glyphSize / 8; }
	protected float staffHeight() { return (SStaff.NO_OF_LINES - 1) * staffStepHeight() * 2; }
	protected Paint textPaint() { return textPaint; }
	protected Paint glyphPaint() { return glyphPaint; }
	protected Paint linePaint() { return linePaint; }

	private static void setAccidentalMemory(Key key, int[] accidentalsMemory)
	{
		int signature = key.fifths;
		Arrays.fill(accidentalsMemory, 0);
		if(signature < 0)
		{
			for(int i = -1; i >= signature; i--)
			{
				accidentalsMemory[Math.floorMod(i, Note.Pitch.Step.NO_OF_STEPS)]--;
			}
		}
		if(signature > 0)
		{
			for(int i = 0; i < signature; i++)
			{
				accidentalsMemory[Math.floorMod(i, Note.Pitch.Step.NO_OF_STEPS)]++;
			}
		}
	}
}
