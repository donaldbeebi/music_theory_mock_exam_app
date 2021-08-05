package edu.cuhk.csci3310.musictheoryapp.Question;

import edu.cuhk.csci3310.musictheoryapp.BuildConfig;

public class QuestionGroup
{
    private final int m_Number;
    private final Description[] m_Descriptions;
    private Question[] m_Questions;
    private boolean m_Initialized;

    public QuestionGroup(int number, Description[] descriptions)
    {
        m_Number = number;
        m_Descriptions = descriptions;
        m_Initialized = false;
    }

    public void setQuestions(Question[] questions)
    {
        if (BuildConfig.DEBUG && m_Initialized) { throw new AssertionError("Assertion failed"); }
        m_Initialized = true;
        m_Questions = questions;
    }

    public int getNumber() { return m_Number; }

    public Description[] getDescriptions() { return m_Descriptions; }

    public Question[] getQuestions() { return m_Questions; }
}
