package com.donald.musictheoryapp.QuestionBuilder;

import com.donald.musictheoryapp.Question.MultipleChoiceQuestion;
import com.donald.musictheoryapp.Question.Question;

public class MultipleChoiceQuestionBuilder extends QuestionBuilder
{
    private String[] m_Options;
    private MultipleChoiceQuestion.OptionType m_OptionType;

    @Override
    protected MultipleChoiceQuestion newQuestion()
    {
        return new MultipleChoiceQuestion(
            m_Number, m_Group, m_Descriptions,
            m_CorrectAnswers, m_Options, m_OptionType
        );
    }

    public void setOptions(String[] options) { m_Options = options; }

    public void setOptionType(MultipleChoiceQuestion.OptionType optionType)
    {
        m_OptionType = optionType;
    }

    @Override
    public Question build()
    {
        if(m_Options == null) throwError("Options");
        if(m_OptionType == null) throwError("OptionType");
        return super.build();
    }

    @Override
    public void reset()
    {
        m_Options = null;
        m_OptionType = null;
        super.reset();
    }
}
