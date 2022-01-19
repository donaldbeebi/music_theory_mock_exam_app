package com.donald.musictheoryapp.Music.ScoreView;

import android.graphics.Canvas;

import com.donald.musictheoryapp.Music.MusicXML.Clef;
import com.donald.musictheoryapp.Music.MusicXML.Key;
import com.donald.musictheoryapp.Music.MusicXML.Note;

class SSignature extends ScoreStamp
{
	private static class SSignatureAccidental extends ScoreStamp
	{
		private static final int[] BASS_SHARP_POS = { 6, 3, 7, 4, 1, 5, 2 };
		private static final int[] BASS_FLAT_POS = { 2, 5, 1, 4, 0, 3, -1 };
		private static final int[] TENOR_SHARP_POS = { 2, 6, 3, 7, 4, 8, 5 };
		private static final int[] TENOR_FLAT_POS = { 5, 8, 4, 7, 3, 6, 2 };
		private static final int[] ALTO_SHARP_POS = { 7, 4, 8, 5, 2, 6, 3 };
		private static final int[] ALTO_FLAT_POS = { 3, 6, 2, 5, 1, 4, 0 };
		private static final int[] TREBLE_SHARP_POS = { 8, 5, 9, 6, 3, 7, 4 };
		private static final int[] TREBLE_FLAT_POS = { 4, 7, 3, 6, 2, 5, 1 };

		private char m_Glyph;
		private float m_VerticalOffsetOnStaff;
		private int[] m_CurrentAccidentalPositions;

		SSignatureAccidental(ScoreView view)
		{
			super(view);
			m_Glyph = U.ACC_NATURAL;
			m_VerticalOffsetOnStaff = 0;
		}

		protected void setKey(Key key, Clef clef)
		{
			boolean sharpSignature = key.fifths > 0;
			if(clef.sign == Clef.Sign.F && clef.line == 4)
			{
				// bass
				m_CurrentAccidentalPositions = sharpSignature ? BASS_SHARP_POS : BASS_FLAT_POS;
			}
			else if(clef.sign == Clef.Sign.C && clef.line == 3)
			{
				// tenor
				m_CurrentAccidentalPositions = sharpSignature ? TENOR_SHARP_POS : TENOR_FLAT_POS;
			}
			else if(clef.sign == Clef.Sign.C && clef.line == 4)
			{
				// alto
				m_CurrentAccidentalPositions = sharpSignature ? ALTO_SHARP_POS : ALTO_FLAT_POS;
			}
			else if(clef.sign == Clef.Sign.G && clef.line == 2)
			{
				// treble
				m_CurrentAccidentalPositions = sharpSignature ? TREBLE_SHARP_POS : TREBLE_FLAT_POS;
			}
		}

		protected void setAccidental(int nthAccidental, int alter)
		{
			m_VerticalOffsetOnStaff =
				(m_CurrentAccidentalPositions[nthAccidental])
				* staffStepHeight();
			switch(alter)
			{
				case -2:
					m_Glyph = U.ACC_D_FLAT;
					break;
				case -1:
					m_Glyph = U.ACC_FLAT;
					break;
				case 0:
					m_Glyph = U.ACC_NATURAL;
					break;
				case 1:
					m_Glyph = U.ACC_SHARP;
					break;
				case 2:
					m_Glyph = U.ACC_D_SHARP;
					break;
				default:
					throw new IllegalArgumentException(alter + " + is not a valid alter.");
			}
		}

		@Override
		protected void onDraw(Canvas canvas, float posX, float posY)
		{
			canvas.drawText(String.valueOf(m_Glyph),
				posX,
				posY + glyphSize() - m_VerticalOffsetOnStaff,
				glyphPaint()
			);
		}
	}

	private final SSignatureAccidental m_SAccidental;
	private Key m_Key;

	SSignature(ScoreView view)
	{
		super(view);
		m_SAccidental = new SSignatureAccidental(view);
	}

	protected void setKey(Key key, Clef clef)
	{
		m_Key = key;
		m_SAccidental.setKey(key, clef);
	}

	protected float getWidth()
	{
		if(m_Key == null) throw new IllegalStateException("Key is not set.");
		return Math.abs(m_Key.fifths) * noteWidth();
	}

	@Override
	protected void onDraw(Canvas canvas, float posX, float posY)
	{
		if(m_Key.fifths == 0) return;

		int firstStep;
		int factor;
		if(m_Key.fifths > 0)
		{
			// sharp signature
			firstStep = Note.Pitch.Step.STEPS_IN_FIFTHS[0];
			factor = 1;
			//signatureStartFifths = m_Key.fifths + 14;
			//nthNoteInSignature =
			//	Math.floorMod(Note.Pitch.Step.FIFTHS[firstStep] - signatureStartFifths,
			//		Note.Pitch.Step.NO_OF_STEPS);
		}
		else
		{
			// flat signature
			firstStep = Note.Pitch.Step.STEPS_IN_FIFTHS[Note.Pitch.Step.STEPS_IN_FIFTHS.length - 1];
			factor = -1;
			//signatureStartFifths = m_Key.fifths + 14;
			//nthNoteInSignature =
			//	Math.floorMod(Note.Pitch.Step.FIFTHS[firstStep] - signatureStartFifths,
			//		Note.Pitch.Step.NO_OF_STEPS);
		}
		// for iterating through the scale
		int signatureStartFifths = m_Key.fifths + 14;
		// initializing it to step F (i.e. Fbb, Fb, F, F#, or F##)
		int nthNoteInSignature = Math.floorMod(Note.Pitch.Step.FIFTHS[firstStep] - signatureStartFifths,
			Note.Pitch.Step.NO_OF_STEPS);

		// TODO: PERHAPS FIGURE OUT HOW TO PROCEDURALLY CALCULATE THE POSITIONS OF ACCIDENTALS
		int currentPosX = 0;
		for(int i = 0; i < 7; i++)
		{
			int currentFifths = Math.floorMod(nthNoteInSignature + i * factor, Note.Pitch.Step.NO_OF_STEPS) +
				signatureStartFifths;
			int currentAlter = currentFifths / Note.Pitch.Step.NO_OF_STEPS - 2;
			if(currentAlter == 0) break;
			m_SAccidental.setAccidental(i, currentFifths / Note.Pitch.Step.NO_OF_STEPS - 2);
			m_SAccidental.onDraw(canvas, posX + currentPosX, posY);
			currentPosX += noteWidth();
		}
	}

	/*
		int currentPosX = 0;
		for(int i = 0; i < 7; i++)
		{
			int currentFifths = (nthNoteInSignature + i) % Note.Pitch.Step.NO_OF_STEPS +
				signatureStartFifths;
			int currentAccidentalPosition =
				(nthPositionFromBottom + 4 * i) % 9 +
					bottomNotePosition;
			m_SAccidental.setAccidental(currentAccidentalPosition,
				currentFifths / Note.Pitch.Step.NO_OF_STEPS - 2, m_Clef);
			m_SAccidental.onDraw(canvas, posX + currentPosX, posY);
			currentPosX += noteWidth();
		}

		 */
}
