package com.donald.musictheoryapp.Question;

public class CheckBoxQuestion extends Question
{
    public static final String CHECK_ANSWER = "check";
    public static final String CROSS_ANSWER = "cross";

    public CheckBoxQuestion(int subNumber, QuestionGroup group,
                            Description[] descriptions, String[] correctAnswer)
    {
        super(subNumber, group, descriptions, correctAnswer);
    }

    public void acceptVisitor(QuestionVisitor visitor) { visitor.visit(this); }
}
