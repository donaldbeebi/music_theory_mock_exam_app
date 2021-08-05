package com.donald.musictheoryapp.AbstractQuestionGenerator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.donald.musictheoryapp.Question.MultipleChoiceQuestion;
import com.donald.musictheoryapp.Question.Question;
import com.donald.musictheoryapp.QuestionBuilder.MultipleChoiceQuestionBuilder;
import com.donald.musictheoryapp.QuestionBuilder.QuestionBuilder;
import com.donald.musictheoryapp.Utils.ArrayShuffler;

public abstract class MultipleChoiceQuestionGenerator extends QuestionGenerator
{
    protected final int NUMBER_OF_OPTIONS;
    private int m_IndexOfCorrectOption;
    private int m_CurrentWrongOptionToSet;

    private String[] m_Options;
    private final MultipleChoiceQuestion.OptionType m_OptionType;

    private final MultipleChoiceQuestionBuilder m_Builder;

    protected MultipleChoiceQuestionGenerator(String topic, int numberOfPossibleAnswers,
                                              MultipleChoiceQuestion.OptionType optionType,
                                              int numberOfSubQuestionsPerGeneration,
                                              SQLiteDatabase db, Context context)
    {
        super(topic, 1, numberOfSubQuestionsPerGeneration, db, context);
        NUMBER_OF_OPTIONS = numberOfPossibleAnswers;
        m_OptionType = optionType;
        m_Builder = new MultipleChoiceQuestionBuilder();
    }

    @Override
    protected void onInitialize()
    {
        super.onInitialize();
        m_IndexOfCorrectOption = 0;
        m_CurrentWrongOptionToSet = 1;
        m_Options = new String[NUMBER_OF_OPTIONS];
    }

    @Override
    protected final void onGenerate()
    {
        setUpData();
        shuffleOptions();
        super.addCorrectAnswer(String.valueOf(m_IndexOfCorrectOption + 1));
    }

    protected void shuffleOptions()
    {
        // shuffling the answers
        int[] dispositions = ArrayShuffler.shuffle(m_Options);
        m_IndexOfCorrectOption += dispositions[m_IndexOfCorrectOption];
    }

    @Override
    protected final QuestionBuilder getBuilder() { return m_Builder; }

    @Override
    protected final Question onBuildQuestion()
    {
        m_Builder.setOptions(m_Options);
        m_Builder.setOptionType(m_OptionType);
        return super.onBuildQuestion();
    }

    // setters
    @Override
    protected void addCorrectAnswer(String correctAnswer) { m_Options[0] = correctAnswer; }
    //protected void setCorrectOption(String correctOption) { m_Options[0] = correctOption; }
    protected void addWrongOption(String wrongOption)
    {
        if(m_CurrentWrongOptionToSet >= m_Options.length)
            throw new IllegalStateException("You are adding too many options.");
        m_Options[m_CurrentWrongOptionToSet++] = wrongOption;
    }
    protected void setWrongOptions(String[] wrongOptions)
    {
        if(wrongOptions.length > m_Options.length - 1)
            throw new IllegalArgumentException("You are adding too many options.");
        System.arraycopy(wrongOptions, 0, m_Options, 1, wrongOptions.length);
        m_CurrentWrongOptionToSet += wrongOptions.length;
    }

    // getters
    protected String[] getOptions() { return m_Options; }
    protected MultipleChoiceQuestion.OptionType getOptionType() { return m_OptionType; }
}
