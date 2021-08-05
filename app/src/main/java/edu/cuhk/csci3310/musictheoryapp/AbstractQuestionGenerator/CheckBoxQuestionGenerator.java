package edu.cuhk.csci3310.musictheoryapp.AbstractQuestionGenerator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import edu.cuhk.csci3310.musictheoryapp.Question.Question;
import edu.cuhk.csci3310.musictheoryapp.QuestionBuilder.CheckBoxQuestionBuilder;

public abstract class CheckBoxQuestionGenerator extends QuestionGenerator
{
    protected CheckBoxQuestionGenerator(String topic, int numberOfSubQuestionsPerGeneration,
                                        int numberOfAnswers, SQLiteDatabase db, Context context)
    {
        super(topic, numberOfSubQuestionsPerGeneration, numberOfAnswers, db, context);
    }

    @Override
    protected final void onGenerate() { setUpData(); }

    @Override
    protected final Question onBuildQuestion()
    {
        return CheckBoxQuestionBuilder
            .question()
            .number(getNumber())
            .group(getGroup())
            .topic(getTopic())
            .descriptions(getQuestionDescriptions())
            .correctAnswer(getCorrectAnswer())
            .build();
    }
}
