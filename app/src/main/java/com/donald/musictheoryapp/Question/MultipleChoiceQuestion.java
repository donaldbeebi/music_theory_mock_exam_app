package com.donald.musictheoryapp.Question;

import com.donald.musictheoryapp.Utils.JSONArrayUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class MultipleChoiceQuestion extends Question
{
    // TODO: USE INTS
    public enum OptionType
    {
        TEXT,
        IMAGE,
        SCORE
    }

    public static class Answer implements Question.Answer
    {
        public int userAnswer;
        public int correctAnswer;

        public Answer(int userAnswer, int correctAnswer)
        {
            this.userAnswer = -1;
            this.correctAnswer = correctAnswer;
        }

        @Override
        public boolean correct()
        {
            return userAnswer == correctAnswer;
        }

        public static Answer fromJSON(JSONObject object) throws JSONException
        {
            return new Answer(
                object.isNull("user_answer") ? -1 : object.getInt("user_answer"),
                object.getInt("correct_answer")
            );
        }
    }

    public Answer answer;
    public String[] options;
    public OptionType optionType;

    public MultipleChoiceQuestion() {}

    @Override
    public int points()
    {
        if(answer.correct()) return 1;
        return 0;
    }

    @Deprecated
    public String[] getOptions() { return options; }

    @Deprecated
    public OptionType getOptionType() { return optionType; }

    public void acceptVisitor(QuestionVisitor visitor) { visitor.visit(this); }

    public static MultipleChoiceQuestion fromJSON(JSONObject object, QuestionGroup group) throws JSONException
    {
        MultipleChoiceQuestion question = new MultipleChoiceQuestion();
        question.number = object.getInt("number");
        question.group = group;
        question.descriptions = JSONArrayUtil.descriptions(object);
        question.optionType = OptionType.values()[object.getInt("option_type")];
        question.options = JSONArrayUtil.options(object);
        question.answer = Answer.fromJSON(object.getJSONObject("answer"));
        return question;
    }
}