package com.donald.musictheoryapp.Question;

import com.donald.musictheoryapp.Utils.JSONArrayUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TruthQuestion extends Question
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

    public static final String TRUE_ANSWER = "true";
    public static final String FALSE_ANSWER = "false";

    public Answer answer;

    public TruthQuestion() {}

    @Override
    public int points()
    {
        if(answer.correct()) return 1;
        return 0;
    }

    @Override
    public void acceptVisitor(QuestionVisitor visitor) { visitor.visit(this); }

    public static TruthQuestion fromJSON(JSONObject object, QuestionGroup group) throws JSONException
    {
        TruthQuestion question = new TruthQuestion();
        question.number = object.getInt("number");
        question.group = group;
        question.descriptions = JSONArrayUtil.descriptions(object);
        question.answer = Answer.fromJSON(object.getJSONObject("answer"));
        return question;
    }
}
