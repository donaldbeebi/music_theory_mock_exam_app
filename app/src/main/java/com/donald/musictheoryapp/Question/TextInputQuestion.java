package com.donald.musictheoryapp.Question;

import com.donald.musictheoryapp.Utils.JSONArrayUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

// TODO: 1 ANSWER PER TEXT INPUT? OR NOT?

public class TextInputQuestion extends Question
{
    // TODO: CHANGE TO INT
    public enum InputType
    {
        Text,
        Number
    }

    public static class Answer implements Question.Answer
    {
        public String userAnswer;
        public String correctAnswer;

        public Answer(String userAnswer, String correctAnswer)
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
                object.isNull("user_answer") ? null : object.getString("user_answer"),
                object.getString("correct_answer")
            );
        }

        @Override
        public boolean correct()
        {
            return userAnswer != null && userAnswer.equals(correctAnswer);
        }
    }

    public InputType inputType;
    public Answer[] answers;

    public TextInputQuestion() {}

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
    public int maxPoints()
    {
        return answers.length;
    }

    @Override
    public void acceptVisitor(QuestionVisitor visitor) { visitor.visit(this); }

    public static TextInputQuestion fromJSON(JSONObject object, QuestionGroup group) throws JSONException
    {
        TextInputQuestion question = new TextInputQuestion();
        question.number = object.getInt("number");
        question.group = group;
        question.descriptions = JSONArrayUtil.descriptions(object);
        question.inputType = InputType.values()[object.getInt("input_type")];
        JSONArray answerJSONArray = object.getJSONArray("answers");
        question.answers = new Answer[answerJSONArray.length()];
        for(int i = 0; i < question.answers.length; i++)
        {
            question.answers[i] = Answer.fromJSON(answerJSONArray.getJSONObject(i));
        }
        return question;
    }
}
