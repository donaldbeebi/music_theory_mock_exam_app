package edu.cuhk.csci3310.musictheoryapp.Question;

import edu.cuhk.csci3310.musictheoryapp.BuildConfig;

public class MultipleChoiceQuestion extends Question
{
    public enum OptionType
    {
        Text,
        Image
    }

    protected String[] m_Options;
    protected OptionType m_OptionType;

    public MultipleChoiceQuestion(int subNumber, QuestionGroup group,
                                  String topic, Description[] descriptions,
                                  String[] correctAnswer, String[] options, OptionType optionType)
    {
        super(subNumber, group, topic, descriptions, correctAnswer);
        if (BuildConfig.DEBUG && correctAnswer.length != 1) // never need MC questions with multiple answers
            throw new AssertionError("Assertion failed");
        m_Options = options;
        m_OptionType = optionType;
    }

    public String[] getOptions() { return m_Options; }

    public OptionType getOptionType() { return m_OptionType; }

    public void acceptVisitor(QuestionVisitor visitor) { visitor.visit(this); }
}