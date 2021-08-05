package com.donald.musictheoryapp.QuestionBuilder;

import com.donald.musictheoryapp.Question.Question;
import com.donald.musictheoryapp.Question.TruthQuestion;

public class TruthQuestionBuilder extends QuestionBuilder
{
    private String m_Statement;

    @Override
    protected TruthQuestion newQuestion()
    {
        return new TruthQuestion(
            m_Number, m_Group, m_Descriptions,
            m_CorrectAnswers, m_Statement
        );
    }

    public void setStatement(String statement) { m_Statement = statement; }

    @Override
    public Question build()
    {
        if(m_Statement == null) throwError("Statement");
        return super.build();
    }

    @Override
    public void reset()
    {
        m_Statement = null;
        super.reset();
    }
}
