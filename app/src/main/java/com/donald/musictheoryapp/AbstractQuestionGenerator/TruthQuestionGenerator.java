package com.donald.musictheoryapp.AbstractQuestionGenerator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.donald.musictheoryapp.Question.Question;
import com.donald.musictheoryapp.QuestionBuilder.QuestionBuilder;
import com.donald.musictheoryapp.QuestionBuilder.TruthQuestionBuilder;

public abstract class TruthQuestionGenerator extends QuestionGenerator
{
    private String m_Statement;

    private final TruthQuestionBuilder m_Builder;

    protected TruthQuestionGenerator(String topic,
                                     int numberOfSubQuestionsPerGeneration,
                                     SQLiteDatabase db, Context context)
    {
        super(topic, 1, numberOfSubQuestionsPerGeneration, db, context);
        m_Builder = new TruthQuestionBuilder();
    }

    @Override
    protected final void onGenerate()
    {
        setUpData();
    }

    @Override
    protected final void onInitialize()
    {
        super.onInitialize();
        m_Statement = null;
    }

    @Override
    protected final QuestionBuilder getBuilder() { return m_Builder; }

    @Override
    protected final Question onBuildQuestion()
    {
        m_Builder.setStatement(m_Statement);
        return super.onBuildQuestion();
    }

    // setters
    protected void setStatement(String statement) { m_Statement = statement; }
}
