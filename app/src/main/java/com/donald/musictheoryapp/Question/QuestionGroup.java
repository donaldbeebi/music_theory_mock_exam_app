package com.donald.musictheoryapp.Question;

import com.donald.musictheoryapp.BuildConfig;

public class QuestionGroup
{
    private final int m_Number;
    private final Description[] m_Descriptions;
    private Question[] m_Questions;
    private final String m_Topic;

    private boolean m_Initialized;

    public QuestionGroup(int number, String topic, Description[] descriptions)
    {
        m_Number = number;
        m_Topic = topic;
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

    public Question getQuestion(int index) { return m_Questions[index]; }

    public Question[] getQuestions() { return m_Questions; }

    public String getTopic() { return m_Topic; }

    public int getTotalScore()
    {
        int score = 0;
        for(Question question : m_Questions)
            score += question.getScore();
        return score;
    }
}
