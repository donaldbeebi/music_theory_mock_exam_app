package com.donald.musictheoryapp.QuestionBuilder;

import com.donald.musictheoryapp.Question.MultipleChoiceQuestion;

public class MultipleChoiceQuestionBuilder
    extends QuestionBuilder<MultipleChoiceQuestionBuilder, MultipleChoiceQuestion>
{
    private String[] m_Options;
    private MultipleChoiceQuestion.OptionType m_OptionType;

    @Override
    protected MultipleChoiceQuestion newQuestion()
    {
        return new MultipleChoiceQuestion(
            m_Number, m_Group, m_Topic, m_Descriptions,
            m_CorrectAnswer, m_Options, m_OptionType
        );
    }

    @Override
    protected MultipleChoiceQuestionBuilder getThis() { return this; }

    public static MultipleChoiceQuestionBuilder question()
    {
        return new MultipleChoiceQuestionBuilder();
    }

    public MultipleChoiceQuestionBuilder options(String[] options)
    {
        m_Options = options;
        return this;
    }

    public MultipleChoiceQuestionBuilder optionType(MultipleChoiceQuestion.OptionType optionType)
    {
        m_OptionType = optionType;
        return this;
    }

    @Override
    public MultipleChoiceQuestion build()
    {
        if(m_Options == null) throwError("Options");
        if(m_OptionType == null) throwError("OptionType");
        return super.build();
    }
}
