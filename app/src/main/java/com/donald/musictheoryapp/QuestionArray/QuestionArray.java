package com.donald.musictheoryapp.QuestionArray;

import com.donald.musictheoryapp.Question.Question;
import com.donald.musictheoryapp.Question.QuestionGroup;

public class QuestionArray
{
    private final Question[] m_Questions;
    private final QuestionGroup[] m_Groups;

    public QuestionArray(Question[] questions, QuestionGroup[] groups)
    {
        m_Questions = questions;
        m_Groups = groups;
    }

    public Question[] getQuestions()
    {
        return m_Questions;
    }

    public Question getQuestion(int questionIndex)
    {
        return m_Questions[questionIndex];
    }

    public Question getQuestion(int groupIndex, int localQuestionIndex)
    {
        return m_Groups[groupIndex].getQuestions()[localQuestionIndex];
    }

    public int getNumberOfQuestions()
    {
        return m_Questions.length;
    }

    public int getNumberOfGroups() { return m_Groups.length; }
}
