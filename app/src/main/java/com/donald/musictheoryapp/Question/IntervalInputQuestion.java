package com.donald.musictheoryapp.Question;

import com.donald.musictheoryapp.Music.MusicXML.Note;
import com.donald.musictheoryapp.Music.MusicXML.Score;
import com.donald.musictheoryapp.Utils.JSONArrayUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class IntervalInputQuestion extends Question
{
	public static class Answer implements Question.Answer
	{
		public Note userAnswer;
		public Note correctAnswer;

		public Answer(Note userAnswer, Note correctAnswer)
		{
			this.userAnswer = userAnswer;
			this.correctAnswer = correctAnswer;
		}

		@Override
		public boolean correct()
		{
			return userAnswer.equals(correctAnswer);
		}

		public static Answer fromJSON(JSONObject object) throws JSONException
		{
			return new Answer(
				object.isNull("user_answer") ? null : Note.fromJSON(object.getJSONObject("user_answer")),
				Note.fromJSON(object.getJSONObject("correct_answer"))
			);
		}
	}

	public Answer answer;
	public Score score;
	public Note correctNote;
	public String requiredInterval;

	public IntervalInputQuestion() {}

	@Override
	public int points()
	{
		Note[] notes = score.parts()[0].measures()[0].notes();
		if(notes[1].equals(correctNote))
		{
			return 1;
		}
		else return 0;
	}

	public Score score() { return score; }
	public String requiredInterval() { return requiredInterval; }

	@Override
	public void acceptVisitor(QuestionVisitor visitor) { visitor.visit(this); }

	public static IntervalInputQuestion fromJSON(JSONObject object, QuestionGroup group)
		throws JSONException, IOException, XmlPullParserException
	{
		IntervalInputQuestion question = new IntervalInputQuestion();
		question.number = object.getInt("number");
		question.group = group;
		question.descriptions = JSONArrayUtil.descriptions(object);
		question.score = Score.fromXML(object.getString("score"));
		if(question.score.parts().length != 1 ||
			question.score.parts()[0].measures().length != 1 ||
			question.score.parts()[0].measures()[0].notes().length != 3)
		{
			throw new IllegalStateException("Invalid score from question.");
		}
		question.answer = Answer.fromJSON(object.getJSONObject("answer"));
		question.requiredInterval = object.getString("required_interval");
		/*
		return new IntervalInputQuestion(
			object.getInt("number"),
			group,
			JSONArrayUtil.descriptions(object),
			//Note.Pitch.fromJSON(object.getJSONObject("correct_pitch")),
			//Note.Pitch.fromJSON(object.getJSONObject("given_pitch")),
			Score.fromXML(object.getString("score")),
			Note.fromJSON(object.getJSONObject("correct_note")),
			object.getString("required_interval")
			//Clef.fromJSON(object.getJSONObject("clef")),
			//Key.fromJSON(object.getJSONObject("key"))
		);

		 */
		return question;
	}
}
