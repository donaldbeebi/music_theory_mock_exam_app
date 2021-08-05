package com.donald.musictheoryapp.AbstractQuestionGenerator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.donald.musictheoryapp.Question.Question;
import com.donald.musictheoryapp.Question.TextInputQuestion;
import com.donald.musictheoryapp.QuestionBuilder.QuestionBuilder;
import com.donald.musictheoryapp.QuestionBuilder.TextInputQuestionBuilder;

public abstract class TextInputQuestionGenerator extends QuestionGenerator
{
    private final TextInputQuestion.InputType m_InputType;

    private final TextInputQuestionBuilder m_Builder;

    protected TextInputQuestionGenerator(String topic, TextInputQuestion.InputType inputType,
                                         int numberOfSubQuestionsPerGeneration, int numberOfAnswers,
                                         SQLiteDatabase db, Context context)
    {
        super(topic, numberOfAnswers, numberOfSubQuestionsPerGeneration, db, context);
        m_InputType = inputType;
        m_Builder = new TextInputQuestionBuilder();
    }

    @Override
    protected final void onGenerate() { setUpData(); }

    @Override
    protected final QuestionBuilder getBuilder() { return m_Builder; }

    @Override
    protected final Question onBuildQuestion()
    {
        m_Builder.setInputType(m_InputType);
        return super.onBuildQuestion();
    }

    // getters
    protected TextInputQuestion.InputType getInputType() { return m_InputType; }
}
