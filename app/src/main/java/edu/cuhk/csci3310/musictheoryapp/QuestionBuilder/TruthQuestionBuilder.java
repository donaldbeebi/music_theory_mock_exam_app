package edu.cuhk.csci3310.musictheoryapp.QuestionBuilder;

import edu.cuhk.csci3310.musictheoryapp.Question.TruthQuestion;

public class TruthQuestionBuilder extends QuestionBuilder<TruthQuestionBuilder, TruthQuestion>
{
    private String m_Statement;

    @Override
    protected TruthQuestion newQuestion()
    {
        return new TruthQuestion(
            m_Number, m_Group, m_Topic, m_Descriptions,
            m_CorrectAnswer, m_Statement
        );
    }

    @Override
    protected TruthQuestionBuilder getThis() { return this; }

    public static TruthQuestionBuilder question()
    {
        return new TruthQuestionBuilder();
    }

    public TruthQuestionBuilder statement(String statement)
    {
        m_Statement = statement;
        return this;
    }

    @Override
    public TruthQuestion build()
    {
        if(m_Statement == null) throwError("Statement");
        return super.build();
    }
}
