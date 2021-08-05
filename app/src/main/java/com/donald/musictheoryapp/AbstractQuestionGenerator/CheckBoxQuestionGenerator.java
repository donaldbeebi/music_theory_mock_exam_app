package com.donald.musictheoryapp.AbstractQuestionGenerator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.donald.musictheoryapp.Question.Question;
import com.donald.musictheoryapp.QuestionBuilder.CheckBoxQuestionBuilder;
import com.donald.musictheoryapp.QuestionBuilder.QuestionBuilder;

public abstract class CheckBoxQuestionGenerator extends QuestionGenerator
{
    private final CheckBoxQuestionBuilder m_Builder;

    protected CheckBoxQuestionGenerator(String topic, int numberOfSubQuestionsPerGeneration,
                                        int numberOfAnswers, SQLiteDatabase db, Context context)
    {
        super(topic, numberOfAnswers, numberOfSubQuestionsPerGeneration, db, context);
        m_Builder = new CheckBoxQuestionBuilder();
    }

    @Override
    protected final void onGenerate() { setUpData(); }

    @Override
    protected final QuestionBuilder getBuilder() { return m_Builder; }

    @Override
    protected final Question onBuildQuestion()
    {
        return super.onBuildQuestion();
    }
}
