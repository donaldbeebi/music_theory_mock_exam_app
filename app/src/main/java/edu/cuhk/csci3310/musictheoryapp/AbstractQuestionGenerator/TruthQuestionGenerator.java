package edu.cuhk.csci3310.musictheoryapp.AbstractQuestionGenerator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import edu.cuhk.csci3310.musictheoryapp.Question.Question;
import edu.cuhk.csci3310.musictheoryapp.QuestionBuilder.TruthQuestionBuilder;

public abstract class TruthQuestionGenerator extends QuestionGenerator
{
    private static final boolean IS_COLLAPSIBLE = false;

    protected String m_Statement;

    protected TruthQuestionGenerator(String topic,
                                     int numberOfSubQuestionsPerGeneration,
                                     SQLiteDatabase db, Context context)
    {
        super(topic, numberOfSubQuestionsPerGeneration, 1, db, context);
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
    protected final Question onBuildQuestion()
    {
        return TruthQuestionBuilder
            .question()
            .number(getNumber())
            .group(getGroup())
            .topic(getTopic())
            .descriptions(getQuestionDescriptions())
            .correctAnswer(getCorrectAnswer())
            .statement(m_Statement)
            .build();
    }

    // setters
    protected void setStatement(String statement) { m_Statement = statement; }
}
