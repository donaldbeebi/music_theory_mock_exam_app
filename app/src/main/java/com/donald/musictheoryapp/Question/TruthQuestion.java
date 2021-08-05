package com.donald.musictheoryapp.Question;

import com.donald.musictheoryapp.BuildConfig;

public class TruthQuestion extends Question
{
    public static final String TRUE_ANSWER = "true";
    public static final String FALSE_ANSWER = "false";

    protected String m_Statement;

    public TruthQuestion(int subNumber, QuestionGroup group, Description[] descriptions,
                         String[] correctAnswer, String statement)
    {
        super(subNumber, group, descriptions, correctAnswer);

        if (BuildConfig.DEBUG && correctAnswer.length != 1)
            throw new AssertionError("Assertion failed");

        m_Statement = statement;
    }

    public String getStatement() { return m_Statement; }

    public void acceptVisitor(QuestionVisitor visitor) { visitor.visit(this); }
}
