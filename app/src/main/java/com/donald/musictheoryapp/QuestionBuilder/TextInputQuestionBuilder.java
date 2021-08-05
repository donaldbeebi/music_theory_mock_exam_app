package com.donald.musictheoryapp.QuestionBuilder;

import com.donald.musictheoryapp.Question.Question;
import com.donald.musictheoryapp.Question.TextInputQuestion;

public class TextInputQuestionBuilder
    extends QuestionBuilder
{
    private TextInputQuestion.InputType m_InputType;

    @Override
    protected TextInputQuestion newQuestion()
    {
        return new TextInputQuestion(
            m_Number, m_Group, m_Descriptions,
            m_CorrectAnswers, m_InputType
        );
    }

    public void setInputType(TextInputQuestion.InputType inputType) { m_InputType = inputType; }

    @Override
    public Question build()
    {
        if(m_InputType == null) throwError("InputType");
        return super.build();
    }

    @Override
    public void reset()
    {
        m_InputType = null;
        super.reset();
    }
}
