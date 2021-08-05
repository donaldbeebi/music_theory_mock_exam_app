package com.donald.musictheoryapp.QuestionBuilder;

import com.donald.musictheoryapp.Question.CheckBoxQuestion;

public class CheckBoxQuestionBuilder
    extends QuestionBuilder
{
    @Override
    protected CheckBoxQuestion newQuestion()
    {
        return new CheckBoxQuestion(
            m_Number, m_Group,
            m_Descriptions, m_CorrectAnswers
        );
    }
}
