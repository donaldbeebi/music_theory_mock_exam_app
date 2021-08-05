package edu.cuhk.csci3310.musictheoryapp.QuestionBuilder;

import edu.cuhk.csci3310.musictheoryapp.Question.TextInputQuestion;

public class TextInputQuestionBuilder
    extends QuestionBuilder<TextInputQuestionBuilder, TextInputQuestion>
{
    private TextInputQuestion.InputType m_InputType;

    @Override
    protected TextInputQuestion newQuestion()
    {
        return new TextInputQuestion(
            m_Number, m_Group, m_Topic,
            m_Descriptions, m_CorrectAnswer, m_InputType
        );
    }

    @Override
    protected TextInputQuestionBuilder getThis() { return this; }

    public static TextInputQuestionBuilder question()
    {
        return new TextInputQuestionBuilder();
    }

    public TextInputQuestionBuilder inputType(TextInputQuestion.InputType inputType)
    {
        m_InputType = inputType;
        return this;
    }

    @Override
    public TextInputQuestion build()
    {
        if(m_InputType == null) throwError("InputType");
        return super.build();
    }
}
