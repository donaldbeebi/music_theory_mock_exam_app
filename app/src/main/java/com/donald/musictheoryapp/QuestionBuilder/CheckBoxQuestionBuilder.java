package com.donald.musictheoryapp.QuestionBuilder;

import com.donald.musictheoryapp.Question.CheckBoxQuestion;

public class CheckBoxQuestionBuilder
    extends QuestionBuilder<CheckBoxQuestionBuilder, CheckBoxQuestion>
{
    @Override
    protected CheckBoxQuestion newQuestion()
    {
        return new CheckBoxQuestion(
            m_Number, m_Group, m_Topic,
            m_Descriptions, m_CorrectAnswer
        );
    }

    @Override
    protected CheckBoxQuestionBuilder getThis() { return this; }

    public static CheckBoxQuestionBuilder question()
    {
        return new CheckBoxQuestionBuilder();
    }

    @Override
    public CheckBoxQuestion build()
    {
        return super.build();
    }
}
