package com.donald.musictheoryapp.Question;

import com.donald.musictheoryapp.Utils.JSONArrayUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CheckBoxQuestion extends Question
{
    public static class Answer implements Question.Answer
    {
        public Boolean userAnswer;
        public boolean correctAnswer;

        public Answer(Boolean userAnswer, boolean correctAnswer)
        {
            this.userAnswer = userAnswer;
            this.correctAnswer = correctAnswer;
        }

        public JSONObject toJSON() throws JSONException
        {
            JSONObject object = new JSONObject();
            object.put("user_answer", userAnswer == null ? JSONObject.NULL : userAnswer);
            object.put("correct_answer", correctAnswer);
            return object;
        }

        public static Answer fromJSON(JSONObject object) throws JSONException
        {
            return new Answer(
                object.isNull("user_answer") ? null : object.getBoolean("user_answer"),
                object.getBoolean("correct_answer")
            );
        }

        @Override
        public boolean correct()
        {
            return userAnswer != null && userAnswer == correctAnswer;
        }
    }

    public static final String CHECK_ANSWER = "check";
    public static final String CROSS_ANSWER = "cross";

    public Answer[] answers;

    public CheckBoxQuestion() {}

    @Override
    public int points()
    {
        int points = 0;
        for(Answer answer : answers)
        {
            if(answer.correct()) points++;
        }
        return points;
    }

    @Override
    public void acceptVisitor(QuestionVisitor visitor) { visitor.visit(this); }

    public static CheckBoxQuestion fromJSON(JSONObject object, QuestionGroup group) throws JSONException
    {
        CheckBoxQuestion question = new CheckBoxQuestion();
        question.number = object.getInt("number");
        question.group = group;
        question.descriptions = JSONArrayUtil.descriptions(object);
        JSONArray answerJSONArray = object.getJSONArray("answers");
        question.answers = new Answer[answerJSONArray.length()];
        for(int i = 0; i < question.answers.length; i++)
        {
            question.answers[i] = Answer.fromJSON(answerJSONArray.getJSONObject(i));
        }
        return question;
    }
}
