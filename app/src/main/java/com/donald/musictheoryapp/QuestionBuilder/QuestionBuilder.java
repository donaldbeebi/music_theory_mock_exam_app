package com.donald.musictheoryapp.QuestionBuilder;

import androidx.annotation.CallSuper;

import com.donald.musictheoryapp.Question.Description;
import com.donald.musictheoryapp.Question.Question;
import com.donald.musictheoryapp.Question.QuestionGroup;

public abstract class QuestionBuilder
{
    protected int m_Number;
    protected QuestionGroup m_Group;
    protected Description[] m_Descriptions;
    protected String[] m_CorrectAnswers;

    protected QuestionBuilder() {  }
    protected abstract Question newQuestion();
    //protected abstract B getThis();

    protected void throwError(String field) { throw new AssertionError("Field '" + field + "' not initialized."); }

    public void setNumber(int number) { m_Number = number; }
    public void setGroup(QuestionGroup group) { m_Group = group; }
    public void setDescriptions(Description[] descriptions) { m_Descriptions = descriptions; }
    public void setCorrectAnswers(String[] correctAnswers) { m_CorrectAnswers = correctAnswers; }
    public Question build()
    {
        if(m_Number == 0) throwError("SubNumber");
        if(m_Descriptions == null) throwError("Descriptions");
        if(m_CorrectAnswers == null) throwError("CorrectAnswer");
        return newQuestion();
    }

    @CallSuper
    public void reset()
    {
        m_Number = 0;
        m_Descriptions = null;
        m_CorrectAnswers = null;
    }
}
