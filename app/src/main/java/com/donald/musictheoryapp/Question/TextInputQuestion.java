package com.donald.musictheoryapp.Question;

public class TextInputQuestion extends Question
{
    public enum InputType
    {
        Text,
        Number
    }

    private final InputType m_InputType;

    public TextInputQuestion(int subNumber, QuestionGroup group,
                             String topic, Description[] descriptions,
                             String[] correctAnswer, InputType inputType)
    {
        super(subNumber, group, topic, descriptions, correctAnswer);
        m_InputType = inputType;
    }

    @Override
    public void acceptVisitor(QuestionVisitor visitor) { visitor.visit(this); }

    public InputType getInputType() { return m_InputType; }
}
